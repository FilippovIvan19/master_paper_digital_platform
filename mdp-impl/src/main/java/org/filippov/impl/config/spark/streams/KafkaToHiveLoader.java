package org.filippov.impl.config.spark.streams;

import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.StructType;
import org.filippov.api.model.MonitorData.Columns;
import org.filippov.api.model.MonitorData.MonitorDataDto;
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
    protected KafkaConfiguration kafkaConfiguration;

    @Autowired
    protected SparkSession spark;

    @Async
    @EventListener(ApplicationStartedEvent.class)
    protected void startStreaming() {
        Column kafkaValue = col("value").cast(StringType);
        Encoder<MonitorDataDto> dtoEncoder = Encoders.bean(MonitorDataDto.class);
        StructType dtoSchema = dtoEncoder.schema();

        Dataset<Row> df = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", kafkaConfiguration.BOOTSTRAP_ADDRESS)
                .option("subscribe", kafkaConfiguration.INPUT_TOPIC)
                .load();
        Dataset<MonitorDataDto> monitorData = df
                .select(from_json(kafkaValue, dtoSchema).alias("monitor_data"))
                .selectExpr("monitor_data.*")
                .withColumn(Columns.TIMESTAMP, col(Columns.TIMESTAMP).cast(TimestampType))
                .as(dtoEncoder);

        try {
//            monitorData.writeStream()
//                    .format("console")
//                    .option("truncate", "false")
//                    .start().awaitTermination();

            monitorData
                    .writeStream()
                    .foreachBatch((batch, batchId) -> {
                        batch.write()
                                .format("hive")
                                .mode(SaveMode.Append)
                                .partitionBy(Columns.MONITOR_ID)
                                .saveAsTable("SmartMonitoring.MonitoringData");
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
