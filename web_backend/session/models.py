from django.db import models


class SessionQueries(models.Model):
    DASHBOARD_QUERY_OPTIONS = (
        ('hour', 'hour'),
        ('day', 'day'),
        ('week', 'week'),
    )

    last_temperature_chart_query = models.CharField(max_length=4, choices=DASHBOARD_QUERY_OPTIONS, null=True)
    last_humidity_chart_query = models.CharField(max_length=4, choices=DASHBOARD_QUERY_OPTIONS, null=True)
    last_noise_level_chart_query = models.CharField(max_length=4, choices=DASHBOARD_QUERY_OPTIONS, null=True)
    last_heart_frequency_chart_query = models.CharField(max_length=4, choices=DASHBOARD_QUERY_OPTIONS, null=True)
    last_report_query = models.CharField(null=True)
