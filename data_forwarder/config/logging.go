package config

import (
	"log/slog"
	"os"
)

func ConfigStructuredLogging() {
	slog.SetDefault(slog.New(slog.NewJSONHandler(os.Stdout, nil)))
}
