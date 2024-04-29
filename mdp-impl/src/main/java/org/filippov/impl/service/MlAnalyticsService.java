package org.filippov.impl.service;

import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.filippov.api.model.MonitorData.Columns;
import org.filippov.api.model.MonitorData.MonitorDataDto;
import org.filippov.api.model.TimeSeries;
import org.filippov.api.model.dbentities.DevicePredictionRecord;
import org.filippov.api.model.dbentities.PredictedDevice;
import org.filippov.api.service.AnalyticsService;
import org.filippov.impl.repository.DevicePredictionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.spark.sql.functions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class MlAnalyticsService implements AnalyticsService {
    @Autowired
    private SparkSession spark;

    @Autowired
    private DevicePredictionRepository devicePredictionRepository;

    private final WebClient webClient = WebClient.create();

    @Value("${nilm-analytics.ml-app.server.url}")
    private String NilmServerUrl;

    private final ParameterizedTypeReference<List<PredictedDevice>> predictedDeviceListType =
            new ParameterizedTypeReference<List<PredictedDevice>>() {};
    private final Encoder<MonitorDataDto> dtoEncoder = Encoders.bean(MonitorDataDto.class);


    @Override
    public List<PredictedDevice> identifyDevicesForMonth(String monitorId, Integer year, Integer month) {
        DevicePredictionRecord predictionRecord = devicePredictionRepository.findByMonitorIdAndYearAndMonth(monitorId, year, month);
        if (predictionRecord != null) {
            return predictionRecord.getPredictedDevices();
        }

////        for debug
//        spark.table("SmartMonitoring.MonitoringData")
//                .where(col(Columns.MONITOR_ID).equalTo(monitorId)
//                        .and(year(col(Columns.TIMESTAMP)).equalTo(year))
//                        .and(month(col(Columns.TIMESTAMP)).equalTo(month))
//                )
//                .show();

        // move to SparkRepository
        List<MonitorDataDto> data = spark.table("SmartMonitoring.MonitoringData")
                .where(col(Columns.MONITOR_ID).equalTo(monitorId)
                        .and(year(col(Columns.TIMESTAMP)).equalTo(year))
                        .and(month(col(Columns.TIMESTAMP)).equalTo(month))
                )
                .as(dtoEncoder)
                .collectAsList();

        List<String> timestamps = data.stream().map(MonitorDataDto::getTimestamp).collect(Collectors.toList());
        List<BigDecimal> amounts = data.stream().map(MonitorDataDto::getAmount).collect(Collectors.toList());
        TimeSeries timeSeries = new TimeSeries(timestamps, amounts);

        List<PredictedDevice> result = webClient.post()
                .uri(NilmServerUrl + "/identifyDevices")
                .contentType(APPLICATION_JSON)
                .bodyValue(timeSeries)
                .retrieve()
                .bodyToMono(predictedDeviceListType)
                .block();

        predictionRecord = new DevicePredictionRecord(null, monitorId, year, month, result);
        for (PredictedDevice device : result) {
            device.setPredictionRecord(predictionRecord);
        }
        devicePredictionRepository.save(predictionRecord);
        return result;
    }
}
