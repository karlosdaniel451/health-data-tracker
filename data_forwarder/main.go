package main

import (
	application "data_forwarder/app"
	"data_forwarder/config"
	dittohandlers "data_forwarder/services/digitaltwin/handlers"
	"github.com/eclipse/ditto-clients-golang"
	mqtt "github.com/eclipse/paho.mqtt.golang"
	"log/slog"
	"os"
	"time"
)

func main() {
	config.ConfigStructuredLogging()

	var app application.App

	dittoClientConfig := ditto.NewConfiguration().
		WithKeepAlive(30 * time.Second). // Default keep alive is 30 seconds.
		WithCredentials(&ditto.Credentials{Username: "ditto", Password: "ditto"}).
		WithBroker("localhost:1883").
		WithConnectHandler(dittohandlers.ConnectionHandler)

	mqttClientConfig := mqtt.NewClientOptions().
		AddBroker("localhost:1883").
		SetOrderMatters(false)

	app.SetConfig(config.Config{
		DigitalTwinConfig:   dittoClientConfig,
		BrokerClientOptions: mqttClientConfig,
	})

	if err := app.Start(); err != nil {
		slog.Error("fatal error when starting application", "cause", err)
		os.Exit(1)
	}

	slog.Info("application started successfully")

	//exitSignal := make(chan os.Signal, 1)
	//signal.Notify(exitSignal, syscall.SIGINT, syscall.SIGTERM)
	//<-exitSignal
	//
	//app.Shutdown()
	//slog.Info("application closed successfully")
}
