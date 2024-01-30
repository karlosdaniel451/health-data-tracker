# Always prefer the latest stable Go version.
FROM golang:1.21-alpine AS builder

# Create and change to the app directory.
WORKDIR /app

# Copy dependency management files.
COPY go.mod ./
COPY go.sum ./

RUN go mod download

# Copy source/configuration code.
COPY . ./

# Build the executable file.
RUN go build -v -o data_forwarder ./cmd/main.go

# Use multi-image container for a lean container.
FROM alpine
RUN apk add --no-cache ca-certificates && update-ca-certificates

# Install Postgres client.
RUN apk update && apk add postgresql

WORKDIR /bin

# Copy the binary to the production image from the builder stage.
COPY --from=builder /app/data_forwarder /bin/data_forwarder
COPY --from=builder /app/.secrets /

# Run the web service on container startup.
ENTRYPOINT [ "/bin/data_forwarder" ]
