from django.db import models

class Student(models.Model):
    enrollment_number = models.CharField(max_length=50, unique=True)
    full_name = models.CharField(max_length=150)
    house = models.CharField(max_length=50)
    class_grade = models.CharField(max_length=20)
    has_voted = models.BooleanField(default=False)

    def __str__(self):
        return f"{self.full_name} ({self.enrollment_number})"


class Position(models.Model):
    SCOPE_CHOICES = (
        ('school_wide', 'School Wide'),
        ('house_specific', 'House Specific'),
    )
    GENDER_CHOICES = (
        ('Boy', 'Boy'),
        ('Girl', 'Girl'),
        ('Open', 'Open'),
    )

    name = models.CharField(max_length=100)
    scope = models.CharField(max_length=20, choices=SCOPE_CHOICES)
    house = models.CharField(max_length=50, blank=True, null=True)
    gender_category = models.CharField(max_length=10, choices=GENDER_CHOICES)
    is_active = models.BooleanField(default=True)

    def __str__(self):
        base = f"{self.name} ({self.gender_category})"
        if self.scope == 'house_specific':
            return f"{base} - {self.house}"
        return base


class Candidate(models.Model):
    student = models.ForeignKey(Student, on_delete=models.CASCADE)
    position = models.ForeignKey(Position, on_delete=models.CASCADE)
    photo = models.ImageField(upload_to='candidate_photos/', blank=True, null=True)
    bio = models.TextField(max_length=300, blank=True, null=True)
    is_active = models.BooleanField(default=True)

    def __str__(self):
        return f"{self.student.full_name} - {self.position.name}"


class Vote(models.Model):
    voter = models.ForeignKey(Student, on_delete=models.CASCADE)
    candidate = models.ForeignKey(Candidate, on_delete=models.CASCADE)
    position = models.ForeignKey(Position, on_delete=models.CASCADE)
    timestamp = models.DateTimeField(auto_now_add=True)

    class Meta:
        unique_together = ('voter', 'position')

    def __str__(self):
        return f"Vote by {self.voter.enrollment_number} for {self.position.name}"


class ElectionSettings(models.Model):
    election_name = models.CharField(max_length=200)
    academic_year = models.CharField(max_length=20)
    is_open = models.BooleanField(default=False)
    station_pin = models.CharField(max_length=10)
    start_time = models.DateTimeField(blank=True, null=True)
    end_time = models.DateTimeField(blank=True, null=True)

    def __str__(self):
        return f"{self.election_name} ({self.academic_year})"
