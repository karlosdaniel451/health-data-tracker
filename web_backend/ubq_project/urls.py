# project_name/urls.py

from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('sensor-data/', include('sensor_data.urls')),
    path('session-queries/', include('session.urls')),
]
