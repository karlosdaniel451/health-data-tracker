from rest_framework import viewsets, status
from rest_framework.generics import get_object_or_404
from rest_framework.renderers import JSONRenderer
from rest_framework.response import Response

from .models import SessionQueries
from .serializers import SessionQueriesSerializer


class SessionQueriesViewSet(viewsets.ModelViewSet):
    queryset = SessionQueries.objects.all()
    serializer_class = SessionQueriesSerializer
    renderer_classes = [JSONRenderer]

    def create(self, request, *args, **kwargs):
        data = request.data.copy()

        session = get_object_or_404(SessionQueries, id=1)
        for key, value in data.items():
            setattr(session, key, value)
        session.save()

        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        headers = self.get_success_headers(serializer.data)
        return Response(serializer.data, status=status.HTTP_201_CREATED, headers=headers)

    def get_http_method_names(self):
        return ['get', 'post']
