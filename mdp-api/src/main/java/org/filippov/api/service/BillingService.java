package org.filippov.api.service;

import java.math.BigDecimal;

public interface BillingService {
    BigDecimal getResourceAmountForMonth(String monitorId, Integer year, Integer month);
}
