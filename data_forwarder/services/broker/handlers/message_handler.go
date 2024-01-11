package handlers

import (
	"bytes"
	"data_forwarder/models"
	"encoding/json"
	"fmt"
	mqtt "github.com/eclipse/paho.mqtt.golang"
	"log/slog"
	"net/http"
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

	requestBodyContent := fmt.Sprintf(
		`
		{
			"ambient_temperature": {
				"properties": {
					"value": %.2f
				}
			},
			"heart_rate": {
				"properties": {
					"value": %d
				}
			},
			"stress": {
				"properties": {
					"value": %d
				}
			}
		}
		`,
		*measurement.AmbientTemperature, *measurement.HeartRate, *measurement.Stress,
	)
	requestBody := bytes.NewReader([]byte(requestBodyContent))

	request, err := http.NewRequest(
		http.MethodPut,
		fmt.Sprintf(
			"http://localhost:8080/api/2/things/%s/features/",
			measurement.ThingID,
		),
		requestBody,
	)
	if err != nil {
		slog.Error(
			"error when creating request",
			"cause", err,
		)
		return
	}

	request.SetBasicAuth("ditto", "ditto")
	request.Header.Set("content-type", "application/json")

	response, err := http.DefaultClient.Do(request)
	if err != nil {
		slog.Error(
			"error when requesting ditto",
			"cause", err,
		)
		return
	}

	if response.StatusCode != http.StatusNoContent {
		if err != nil {
			slog.Error(
				"status in ditto http response is different than expected",
				"expected", http.StatusNoContent,
				"actual", response.StatusCode,
			)
		}
	}
	//command := things.NewCommand(model.NewNamespacedIDFrom(measurement.ThingID)).
	//	Twin()

	////if measurement.HeartRate != nil {
	////	command = command.
	////		FeatureProperty("heart_rate", "value").
	////		Modify(*measurement.HeartRate)
	////}
	////if measurement.AmbientTemperature != nil {
	////	command = command.
	////		FeatureProperty("ambient_temperature", "value").
	////		Modify(*measurement.AmbientTemperature)
	////}
	////if measurement.Stress != nil {
	////	command = command.
	////		FeatureProperty("ambient_temperature", "value").
	////		Modify(*measurement.Stress)
	////}
	//
	////feature := &model.Feature{}
	////feature = feature.WithProperty("value", 90)
	////command.Feature("stress").Modify(feature)
	//
	//feature := &model.Feature{}
	//feature = feature.WithProperty("value", 100)
	//command.Feature("new_test_feature").Modify(feature)
	//
	//envelope := command.Envelope(protocol.WithResponseRequired(false))
	//if err := dittoClient.Send(envelope); err != nil {
	//	slog.Error(
	//		"error when sending command to ditto",
	//		"cause", err,
	//	)
	//}
	//
	//slog.Info(
	//	"command sent to ditto",
	//	"command_path", command.Path,
	//	"command_topic", command.Topic,
	//	"command_payload", command.Payload,
	//	"envelope_value", envelope.Value,
	//)
}
