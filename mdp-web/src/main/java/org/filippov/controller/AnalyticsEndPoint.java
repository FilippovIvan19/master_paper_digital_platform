package org.filippov.controller;

import lombok.extern.slf4j.Slf4j;
import org.filippov.api.model.PredictedDevice;
import org.filippov.api.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("analytics")
public class AnalyticsEndPoint {
    @Autowired
    private AnalyticsService analyticsService;

    @ResponseBody
    @GetMapping("identifyDevices")
    public List<PredictedDevice> fillTopicFromScv(@RequestParam String monitorId,
                                                  @RequestParam Integer year,
                                                  @RequestParam Integer month) {
        return analyticsService.identifyDevicesForMonth(monitorId, year, month);
    }
}
