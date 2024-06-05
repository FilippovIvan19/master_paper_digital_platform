CREATE TABLE device_prediction_record (
    id          BIGINT PRIMARY KEY,
    monitor_id  VARCHAR(20),
    year        INT,
    month       INT
);

CREATE TABLE predicted_device (
    id                      BIGINT PRIMARY KEY,
    prediction_record_id    BIGINT REFERENCES device_prediction_record(id),
    device_type             VARCHAR(20),
    device_confidence       REAL,
    model                   VARCHAR(50),
    model_confidence        REAL
);

CREATE SEQUENCE hibernate_sequence START 1;

CREATE TABLE resource_monitor (
    id              VARCHAR(20) PRIMARY KEY,
    user_email      VARCHAR(50),
    resource_type   VARCHAR(20)
);

INSERT INTO resource_monitor VALUES ('123', 'abc@gmail.com', 'GAS');
INSERT INTO resource_monitor VALUES ('345', 'abc@gmail.com', 'WATER');
