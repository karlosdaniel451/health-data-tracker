package models

import (
	"fmt"
	"github.com/google/uuid"
	"time"
)

type Measurement struct {
	Time               time.Time `json:"time"`
	ThingID            string    `json:"thing_id"`
	PersonID           uuid.UUID `json:"person_id"`
	HeartRate          *int      `json:"heart_rate"`
	AmbientTemperature *float64  `json:"ambient_temperature"`
	Stress             *int      `json:"stress"`
}

func (m Measurement) String() string {
	return fmt.Sprintf(
		"time: %s, thing_id: %s, person_id: %s, heart_rate: %v, ambient_temperature: %v, stress: %v",
		m.Time.UTC(), m.ThingID, m.PersonID, m.HeartRate, m.AmbientTemperature, m.Stress,
	)
}
