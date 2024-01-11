package config

import (
	"github.com/eclipse/ditto-clients-golang"
	mqtt "github.com/eclipse/paho.mqtt.golang"
)

type Config struct {
	//	DigitalTwinServiceHost string
	//	DigitalTwinServicePost int
	//	BrokerServerHost       string
	//	BrokerServerPost       int
	DigitalTwinConfig   *ditto.Configuration
	BrokerClientOptions *mqtt.ClientOptions
}
