package com.example.recommend.agent;
/**
 * Collected slots for the car-loan recommendation conversation.
 * Null means "not yet provided".
 */
public class UserProfile {
    private String carModel;          // Q1
    private Double carPrice;          // Q2 (in INR)
    private String carType;           // Q2
    private Double downPayment;       // Q3
    private Double monthlyIncome;     // Q4
    private Double preferredEmi;      // Q5
    private Integer tenureMonths;     // Q6
    private Integer creditScore;      // Q7 (optional)
    public String getCarModel() { return carModel; }
    public void setCarModel(String carModel) { this.carModel = carModel; }
    public Double getCarPrice() { return carPrice; }
    public void setCarPrice(Double carPrice) { this.carPrice = carPrice; }
    public String getCarType() { return carType; }
    public void setCarType(String carType) { this.carType = carType; }
    public Double getDownPayment() { return downPayment; }
    public void setDownPayment(Double downPayment) { this.downPayment = downPayment; }
    public Double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(Double monthlyIncome) { this.monthlyIncome = monthlyIncome; }
    public Double getPreferredEmi() { return preferredEmi; }
    public void setPreferredEmi(Double preferredEmi) { this.preferredEmi = preferredEmi; }
    public Integer getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(Integer tenureMonths) { this.tenureMonths = tenureMonths; }
    public Integer getCreditScore() { return creditScore; }
    public void setCreditScore(Integer creditScore) { this.creditScore = creditScore; }
}