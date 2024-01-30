package handlers

import (
	"data_forwarder/models"
	"data_forwarder/services/webservice"
	"encoding/json"
	mqtt "github.com/eclipse/paho.mqtt.golang"
	"log/slog"
)

func MessageHandler(_ mqtt.Client, message mqtt.Message) {
	slog.Info(
		"mqtt message consumed",
		"message_id", message.MessageID(),
		"topic", message.Topic(),
		"payload", string(message.Payload()),
	)

	var measurement models.Measurement
	if err := json.Unmarshal(message.Payload(), &measurement); err != nil {
		slog.Error(
			"error when deserializing message consumed from broker",
			"cause", err,
		)
		return
	}
	slog.Info("measurement message serialized", "measurement", measurement.String())

	if err := webservice.PostMeasurement(&measurement); err != nil {
		slog.Error(
			"error when sending measurement to web backend",
			"cause", err,
		)
		return
	}
}
