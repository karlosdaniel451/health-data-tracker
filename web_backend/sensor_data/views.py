from rest_framework import viewsets, pagination
from rest_framework.renderers import JSONRenderer

from .models import SensorData
from .serializers import SensorDataSerializer
from django.utils.dateparse import parse_datetime


class StandardResultsSetPagination(pagination.PageNumberPagination):
    page_size_query_param = 'page_size'
    page_size = 50
    max_page_size = 100


class SensorDataViewSet(viewsets.ModelViewSet):
    queryset = SensorData.objects.all().order_by('-timestamp')
    serializer_class = SensorDataSerializer
    renderer_classes = [JSONRenderer]  # Add this line
    pagination_class = StandardResultsSetPagination

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
