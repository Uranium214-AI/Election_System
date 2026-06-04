from django.contrib import admin
from .models import Student, Position, Candidate, Vote, ElectionSettings

admin.site.register(Student)
admin.site.register(Position)
admin.site.register(Candidate)
admin.site.register(Vote)
admin.site.register(ElectionSettings)
