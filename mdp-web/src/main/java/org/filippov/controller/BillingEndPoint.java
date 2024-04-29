package org.filippov.controller;

import lombok.extern.slf4j.Slf4j;
import org.filippov.api.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("billing")
public class BillingEndPoint {
    @Autowired
    private BillingService billingService;

    @ResponseBody
    @GetMapping("monthAmount")
    public BigDecimal getResourceAmountForMonth(@RequestParam String monitorId,
                                                @RequestParam Integer year,
                                                @RequestParam Integer month) {
        return billingService.getResourceAmountForMonth(monitorId, year, month);
    }

    @ResponseBody
    @GetMapping("monthInvoice")
    public void sendMonthInvoice(@RequestParam String monitorId) {
        billingService.sendMonthInvoice(monitorId);
    }
}
