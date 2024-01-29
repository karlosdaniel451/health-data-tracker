from rest_framework import viewsets, pagination, status
from rest_framework.generics import get_object_or_404
from rest_framework.renderers import JSONRenderer
from rest_framework.response import Response

from .models import SensorData, LastRecordedSensorData
from .serializers import SensorDataSerializer, LastRecordedSensorDataSerializer
from django.utils.dateparse import parse_datetime


class StandardResultsSetPagination(pagination.PageNumberPagination):
    page_size_query_param = 'page_size'
    page_size = 50


class SensorDataViewSet(viewsets.ModelViewSet):
    queryset = SensorData.objects.all().order_by('-timestamp')
    serializer_class = SensorDataSerializer
    renderer_classes = [JSONRenderer]
    pagination_class = StandardResultsSetPagination

    def create(self, request, *args, **kwargs):
        response = super().create(request, *args, **kwargs)
        instance = self.get_object()
        instance.heart_frequency = LastRecordedSensorData.objects.get(id=1).heart_frequency
        instance.save()

        return response

    def get_queryset(self):
        # Retrieve query parameters
        start_date = self.request.query_params.get('start_date')
        end_date = self.request.query_params.get('end_date')

        # Temperature filters
        temperature = self.request.query_params.get('temperature')
        temperature_condition = self.request.query_params.get('temperature_condition')

        # Noise level filters
        noise_level = self.request.query_params.get('noise_level')
        noise_level_condition = self.request.query_params.get('noise_level_condition')

        # Humidity filters
        humidity = self.request.query_params.get('humidity')
        humidity_condition = self.request.query_params.get('humidity_condition')

        # Heart frequency filters
        heart_frequency = self.request.query_params.get('heart_frequency')
        heart_frequency_condition = self.request.query_params.get('heart_frequency_condition')

        queryset = self.queryset

        # Date range filtering
        if start_date:
            start = parse_datetime(start_date)
            start = start.replace(second=0)
            queryset = queryset.filter(timestamp__gte=start)

        if end_date:
            end = parse_datetime(end_date)
            end = end.replace(second=59)
            queryset = queryset.filter(timestamp__lte=end)

        # Apply filters with conditions
        if temperature and temperature_condition:
            queryset = self.apply_condition_filter(queryset, 'temperature', temperature, temperature_condition)

        if noise_level and noise_level_condition:
            queryset = self.apply_condition_filter(queryset, 'noise_level', noise_level, noise_level_condition)

        if humidity and humidity_condition:
            queryset = self.apply_condition_filter(queryset, 'humidity', humidity, humidity_condition)

        if heart_frequency and heart_frequency_condition:
            queryset = self.apply_condition_filter(queryset, 'heart_frequency', heart_frequency,
                                                   heart_frequency_condition)
        return queryset

    def apply_condition_filter(self, queryset, field, value, condition):
        if condition == 'lt':
            return queryset.filter(**{f'{field}__lt': value})
        elif condition == 'gt':
            return queryset.filter(**{f'{field}__gt': value})
        elif condition == 'eq':
            return queryset.filter(**{f'{field}': value})
        return queryset

    def get_http_method_names(self):
        return ['get', 'post']


class LastRecordedSensorDataViewSet(viewsets.ModelViewSet):
    queryset = LastRecordedSensorData.objects.all()
    serializer_class = LastRecordedSensorDataSerializer
    renderer_classes = [JSONRenderer]

    def create(self, request, *args, **kwargs):
        data = request.data.copy()

        last_recorded_sensor_data = get_object_or_404(LastRecordedSensorData, id=1)
        for key, value in data.items():
            setattr(last_recorded_sensor_data, key, value)
        last_recorded_sensor_data.save()

        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        headers = self.get_success_headers(serializer.data)
        return Response(serializer.data, status=status.HTTP_201_CREATED, headers=headers)

    def get_http_method_names(self):
        return ['post']
