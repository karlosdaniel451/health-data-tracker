package broker

import (
	"data_forwarder/services/broker/handlers"
	mqtt "github.com/eclipse/paho.mqtt.golang"
)

// Connect connects to an MQTT Broker with the given options, and returns the created client and
// any encountered error.
func Connect(options *mqtt.ClientOptions) (*mqtt.Client, error) {
	client := mqtt.NewClient(options)

	//// Wait until connection to broker is established.
	//<-client.Connect().Done()
	//
	//if err := client.Connect().Error(); err != nil {
	//	return nil, err
	//}
	//
	//return &client, nil
	if token := client.Connect(); token.Wait() && token.Error() != nil {
		return nil, token.Error()
	}

	return &client, nil
}

func Consume(client *mqtt.Client, topic string, qos byte) error {
	token := (*client).Subscribe(
		topic,
		qos,
		handlers.MessageHandler,
	)

	if token.Wait() && token.Error() != nil {
		return token.Error()
	}
	return nil
}
