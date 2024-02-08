package org.filippov.api.service.dataanalysis;

public interface AnalyticsHandler<T extends AnalyticsDto> {
    void handle(T input);
}
