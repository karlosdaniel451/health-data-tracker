package webservice

import (
	"bytes"
	"data_forwarder/cmd/setup"
	"data_forwarder/models"
	"encoding/json"
	"fmt"
	"net/http"
)

//type WebServiceClient struct {
//	Host    string
//	Port    int
//	IsSSL   bool
//	BaseURL string
//}

//func NewWebServiceClient(host string, port int, isSSL bool) *WebServiceClient {
//	baseURL := "http"
//	if isSSL {
//		baseURL += "s"
//	}
//	baseURL += fmt.Sprintf("://%s:%d", host, port)
//
//	return &WebServiceClient{
//		Host:    host,
//		Port:    port,
//		IsSSL:   isSSL,
//		BaseURL: baseURL,
//	}
//}

func PostMeasurement(measurement *models.Measurement) error {
	if measurement == nil {
		return fmt.Errorf("measurement should not be nil")
	}

	requestBody := new(bytes.Buffer)
	if err := json.NewEncoder(requestBody).Encode(measurement); err != nil {
		return fmt.Errorf("error when serializing measurement: %s", err)
	}

	request, err := http.NewRequest(
		http.MethodPost,
		fmt.Sprintf(
			"http://%s:%d/sensor-data/register/",
			setup.Config.WebServiceConfig.Host, setup.Config.WebServiceConfig.Port,
		),
		requestBody,
	)
	if err != nil {
		return fmt.Errorf("error when creating request: %s", err)
	}

	request.Header.Set("content-type", "application/json")

	response, err := http.DefaultClient.Do(request)
	if err != nil {
		return fmt.Errorf("error when requesting web service: %s", err)
	}

	if response.StatusCode != http.StatusCreated {
		return fmt.Errorf(
			"error when requesting web service: response status code is different from expected: "+
				"expected: %d, received: %d",
			http.StatusCreated, response.StatusCode,
		)
	}

	return nil
}
