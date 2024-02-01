package config

import (
	"fmt"
	mqtt "github.com/eclipse/paho.mqtt.golang"
)

type DigitalTwinConfig struct {
	Host     string
	Port     int
	Username string
	Password string
	ThingID  string
}

func NewDigitalTwinConfig(
	host string, port int,
) *WebServiceConfig {

	return &WebServiceConfig{
		Host: host,
		Port: port,
	}
}

func (digitalTwinConfig DigitalTwinConfig) GetBaseUrl() string {
	return fmt.Sprintf("http://%s:%d", digitalTwinConfig.Host, digitalTwinConfig.Port)
}

type WebServiceConfig struct {
	Host string
	Port int
}

func NewWebServiceConfig(host string, port int) *WebServiceConfig {
	return &WebServiceConfig{
		Host: host,
		Port: port,
	}
}

func (webServiceConfig WebServiceConfig) GetBaseUrl() string {
	return fmt.Sprintf("http://%s:%d", webServiceConfig.Host, webServiceConfig.Port)
}

type Config struct {
	DigitalTwinConfig   *DigitalTwinConfig
	BrokerClientOptions *mqtt.ClientOptions
	WebServiceConfig    *WebServiceConfig
}
