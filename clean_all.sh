#!/bin/bash

# do not use sudo to run this script, it will cause issues with unavailability of some service directories (like logs, tmp, pids, etc)

rm out

cd $KAFKA_HOME
# stop kafka services
bin/kafka-server-stop.sh config/server.properties
bin/zookeeper-server-stop.sh config/zookeeper.properties

cd $HIVE_HOME
pgrep -f org.apache.hadoop.hive.metastore.HiveMetaStore | xargs -r kill

cd $DERBY_HOME
# clean up derby db
bin/stopNetworkServer
rm    derby.log
rm -r metastore_db

cd $HADOOP_HOME
sbin/stop-dfs.sh > /dev/null 2> /dev/null
# clear incorrect state
rm -r /tmp/hadoop-*
bin/hdfs namenode -format -force > /dev/null 2> /dev/null

pgrep -f NilmAnalyticsMlApp/.venv/bin/flask | xargs -r kill

sudo systemctl stop postgresql.service

