from rest_framework import serializers

from .models import SessionQueries


class SessionQueriesSerializer(serializers.ModelSerializer):
    class Meta:
        model = SessionQueries
        fields = '__all__'
        read_only_fields = ['id']
