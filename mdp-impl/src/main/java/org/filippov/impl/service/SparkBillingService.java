package org.filippov.impl.service;

import org.apache.spark.sql.SparkSession;
import org.filippov.api.model.MonitorData.Columns;
import org.filippov.api.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.year;
import static org.apache.spark.sql.functions.month;

@Service
public class SparkBillingService implements BillingService {
    @Autowired
    private SparkSession spark;

    @Override
    public BigDecimal getResourceAmountForMonth(String monitorId, Integer year, Integer month) {
//        spark.sql("USE SmartMonitoring");
//        spark.sql("SELECT * FROM MonitoringData").show();
////        StructType schema = spark.table("SmartMonitoring.MonitoringData").schema();
//
////        SQLContext sqlContext = new HiveContext(spark.sparkContext());
////        sqlContext.sql("SELECT * FROM SmartMonitoring.MonitoringData").show();
//
//        spark.sql("SHOW PARTITIONS MonitoringData").show();

        spark.table("SmartMonitoring.MonitoringData")
                .where(col("monitor_id").equalTo(monitorId)
                        .and(year(col(Columns.TIMESTAMP)).equalTo(year))
                        .and(month(col(Columns.TIMESTAMP)).equalTo(month))
                )
                .show();

        return BigDecimal.ZERO;
    }
}
