#!/bin/bash

# do not use sudo to run this script, it will cause issues with unavailability of some service directories (like logs, tmp, pids, etc)

SCRIPT_DIR=$(pwd)
RED='\033[0;31m'
NC='\033[0m' # No Color
OUT_FILE=$SCRIPT_DIR/out

cd $KAFKA_HOME
# start kafka services
nohup bin/zookeeper-server-start.sh config/zookeeper.properties > $OUT_FILE 2> $OUT_FILE &
sleep 20
nohup bin/kafka-server-start.sh config/server.properties > $OUT_FILE 2> $OUT_FILE &
sleep 2
# kafka consumer terminal window
x-terminal-emulator -e bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic monitor-data-input --property print.key=true --property key.separator="-"
echo -e "${RED}KAFKA FINISHED${NC}"

cd $HADOOP_HOME
sbin/start-dfs.sh > $OUT_FILE 2> $OUT_FILE
# terminal window for running hdfs commands
# x-terminal-emulator
echo -e "${RED}HADOOP FINISHED${NC}"

cd $DERBY_HOME
# start derby db server
nohup bin/startNetworkServer -h 0.0.0.0 > $OUT_FILE 2> $OUT_FILE &
sleep 2
# create database
bin/ij <<< "CONNECT 'jdbc:derby://127.0.0.1:1527/metastore_db;create=true';"
echo -e "${RED}DERBY FINISHED${NC}"

cd $HIVE_HOME
# init metastore database
bin/schematool -dbType derby -initSchema -url jdbc:derby://127.0.0.1:1527/metastore_db -driver org.apache.derby.jdbc.ClientDriver > $OUT_FILE 2> $OUT_FILE
# start metastore server for multiple connections
nohup bin/hive --service metastore > $OUT_FILE 2> $OUT_FILE &
sleep 2
# create hive table
bin/beeline -u jdbc:hive2:// --showDbInPrompt=true < $SCRIPT_DIR/hive_init.sql > $OUT_FILE 2> $OUT_FILE
# terminal window with beeline for hive
x-terminal-emulator -e bin/beeline -u jdbc:hive2:///smartmonitoring --showDbInPrompt=true
echo -e "${RED}HIVE FINISHED${NC}"

# cd $SCRIPT_DIR/NilmAnalyticsMlApp
# source .venv/bin/activate
# nohup flask --app FlaskServer run > $OUT_FILE 2> $OUT_FILE &
# deactivate
# echo -e "${RED}FLASK FINISHED${NC}"

sudo systemctl start postgresql.service

