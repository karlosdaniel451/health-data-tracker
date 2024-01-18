from rest_framework import serializers
from .models import SensorData


class SensorDataSerializer(serializers.ModelSerializer):
    class Meta:
        model = SensorData
        fields = ['timestamp', 'temperature', 'noise_level', 'humidity', 'heart_frequency']
        read_only_fields = ['id']
