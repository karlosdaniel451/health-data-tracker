package webservice

import (
	"data_forwarder/cmd/setup"
	"data_forwarder/models"
	"encoding/json"
	"fmt"
	"io"
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

	var requestBody io.Reader
	if err := json.NewDecoder(requestBody).Decode(measurement); err != nil {
		return fmt.Errorf("error when serializing measurement: %s", err)
	}

	baseUrl := setup.Config.WebServiceConfig.GetBaseUrl()

	request, err := http.NewRequest(
		http.MethodPost,
		baseUrl+"/sensor-data/",
		requestBody,
	)
	if err != nil {
		return fmt.Errorf("error when creating request: %s", err)
	}

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
