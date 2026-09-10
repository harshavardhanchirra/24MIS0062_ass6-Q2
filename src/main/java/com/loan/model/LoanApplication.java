package com.loan.model;

public class LoanApplication {
    private String applicationId;
    private Customer customer;
    private double requestedAmount;

    public LoanApplication(String applicationId, Customer customer, double requestedAmount) {
        this.applicationId = applicationId;
        this.customer = customer;
        this.requestedAmount = requestedAmount;
    }

    public String getApplicationId() { return applicationId; }
    public Customer getCustomer() { return customer; }
    public double getRequestedAmount() { return requestedAmount; }
}
