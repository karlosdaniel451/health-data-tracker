# ./Dockerfile

# Extend from the official Elixir image.
FROM elixir:1.15.7-alpine

COPY . .

## Required when using Linux.
RUN apk add inotify-tools

# Install dependencies.
RUN mix do deps.get

# Compile the mix project.
RUN mix do compile

## Run the application.
#ENTRYPOINT ["mix", "phx.server"]

EXPOSE 4000
