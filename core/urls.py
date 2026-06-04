from django.urls import path
from . import views

urlpatterns = [
    # General
    path('', views.home_redirect, name='home_redirect'),
    path('setup/', views.setup, name='setup'),
    path('setup/house/', views.setup_house, name='setup_house'),
    
    # Voter
    path('vote/login/', views.vote_login, name='vote_login'),
    path('vote/ballot/', views.vote_ballot, name='vote_ballot'),
    path('vote/confirm/', views.vote_confirm, name='vote_confirm'),
    path('vote/already-voted/', views.vote_already_voted, name='vote_already_voted'),
    
    # Admin
    path('admin/login/', views.admin_login, name='admin_login'),
    path('admin/logout/', views.admin_logout, name='admin_logout'),
    path('admin/dashboard/', views.admin_dashboard, name='admin_dashboard'),
    path('admin/election/', views.admin_election, name='admin_election'),
    path('admin/students/', views.admin_students, name='admin_students'),
    path('admin/candidates/', views.admin_candidates, name='admin_candidates'),
    path('admin/results/', views.admin_results, name='admin_results'),
    path('admin/results/data/', views.admin_results_data, name='admin_results_data'),
    path('admin/export/', views.admin_export, name='admin_export'),
    path('admin/audit/', views.admin_audit, name='admin_audit'),
]
