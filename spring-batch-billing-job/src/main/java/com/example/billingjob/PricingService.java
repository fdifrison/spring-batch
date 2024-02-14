package com.example.billingjob;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

import java.util.Random;

public class PricingService {
    @Value("${spring.cellular.pricing.data:0.01}")
    private float dataPricing;

    @Getter
    @Value("${spring.cellular.pricing.call:0.5}")
    private float callPricing;

    @Getter
    @Value("${spring.cellular.pricing.sms:0.1}")
    private float smsPricing;

    private final Random random = new Random();

    public float getDataPricing() {
        // We want to simulate a flaky web service that may sometimes fail to respond
        if (this.random.nextInt(1000) % 7 == 0) {
            throw new PricingException("Error while retrieving data pricing");
        }
        return this.dataPricing;
    }

}
