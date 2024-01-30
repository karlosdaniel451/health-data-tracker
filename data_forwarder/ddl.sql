--- Create measurements table.
CREATE TABLE IF NOT EXISTS measurements
(
    time        PG_CATALOG.TIMESTAMPTZ NOT NULL,
    user_id     uuid                   NOT NULL,
    device_id   uuid                   NOT NULL,
    temperature DOUBLE PRECISION       NOT NULL,
    humidity    FLOAT                  NOT NULL,
    noise       FLOAT                  NOT NULL,
    heart_rate  INTEGER
);


--- Create timescaledb hypertable from the measurements table.
SELECT create_hypertable('measurements', 'time');

--- Create index of measurements on time, user_id and device_id.
CREATE INDEX ix_measurements_time_user_id ON measurements (time, user_id, device_id);
