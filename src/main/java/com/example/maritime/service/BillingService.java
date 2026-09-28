package com.example.maritime.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculates dynamic docking fees and shore-power fees.
 *
 * Docking fee rule (occupancy multiplier tiers):
 *   0-4 hours   -> 1.00 x base hourly rate
 *   4-8 hours   -> 1.25 x base hourly rate
 *   more than 8 -> 1.50 x base hourly rate
 *
 * Example: base rate = Rs 5000/hr, occupancy = 6 hours
 *          fee = 6 x 5000 x 1.25 = Rs 37,500
 *
 * Shore-power fee = kWh consumed x electricity rate (Rs/kWh, from application.properties)
 */
@Service
public class BillingService {

    @Value("${maritime.shorepower.rate-per-kwh:12}")
    private double electricityRatePerKwh;

    public BigDecimal calculateDockingFee(double occupancyHours, BigDecimal baseRatePerHour) {
        BigDecimal multiplier;
        if (occupancyHours <= 4) {
            multiplier = new BigDecimal("1.00");
        } else if (occupancyHours <= 8) {
            multiplier = new BigDecimal("1.25");
        } else {
            multiplier = new BigDecimal("1.50");
        }
        BigDecimal hours = BigDecimal.valueOf(occupancyHours);
        return baseRatePerHour.multiply(hours).multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateShorePowerFee(double shorePowerKwh) {
        return BigDecimal.valueOf(shorePowerKwh)
                .multiply(BigDecimal.valueOf(electricityRatePerKwh))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public double getElectricityRatePerKwh() {
        return electricityRatePerKwh;
    }
}
