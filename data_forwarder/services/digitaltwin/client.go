package digitaltwin

import (
	"bytes"
	"data_forwarder/cmd/setup"
	"data_forwarder/models"
	"fmt"
	"net/http"
)

func UpdateDigitalTwinState(measurement *models.Measurement) error {
	requestBodyContent := fmt.Sprintf(
		`
		{
			"timestamp": {
				"properties": {
					"value": %s
				}
			},
			"temperature": {
				"properties": {
					"value": %.2f
				}
			},
			"noise_level": {
				"properties": {
					"value": %.2f
				}
			},
			"humidity": {
				"properties": {
					"value": %.2f
				}
			},
			"heart_frequency": {
				"properties": {
					"value": %d
				}
			}
		}
		`,
		measurement.Timestamp.String(), *measurement.Temperature, *measurement.NoiseLevel,
		*measurement.Humidity, *measurement.HeartFrequency,
	)
	requestBody := bytes.NewReader([]byte(requestBodyContent))

	request, err := http.NewRequest(
		http.MethodPut,
		fmt.Sprintf(
			"http://localhost:8080/api/2/things/%s/features/",
			setup.Config.DigitalTwinConfig.ThingID,
		),
		requestBody,
	)
	if err != nil {
		return fmt.Errorf("error when creating request: %s", err)
	}

	request.SetBasicAuth(setup.Config.DigitalTwinConfig.Host, setup.Config.DigitalTwinConfig.Password)
	request.Header.Set("content-type", "application/json")

	response, err := http.DefaultClient.Do(request)
	if err != nil {
		return fmt.Errorf("error when requesting ditto web serice: %s", err)
	}

	if response.StatusCode != http.StatusNoContent {
		if err != nil {
			return fmt.Errorf(
				"status in ditto http response is different from expected, expected: %d, actual: %d",
				http.StatusNoContent, response.StatusCode,
			)
		}
	}
	return nil
}
