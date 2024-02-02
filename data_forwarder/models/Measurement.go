package models

import (
	"fmt"
)

type Measurement struct {
	Timestamp   *string  `json:"timestamp"`
	Temperature *float64 `json:"temperature"`
	NoiseLevel  *float64 `json:"noise_level"`
	Humidity    *float64 `json:"humidity"`
}

func (m Measurement) String() string {
	return fmt.Sprintf(
		"time: %s, noise_level: %v, temperature: %v, humidity: %v",
		*m.Timestamp, *m.NoiseLevel, *m.Temperature, *m.Humidity,
	)
}
