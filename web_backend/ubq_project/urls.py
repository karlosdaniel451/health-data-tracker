# project_name/urls.py

from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('admin/', admin.site.urls),
    path('sensor-data/', include('sensor_data.urls')),  # Include your app's URLs
    path('session-queries/', include('session.urls')),  # Include your app's URLs
    # You can add more paths here for other apps or URL patterns
]
