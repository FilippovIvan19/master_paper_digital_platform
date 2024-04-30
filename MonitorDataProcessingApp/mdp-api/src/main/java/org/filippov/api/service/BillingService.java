package org.filippov.api.service;

import org.filippov.api.model.dbentities.ResourceMonitor;

import java.math.BigDecimal;

public interface BillingService {
    BigDecimal getResourceAmountForMonth(String monitorId, Integer year, Integer month);
    void sendMonthInvoice(String monitorId);
    void sendMonthInvoice(ResourceMonitor monitor);
    void sendAllInvoices();
}
