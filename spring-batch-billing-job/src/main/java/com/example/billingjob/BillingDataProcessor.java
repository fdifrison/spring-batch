package com.example.billingjob;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

public class BillingDataProcessor implements ItemProcessor<BillingData, ReportingData> {

    private final PricingService pricingService;

    public BillingDataProcessor(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @Value("${spring.cellular.spending.threshold:150}")
    private float spendingThreshold;

    @Override
    public ReportingData process(BillingData item) throws Exception {
        double billingTotal = item.dataUsage() * pricingService.getDataPricing() +
                item.callDuration() * pricingService.getCallPricing() +
                item.smsCount() * pricingService.getSmsPricing();
        return billingTotal < spendingThreshold ? null : new ReportingData(item, billingTotal);
    }
}
