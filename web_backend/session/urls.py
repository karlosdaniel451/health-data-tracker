# myapp/urls.py

from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import SessionQueriesViewSet

router = DefaultRouter()
router.register('', SessionQueriesViewSet)

urlpatterns = [
    path('', include(router.urls)),
]
