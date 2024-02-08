package org.filippov.impl.service.integration;

import lombok.extern.slf4j.Slf4j;
import org.filippov.api.service.integration.EmergencyCallService;
import org.springframework.stereotype.Service;

// Sample of some outer system that automatically makes a phone call to emergency assistance office
@Slf4j
@Service
public class EmergencyCallServiceImpl implements EmergencyCallService {
    @Override
    public Object callEmergency(String address) {
        log.info("callEmergency - {}", address);
        return null;
    }
}
