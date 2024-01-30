from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import SensorDataViewSet, LastRecordedSensorDataViewSet

router = DefaultRouter()
router.register('register', SensorDataViewSet)
router.register('last-recorded', LastRecordedSensorDataViewSet)

urlpatterns = [
    path('', include(router.urls)),
]
