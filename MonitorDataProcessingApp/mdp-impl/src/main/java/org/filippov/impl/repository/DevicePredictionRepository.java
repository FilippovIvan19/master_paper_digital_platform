package org.filippov.impl.repository;

import org.filippov.api.model.dbentities.DevicePredictionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevicePredictionRepository extends JpaRepository<DevicePredictionRecord, Long> {
    DevicePredictionRecord findByMonitorIdAndYearAndMonth(String monitorId, Integer year, Integer month);
}
