from rest_framework import serializers
from .models import SensorData, LastRecordedSensorData


class SensorDataSerializer(serializers.ModelSerializer):
    class Meta:
        model = SensorData
        fields = ['timestamp', 'temperature', 'noise_level', 'humidity']
        read_only_fields = ['id']


class LastRecordedSensorDataSerializer(serializers.ModelSerializer):
    class Meta:
        model = LastRecordedSensorData
        fields = ['heart_frequency']
        read_only_fields = ['id']
