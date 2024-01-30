package models

import (
	"fmt"
	"time"
)

type Measurement struct {
	Timestamp      time.Time `json:"timestamp"`
	Temperature    *float64  `json:"temperature"`
	NoiseLevel     *float64  `json:"noise_level"`
	Humidity       *float64  `json:"stress"`
	HeartFrequency *int      `json:"heart_frequency"`
}

func (m Measurement) String() string {
	return fmt.Sprintf(
		"time: %s, thing_id: %s, heart_rate: %v, ambient_temperature: %v, humidity: %v",
		m.Timestamp.UTC(), m.NoiseLevel, m.HeartFrequency, m.Temperature, m.Humidity,
	)
}