from django.core.management.base import BaseCommand
from django.contrib.auth.models import User
from core.models import Student, Position, Candidate, ElectionSettings

class Command(BaseCommand):
    help = 'Seeds the database with demo data'

    def handle(self, *args, **kwargs):
        # Create superuser
        if not User.objects.filter(username='admin').exists():
            User.objects.create_superuser('admin', 'admin@example.com', 'vvs2024')
            self.stdout.write(self.style.SUCCESS('Admin user created.'))

        # Create settings
        settings, _ = ElectionSettings.objects.get_or_create(id=1)
        settings.election_name = "VVS Demo Election"
        settings.academic_year = "2024"
        settings.is_open = True
        settings.station_pin = "1234"
        settings.save()

        # Create Positions
        school_wide = [
            ("Head Boy", "Boy"),
            ("Head Girl", "Girl"),
            ("Sports Prefect (Boy)", "Boy"),
            ("Sports Prefect (Girl)", "Girl"),
        ]
        for name, gender in school_wide:
            Position.objects.get_or_create(name=name, scope='school_wide', gender_category=gender)

        houses = ['Cheetah', 'Jaguar', 'Puma', 'Sher']
        house_specific = [
            ("House Captain (Boy)", "Boy"),
            ("House Captain (Girl)", "Girl"),
            ("House Vice Captain (Boy)", "Boy"),
            ("House Vice Captain (Girl)", "Girl"),
        ]
        for house in houses:
            for name, gender in house_specific:
                Position.objects.get_or_create(name=name, scope='house_specific', house=house, gender_category=gender)

        self.stdout.write(self.style.SUCCESS('Positions created.'))

        # Create Students
        for i, house in enumerate(houses):
            for j in range(5):
                enr = f"DEMO{i}{j}"
                Student.objects.get_or_create(
                    enrollment_number=enr,
                    defaults={'full_name': f"Demo Student {enr}", 'house': house, 'class_grade': '10th'}
                )

        self.stdout.write(self.style.SUCCESS('Students created.'))

        # Create Candidates (2 per position)
        for pos in Position.objects.all():
            if Candidate.objects.filter(position=pos).count() < 2:
                # Find students
                if pos.scope == 'house_specific':
                    st_list = Student.objects.filter(house=pos.house)[:2]
                else:
                    st_list = Student.objects.all()[:2]
                
                for st in st_list:
                    Candidate.objects.get_or_create(student=st, position=pos, defaults={'bio': 'Vote for me!'})

        self.stdout.write(self.style.SUCCESS('Candidates created.'))
        self.stdout.write(self.style.SUCCESS('Database seeded successfully!'))
