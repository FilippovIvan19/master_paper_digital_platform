package org.filippov.api.service;

import org.filippov.api.model.PredictedDevice;

import java.util.List;

public interface AnalyticsService {
    List<PredictedDevice> identifyDevicesForMonth(String monitorId, Integer year, Integer month);
}
