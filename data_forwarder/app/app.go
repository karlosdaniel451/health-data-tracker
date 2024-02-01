package app

import (
	"data_forwarder/config"
	"data_forwarder/services/broker"
	"fmt"
	"github.com/eclipse/ditto-clients-golang"
	mqtt "github.com/eclipse/paho.mqtt.golang"
	"log/slog"
)

type App struct {
	dittoClient *ditto.Client
	mqttClient  *mqtt.Client
	config      *config.Config
}

func (app *App) SetConfig(config *config.Config) {
	app.config = config
}

// Start starts the application and run indefinitely until Shutdown is called.
func (app App) Start() error {
	mqttClient, err := broker.Connect(app.config.BrokerClientOptions)
	if err != nil {
		return fmt.Errorf("error when connecting to mqtt broker server: %s", err)
	}
	slog.Info("connected to mqtt broker server successfully")

	app.mqttClient = mqttClient

	if err := broker.Consume(mqttClient, "enviro_pulse", 0); err != nil {
		slog.Error(
			"error when subscribing to mqtt topic",
			"topic", "enviro_pulse",
			"cause", err,
		)
		return err
	}
	slog.Info(
		"subscribed to mqtt topic successfully",
		"topic", "enviro_pulse",
	)

	// Goroutine wait indefinitely.
	select {}
}

func (app App) Shutdown() {
	(*app.mqttClient).Disconnect(5_000)
	(*app.dittoClient).Disconnect()
}
