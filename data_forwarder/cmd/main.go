package main

import (
	application "data_forwarder/app"
	"data_forwarder/cmd/setup"
	"data_forwarder/config"
	"log/slog"
	"os"
)

const (
	envFilenameDev  = "../.env_dev"
	secretsFilename = ".secrets"
)

func main() {
	var app application.App
	var appConfig *config.Config
	var err error

	if appConfig, err = setup.Setup(secretsFilename); err != nil {
		slog.Error(
			"error when setting up application",
			"cause", err,
		)
		os.Exit(1)
	}

	app.SetConfig(appConfig)

	if err := app.Start(); err != nil {
		slog.Error("fatal error when starting application", "cause", err)
		os.Exit(1)
	}

	//slog.Info("application started successfully")
}
