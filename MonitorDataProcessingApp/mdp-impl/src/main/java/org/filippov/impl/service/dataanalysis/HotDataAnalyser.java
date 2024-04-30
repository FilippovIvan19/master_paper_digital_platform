package org.filippov.impl.service.dataanalysis;

import org.apache.kafka.common.serialization.Serde;
import org.filippov.api.model.MonitorData;
import org.filippov.api.model.MonitorData.Flag;
import org.filippov.api.service.dataanalysis.DataAnalyser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Qualifier("hotDataAnalyser")
public class HotDataAnalyser implements DataAnalyser<HotDataAnalyticsDto> {

    @Override
    public HotDataAnalyticsDto analyse(List<MonitorData> data) {
        HotDataAnalyticsDto analytics = new HotDataAnalyticsDto();
        analytics.setMonitorId(data.get(0).getMonitorId());
        analytics.setIncident(checkIncident(data));
        analytics.setIntervention(containsFlag(data, Flag.INTERVENTION_WARNING) || checkIntervention(data));
        analytics.setCrash(containsFlag(data, Flag.CRASH_WARNING));
        analytics.setLowButtery(containsFlag(data, Flag.LOW_BATTERY));
        return analytics;
    }

    @Override
    public Serde<HotDataAnalyticsDto> getDtoSerde() {
        return HotDataAnalyticsDto.getSerde();
    }

    @Override
    public Class<HotDataAnalyticsDto> getDtoClass() {
        return HotDataAnalyticsDto.class;
    }

    private boolean containsFlag(List<MonitorData> data, Flag flag) {
        return data.stream().anyMatch(d -> d.getFlags().getEnumSet().contains(flag));
    }

    private boolean checkIncident(List<MonitorData> data) {
        return false;
    }

    private boolean checkIntervention(List<MonitorData> data) {
        return false;
    }
}
