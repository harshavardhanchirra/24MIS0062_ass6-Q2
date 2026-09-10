package com.loan.model;

public class Customer {
    private String name;
    private int age;
    private String idNumber; // Government-issued ID
    private double monthlyIncome;
    private int creditScore;
    private double existingMonthlyObligations;

    public Customer(String name, int age, String idNumber, double monthlyIncome, int creditScore, double existingMonthlyObligations) {
        this.name = name;
        this.age = age;
        this.idNumber = idNumber;
        this.monthlyIncome = monthlyIncome;
        this.creditScore = creditScore;
        this.existingMonthlyObligations = existingMonthlyObligations;
    }

    // Getters
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getIdNumber() { return idNumber; }
    public double getMonthlyIncome() { return monthlyIncome; }
    public int getCreditScore() { return creditScore; }
    public double getExistingMonthlyObligations() { return existingMonthlyObligations; }
}
