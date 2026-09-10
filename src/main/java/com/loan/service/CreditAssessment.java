package com.loan.service;

import com.loan.exception.InvalidInputException;
import com.loan.model.Customer;
import com.loan.model.LoanApplication;
import java.util.ArrayList;
import java.util.List;

public class CreditAssessment {

    // Threshold Constants
    private static final int MIN_AGE = 21;
    private static final int MIN_CREDIT_SCORE = 600;
    private static final int HIGH_CREDIT_SCORE = 750;
    private static final double MIN_MONTHLY_INCOME = 2000.0;
    private static final double MAX_DTI = 0.45; // 45% Boundary
    private static final double LOW_RISK_DTI = 0.30; // 30% Boundary
    private static final double INCOME_MULTIPLIER = 12.0; // Max permissible loan calculation rule

    public static class AssessmentResult {
        public String riskClass; // Low Risk, Medium Risk, High Risk/Rejected
        public double maxPermissibleLoan;
        public List<String> rejectionReasons = new ArrayList<>();

        @Override
        public String toString() {
            return "Risk Classification: " + riskClass + " | Max Permissible Loan: $" + maxPermissibleLoan +
                   (rejectionReasons.isEmpty() ? "" : " | Rejections: " + rejectionReasons);
        }
    }

    public AssessmentResult evaluateApplication(LoanApplication application) {
        if (application == null || application.getCustomer() == null) {
            throw new InvalidInputException("Application or Customer data cannot be null.");
        }

        Customer customer = application.getCustomer();
        validateInputs(customer, application.getRequestedAmount());

        AssessmentResult result = new AssessmentResult();
        
        // 1. Calculate Maximum Permissible Loan Amount
        result.maxPermissibleLoan = customer.getMonthlyIncome() * INCOME_MULTIPLIER;

        // 2. Compute Debt-to-Income (DTI) Ratio
        double dti = customer.getExistingMonthlyObligations() / customer.getMonthlyIncome();

        // 3. Evaluate and Collect Multiple Critical Rejection Rules
        if (customer.getAge() < MIN_AGE) {
            result.rejectionReasons.add("Underage: Customer must be at least " + MIN_AGE + " years old.");
        }
        if (customer.getIdNumber() == null || customer.getIdNumber().trim().isEmpty()) {
            result.rejectionReasons.add("Missing Identification: A valid government-issued ID number is required.");
        }
        if (customer.getMonthlyIncome() < MIN_MONTHLY_INCOME) {
            result.rejectionReasons.add("Insufficient Income: Minimum monthly income required is $" + MIN_MONTHLY_INCOME);
        }
        if (application.getRequestedAmount() > result.maxPermissibleLoan) {
            result.rejectionReasons.add("Loan Cap Exceeded: Requested amount exceeds the maximum permissible limit of $" + result.maxPermissibleLoan);
        }
        if (customer.getCreditScore() < MIN_CREDIT_SCORE) {
            result.rejectionReasons.add("Critical Credit Score: Score is below the required absolute minimum of " + MIN_CREDIT_SCORE);
        }
        if (dti > MAX_DTI) {
            result.rejectionReasons.add("Excessive Leverage: Debt-To-Income ratio exceeds the maximum permissible 45% threshold (Current DTI: " + String.format("%.2f", dti * 100) + "%).");
        }

        // 4. Assign Risk and Final Status Classification
        if (!result.rejectionReasons.isEmpty()) {
            result.riskClass = "High Risk/Rejected";
        } else {
            // Evaluates Low Risk vs Medium Risk
            if (customer.getCreditScore() >= HIGH_CREDIT_SCORE && dti <= LOW_RISK_DTI) {
                result.riskClass = "Low Risk";
            } else {
                result.riskClass = "Medium Risk";
            }
        }

        return result;
    }

    private void validateInputs(Customer customer, double requestedAmount) {
        if (customer.getAge() < 0 || customer.getMonthlyIncome() < 0 || customer.getCreditScore() < 0 || customer.getExistingMonthlyObligations() < 0 || requestedAmount <= 0) {
            throw new InvalidInputException("Financial values, age, and loan request amounts must be positive numbers.");
        }
    }
}
