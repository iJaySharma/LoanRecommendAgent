package com.example.recommend.service;

import org.springframework.stereotype.Component;
@Component
public class EmiCalculator {
    /**
     * Standard EMI formula:
     * EMI = P * r * (1+r)^n / ((1+r)^n - 1)
     *   P = principal, r = monthly interest rate, n = tenure in months.
     */
    public double computeEmi(double principal, double annualRatePct, int tenureMonths) {
        if (tenureMonths <= 0) return principal;
        double r = (annualRatePct / 100.0) / 12.0;
        if (r == 0) return principal / tenureMonths;
        double pow = Math.pow(1 + r, tenureMonths);
        return principal * r * pow / (pow - 1);
    }
    public double totalInterest(double emi, int tenureMonths, double principal) {
        return (emi * tenureMonths) - principal;
    }
}
