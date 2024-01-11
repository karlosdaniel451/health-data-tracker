package digitaltwin

import (
	"github.com/eclipse/ditto-clients-golang"
)

// Connect connects to Eclipse Ditto Digital Twin service with the given config,
// and returns the created client and any encountered error.
func Connect(config *ditto.Configuration) (*ditto.Client, error) {
	client := ditto.NewClient(config)
	if err := client.Connect(); err != nil {
		return nil, err
	}

	return &client, nil
}
