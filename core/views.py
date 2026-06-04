import csv
from datetime import datetime
from django.shortcuts import render, redirect, get_object_or_404
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import user_passes_test
from django.db import transaction
from django.http import JsonResponse, HttpResponse
from django.contrib import messages
from .models import Student, Position, Candidate, Vote, ElectionSettings

def is_staff(user):
    return user.is_authenticated and user.is_staff

def get_settings():
    settings = ElectionSettings.objects.first()
    if not settings:
        settings = ElectionSettings.objects.create(
            election_name="VVS Election",
            academic_year="2024-2025",
            is_open=False,
            station_pin="1234"
        )
    return settings

def home_redirect(request):
    return redirect('setup')

def setup(request):
    if request.method == 'POST':
        action = request.POST.get('action')
        if action == 'host':
            return redirect('admin_login')
        elif action == 'station':
            return redirect('setup_house')
    return render(request, 'core/setup.html')

def setup_house(request):
    if request.method == 'POST':
        house = request.POST.get('house')
        pin = request.POST.get('pin')
        settings = get_settings()
        if pin == settings.station_pin:
            request.session['station_house'] = house
            return redirect('vote_login')
        else:
            messages.error(request, "Invalid Station PIN")
    return render(request, 'core/setup_house.html')

# Voter Views
def check_station(request):
    return request.session.get('station_house')

def vote_login(request):
    station_house = check_station(request)
    if not station_house:
        return redirect('setup')
    
    settings = get_settings()
    
    if request.method == 'POST':
        enrollment_number = request.POST.get('enrollment_number')
        try:
            student = Student.objects.get(enrollment_number=enrollment_number)
            if not settings.is_open:
                messages.error(request, "Election is currently closed.")
            elif student.has_voted:
                request.session['voter_id'] = student.id
                return redirect('vote_already_voted')
            else:
                request.session['voter_id'] = student.id
                return redirect('vote_ballot')
        except Student.DoesNotExist:
            messages.error(request, "Student not found.")
            
    return render(request, 'core/vote_login.html', {'station_house': station_house})

def vote_ballot(request):
    station_house = check_station(request)
    if not station_house:
        return redirect('setup')
    
    voter_id = request.session.get('voter_id')
    if not voter_id:
        return redirect('vote_login')
        
    student = get_object_or_404(Student, id=voter_id)
    if student.has_voted:
        return redirect('vote_already_voted')
        
    school_positions = Position.objects.filter(scope='school_wide', is_active=True)
    house_positions = Position.objects.filter(scope='house_specific', house__iexact=station_house, is_active=True)
    
    if request.method == 'POST':
        all_positions = list(school_positions) + list(house_positions)
        votes_to_create = []
        try:
            with transaction.atomic():
                for pos in all_positions:
                    candidate_id = request.POST.get(f'position_{pos.id}')
                    if not candidate_id:
                        raise ValueError("Please vote for all positions.")
                    candidate = Candidate.objects.get(id=candidate_id)
                    votes_to_create.append(Vote(voter=student, candidate=candidate, position=pos))
                
                Vote.objects.bulk_create(votes_to_create)
                student.has_voted = True
                student.save()
                
            return redirect('vote_confirm')
        except Exception as e:
            messages.error(request, str(e))
            
    context = {
        'student': student,
        'station_house': station_house,
        'school_positions': school_positions,
        'house_positions': house_positions,
        'candidates': Candidate.objects.filter(is_active=True).select_related('position', 'student'),
    }
    return render(request, 'core/vote_ballot.html', context)

def vote_confirm(request):
    voter_id = request.session.get('voter_id')
    student = None
    if voter_id:
        student = Student.objects.filter(id=voter_id).first()
        request.session.pop('voter_id', None)
    return render(request, 'core/vote_confirm.html', {'student': student})

def vote_already_voted(request):
    voter_id = request.session.get('voter_id')
    student = None
    if voter_id:
        student = Student.objects.filter(id=voter_id).first()
        request.session.pop('voter_id', None)
    return render(request, 'core/vote_already_voted.html', {'student': student})


# Admin Views
def admin_login(request):
    if request.method == 'POST':
        u = request.POST.get('username')
        p = request.POST.get('password')
        user = authenticate(request, username=u, password=p)
        if user and user.is_staff:
            login(request, user)
            return redirect('admin_dashboard')
        else:
            messages.error(request, "Invalid admin credentials")
    return render(request, 'core/admin_login.html')

def admin_logout(request):
    logout(request)
    return redirect('setup')

@user_passes_test(is_staff, login_url='admin_login')
def admin_dashboard(request):
    total = Student.objects.count()
    voted = Student.objects.filter(has_voted=True).count()
    turnout = (voted / total * 100) if total > 0 else 0
    settings = get_settings()
    
    recent_votes = Vote.objects.select_related('voter').order_by('-timestamp')[:10]
    
    context = {
        'total': total,
        'voted': voted,
        'turnout': turnout,
        'settings': settings,
        'recent_votes': recent_votes,
    }
    return render(request, 'core/admin_dashboard.html', context)

@user_passes_test(is_staff, login_url='admin_login')
def admin_election(request):
    settings = get_settings()
    if request.method == 'POST':
        if 'reset_votes' in request.POST:
            if request.POST.get('confirm_text') == 'RESET':
                Vote.objects.all().delete()
                Student.objects.update(has_voted=False)
                messages.success(request, "All votes have been reset.")
            else:
                messages.error(request, "Reset cancelled. You must type RESET.")
            return redirect('admin_election')
            
        settings.election_name = request.POST.get('election_name')
        settings.academic_year = request.POST.get('academic_year')
        settings.is_open = request.POST.get('is_open') == 'on'
        pin = request.POST.get('station_pin')
        if pin:
            settings.station_pin = pin
        settings.save()
        messages.success(request, "Settings updated successfully.")
        return redirect('admin_election')
        
    return render(request, 'core/admin_election.html', {'settings': settings})

@user_passes_test(is_staff, login_url='admin_login')
def admin_students(request):
    if request.method == 'POST' and request.FILES.get('csv_file'):
        csv_file = request.FILES['csv_file']
        decoded_file = csv_file.read().decode('utf-8').splitlines()
        reader = csv.DictReader(decoded_file)
        
        skipped = 0
        added = 0
        for row in reader:
            enrollment_number = row.get('enrollment_number')
            house = row.get('house')
            if not enrollment_number or not house:
                skipped += 1
                continue
                
            obj, created = Student.objects.get_or_create(
                enrollment_number=enrollment_number,
                defaults={
                    'full_name': row.get('full_name', ''),
                    'house': house,
                    'class_grade': row.get('class_grade', '')
                }
            )
            if created:
                added += 1
            else:
                skipped += 1
                
        messages.success(request, f"Imported {added} students. Skipped {skipped} rows.")
        return redirect('admin_students')
        
    students = Student.objects.all()
    return render(request, 'core/admin_students.html', {'students': students})

@user_passes_test(is_staff, login_url='admin_login')
def admin_candidates(request):
    if request.method == 'POST':
        student_id = request.POST.get('student_id')
        position_id = request.POST.get('position_id')
        bio = request.POST.get('bio')
        photo = request.FILES.get('photo')
        
        try:
            student = Student.objects.get(id=student_id)
            position = Position.objects.get(id=position_id)
            Candidate.objects.create(student=student, position=position, bio=bio, photo=photo)
            messages.success(request, "Candidate added.")
        except Exception as e:
            messages.error(request, str(e))
        return redirect('admin_candidates')
        
    candidates = Candidate.objects.select_related('student', 'position').all()
    positions = Position.objects.all()
    students = Student.objects.all()
    return render(request, 'core/admin_candidates.html', {
        'candidates': candidates, 'positions': positions, 'students': students
    })

@user_passes_test(is_staff, login_url='admin_login')
def admin_results(request):
    return render(request, 'core/admin_results.html')

@user_passes_test(is_staff, login_url='admin_login')
def admin_results_data(request):
    positions = Position.objects.filter(is_active=True)
    results = []
    
    for pos in positions:
        candidates = Candidate.objects.filter(position=pos, is_active=True)
        total_votes = Vote.objects.filter(position=pos).count()
        cand_list = []
        for c in candidates:
            votes = Vote.objects.filter(candidate=c).count()
            pct = (votes / total_votes * 100) if total_votes > 0 else 0
            cand_list.append({
                'id': c.id,
                'name': c.student.full_name,
                'photo_url': c.photo.url if c.photo else '',
                'votes': votes,
                'percentage': round(pct, 1)
            })
        
        cand_list = sorted(cand_list, key=lambda x: x['votes'], reverse=True)
        
        results.append({
            'position_id': pos.id,
            'position_name': pos.name,
            'scope': pos.scope,
            'house': pos.house,
            'total_votes': total_votes,
            'candidates': cand_list
        })
        
    return JsonResponse({'results': results})

@user_passes_test(is_staff, login_url='admin_login')
def admin_export(request):
    if request.GET.get('format') == 'csv':
        response = HttpResponse(content_type='text/csv')
        response['Content-Disposition'] = 'attachment; filename="results.csv"'
        writer = csv.writer(response)
        writer.writerow(['Position', 'Candidate', 'Enrollment No.', 'Votes', 'Percentage'])
        
        for pos in Position.objects.filter(is_active=True):
            total_votes = Vote.objects.filter(position=pos).count()
            for c in Candidate.objects.filter(position=pos, is_active=True):
                votes = Vote.objects.filter(candidate=c).count()
                pct = (votes / total_votes * 100) if total_votes > 0 else 0
                writer.writerow([pos.name, c.student.full_name, c.student.enrollment_number, votes, f"{pct:.1f}%"])
        return response
        
    elif request.GET.get('format') == 'pdf':
        try:
            from reportlab.pdfgen import canvas
            response = HttpResponse(content_type='application/pdf')
            response['Content-Disposition'] = 'attachment; filename="results.pdf"'
            
            p = canvas.Canvas(response)
            p.drawString(100, 800, "VVS Election Results")
            
            y = 750
            for pos in Position.objects.filter(is_active=True):
                p.drawString(100, y, f"Position: {pos.name}")
                y -= 20
                total_votes = Vote.objects.filter(position=pos).count()
                for c in Candidate.objects.filter(position=pos, is_active=True):
                    votes = Vote.objects.filter(candidate=c).count()
                    pct = (votes / total_votes * 100) if total_votes > 0 else 0
                    p.drawString(120, y, f"{c.student.full_name}: {votes} votes ({pct:.1f}%)")
                    y -= 20
                y -= 10
                if y < 100:
                    p.showPage()
                    y = 800
                    
            p.showPage()
            p.save()
            return response
        except ImportError:
            return HttpResponse("ReportLab not installed for PDF generation.")
        
    return render(request, 'core/admin_export.html')

@user_passes_test(is_staff, login_url='admin_login')
def admin_audit(request):
    logs = Vote.objects.select_related('voter', 'position').order_by('-timestamp')
    house_filter = request.GET.get('house')
    if house_filter:
        logs = logs.filter(voter__house__iexact=house_filter)
    
    return render(request, 'core/admin_audit.html', {'logs': logs})
