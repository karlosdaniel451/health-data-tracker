from django.db import models


class SensorData(models.Model):
    timestamp = models.DateTimeField()
    temperature = models.DecimalField(max_digits=5, decimal_places=2)
    noise_level = models.DecimalField(max_digits=5, decimal_places=2)
    humidity = models.DecimalField(max_digits=5, decimal_places=2)
    heart_frequency = models.IntegerField(blank=True, null=True)

    def __str__(self):
        return (f"{self.timestamp} | Temp: {self.temperature} °C | Noise: {self.noise_level} dB |"
                f" Humidity: {self.humidity}% | Heart Frequency: {self.heart_frequency}%")


class LastRecordedSensorData(models.Model):
    heart_frequency = models.IntegerField(blank=True, null=True)

    def __str__(self):
        return f"Heart Frequency: {self.heart_frequency}%"
