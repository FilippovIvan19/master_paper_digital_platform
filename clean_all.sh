#!/bin/bash

# you need sudo to run this script because of postgres service managing

rm out

cd $KAFKA_HOME
# stop kafka services
bin/zookeeper-server-stop.sh config/zookeeper.properties
bin/kafka-server-stop.sh config/server.properties

cd $HIVE_HOME
pgrep -f org.apache.hadoop.hive.metastore.HiveMetaStore | xargs -r kill

cd $DERBY_HOME
# clean up derby db
bin/stopNetworkServer
rm    derby.log
rm -r metastore_db

cd $HADOOP_HOME
sbin/stop-dfs.sh
# clear incorrect state
rm -r /tmp/hadoop-*
bin/hdfs namenode -format -force

pgrep -f NilmAnalyticsMlApp/.venv/bin/flask | xargs -r kill

sudo systemctl stop postgresql.service

