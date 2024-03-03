package org.filippov.impl.config.spark.streams;

import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DecimalType;
import org.apache.spark.sql.types.StructType;
import org.filippov.api.model.MonitorData;
import org.filippov.api.model.MonitorData.Columns;
import org.filippov.impl.config.kafka.KafkaConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;
import static org.apache.spark.sql.types.DataTypes.StringType;
import static org.apache.spark.sql.types.DataTypes.TimestampType;

@Service
public class KafkaToHiveLoader {
    @Autowired
    private KafkaConfiguration kafkaConfiguration;

    @Autowired
    private SparkSession spark;

    @Async
    @EventListener(ApplicationStartedEvent.class)
    protected void startStreaming() {
        for (int i = 0; i < 5; i++) {
            System.out.println("stream");
        }

        StructType userSchema = new StructType()
                .add(Columns.TIMESTAMP, StringType)
                .add(Columns.AMOUNT, DecimalType.apply(38,18))
                .add(Columns.FLAGS, StringType)
                .add("monitor_id", StringType);


        Dataset<Row> df = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", kafkaConfiguration.BOOTSTRAP_ADDRESS)
                .option("subscribe", kafkaConfiguration.INPUT_TOPIC)
//                .schema(userSchema)
                .load()
                .select(col("key").cast("string"), from_json(col("value").cast("string"), userSchema).alias("monitor_data"))
                .selectExpr("monitor_data.*") // todo refactor to be more clean and to save original kafka headers
                ;




        try {
//            df.writeStream()
//                    .format("console")
//                    .option("truncate", "false")
//                    .start().awaitTermination();

            Dataset<MonitorData> monitorData = df.as(Encoders.bean(MonitorData.class));

            monitorData
                    .withColumn(Columns.TIMESTAMP, col(Columns.TIMESTAMP).cast(TimestampType))
                    .writeStream()
                    .foreachBatch((batch, batchId) -> {
                        batch.write()
                                .mode(SaveMode.Append)
//                                .partitionBy("monitor_id") // todo to test
                                .insertInto("SmartMonitoring.MonitoringData"); // todo test saveAsTable
                    }).start().awaitTermination();
        } catch (TimeoutException | StreamingQueryException e) {
            for (int i = 0; i < 5; i++) {
                System.out.println("catch reached");
            }
            spark.stop();
            e.printStackTrace();
        }
    }

}
