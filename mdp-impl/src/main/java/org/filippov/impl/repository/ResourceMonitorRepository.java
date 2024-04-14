package org.filippov.impl.repository;

import org.filippov.api.model.dbentities.ResourceMonitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceMonitorRepository extends JpaRepository<ResourceMonitor, String> {
//    ResourceMonitor findByMonitorIdAndYearAndMonth(String monitorId, Integer year, Integer month);
}
