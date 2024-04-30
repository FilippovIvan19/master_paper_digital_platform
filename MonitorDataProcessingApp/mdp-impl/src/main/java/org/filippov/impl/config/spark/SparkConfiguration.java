package org.filippov.impl.config.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SparkConfiguration {
    @Value("${app.name}")
    private String appName;

    @Value("${master.uri}")
    private String masterUri;

    @Bean
    public SparkConf sparkConf() {
        SparkConf sparkConf = new SparkConf()
                .setAppName(appName)
                .setMaster(masterUri)
                .set("spark.sql.hive.metastore.version", "3.1.3")
                .set("spark.sql.hive.metastore.jars", "path")
                .set("spark.sql.hive.metastore.jars.path", "file:///home/ivan/bigdata/hive/apache-hive-3.1.3-bin/lib/*")
                .set("hive.exec.dynamic.partition.mode", "nonstrict")
                ;
        return sparkConf;
    }

    @Bean
    public JavaSparkContext javaSparkContext() {
        return new JavaSparkContext(sparkConf());
    }

    @Bean
    public SparkSession sparkSession() {
        return SparkSession
                .builder()
                .sparkContext(javaSparkContext().sc())
                .appName(appName)
                .enableHiveSupport()
                .getOrCreate();
    }
}
