package app

import (
	"data_forwarder/config"
	"data_forwarder/services/broker"
	mqtthandlers "data_forwarder/services/broker/handlers"
	"data_forwarder/services/digitaltwin"
	"fmt"
	"github.com/eclipse/ditto-clients-golang"
	mqtt "github.com/eclipse/paho.mqtt.golang"
	"log/slog"
)

type App struct {
	dittoClient *ditto.Client
	mqttClient  *mqtt.Client
	config      config.Config
}

func (app *App) SetConfig(config config.Config) {
	app.config = config
}

// Start starts the application and run indefinitely until Shutdown is called.
func (app App) Start() error {
	dittoClient, err := digitaltwin.Connect(app.config.DigitalTwinConfig)
	if err != nil {
		return fmt.Errorf("error when connecting to digital twin service (ditto): %s", err)
	}
	slog.Info("connected to digital twin service (ditto) successfully")

	app.dittoClient = dittoClient

	mqttClient, err := broker.Connect(app.config.BrokerClientOptions)
	if err != nil {
		return fmt.Errorf("error when connecting to mqtt broker server: %s", err)
	}
	slog.Info("connected to mqtt broker server successfully")

	app.mqttClient = mqttClient

	token := (*mqttClient).Subscribe(
		"enviro_pulse_dev",
		0,
		mqtthandlers.MessageHandler,
	)

	if token.Wait() && token.Error() != nil {
		//slog.Error("error when subscribing to topic", "cause", token.Error())
		return fmt.Errorf("error when subscribing to topic", "cause", token.Error())
	}
	slog.Info(
		fmt.Sprintf("subscribed to mqtt topic '%s' successfully", "enviro_pulse_dev"),
	)

	// Goroutine wait indefinitely.
	select {}
}

func (app App) Shutdown() {
	(*app.mqttClient).Disconnect(5_000)
	(*app.dittoClient).Disconnect()
}
