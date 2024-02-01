package setup

import (
	"data_forwarder/config"
	"fmt"
	mqtt "github.com/eclipse/paho.mqtt.golang"
	"github.com/joho/godotenv"
	"log/slog"
	"os"
	"strconv"
)

var Config *config.Config

// Setup setups the application configuration variables, compile-time values
// (interface implementations, interface assertions) and structured logging.
func Setup(envFilename string) (*config.Config, error) {
	assertInterfaces()
	setupStructuredLogger()

	appConfig, err := setupEnvVariables(envFilename)
	if err != nil {
		return nil, fmt.Errorf("error when setting up env variables: %s", err)
	}

	Config = appConfig

	return appConfig, nil
}

func setupEnvVariables(envFilename string) (*config.Config, error) {
	if err := godotenv.Load(envFilename); err != nil {
		return nil, fmt.Errorf("error when loading env file: %s", err)
	}

	dittoPort, err := strconv.Atoi(os.Getenv("DITTO_PORT"))
	if err != nil {
		return nil, fmt.Errorf("DITTO_PORT should be an integer: %s", err)
	}

	dittoConfig := &config.DigitalTwinConfig{
		Host:     os.Getenv("DITTO_HOST"),
		Port:     dittoPort,
		Username: os.Getenv("DITTO_USERNAME"),
		Password: os.Getenv("DITTO_PASSWORD"),
		ThingID:  os.Getenv("DITTO_THING_ID"),
	}

	mqttBrokerPort, err := strconv.Atoi(os.Getenv("BROKER_PORT"))
	if err != nil {
		return nil, fmt.Errorf("BROKER_PORT should be an integer: %s", err)
	}

	mqttClientConfig := mqtt.NewClientOptions().
		AddBroker(fmt.Sprintf(
			"tcp://%s:%d", os.Getenv("BROKER_HOST"), mqttBrokerPort),
		).
		SetOrderMatters(false)

	webServicePort, err := strconv.Atoi(os.Getenv("WEB_SERVICE_PORT"))
	if err != nil {
		return nil, fmt.Errorf("WEB_SERVICE_PORT should be an integer: %s", err)
	}

	webServiceConfig := config.NewWebServiceConfig(
		os.Getenv("WEB_SERVICE_HOST"),
		webServicePort,
	)

	return &config.Config{
		DigitalTwinConfig:   dittoConfig,
		BrokerClientOptions: mqttClientConfig,
		WebServiceConfig:    webServiceConfig,
	}, nil
}

func assertInterfaces() {
	//
}

func setupStructuredLogger() {
	// Setup structured logger using the JSON format.
	slog.SetDefault(slog.New(slog.NewJSONHandler(os.Stdout, nil)))
}
