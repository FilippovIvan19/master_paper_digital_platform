create database SmartMonitoring;
USE SmartMonitoring;

CREATE TABLE MonitoringData (
    `timestamp` TIMESTAMP,
    amount DECIMAL(38, 6),
    flags STRING
)
PARTITIONED BY (monitorId STRING)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE;

