package com.loan.service;

import com.loan.exception.InvalidInputException;
import com.loan.model.Customer;
import com.loan.model.LoanApplication;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreditAssessmentTest {

    private final CreditAssessment assessmentEngine = new CreditAssessment();

    @Test
    public void testLowRiskScenario() {
        Customer customer = new Customer("John Doe", 30, "ID9982", 6000.0, 780, 1200.0); // DTI = 20%
        LoanApplication app = new LoanApplication("APP01", customer, 20000.0);
        
        var result = assessmentEngine.evaluateApplication(app);
        assertEquals("Low Risk", result.riskClass);
        assertEquals(72000.0, result.maxPermissibleLoan);
        assertTrue(result.rejectionReasons.isEmpty());
    }

    @Test
    public void testBoundaryScenario_ExactLimits() {
        // Exactly 21 years old, exactly 600 credit score, and exactly 45% DTI (2250 / 5000)
        Customer customer = new Customer("Edge Case", 21, "ID1102", 5000.0, 600, 2250.0);
        LoanApplication app = new LoanApplication("APP02", customer, 60000.0); // Exactly at 12x multiplier limit
        
        var result = assessmentEngine.evaluateApplication(app);
        assertEquals("Medium Risk", result.riskClass); // Met absolute mins but DTI/Credit are moderate
        assertTrue(result.rejectionReasons.isEmpty());
    }

    @Test
    public void testMultipleRejectionsScenario() {
        // Underage (19), No ID, Low Credit Score (520), and exceeding income capacity
        Customer customer = new Customer("Invalid Applicant", 19, "", 3000.0, 520, 500.0);
        LoanApplication app = new LoanApplication("APP03", customer, 50000.0); // Limit is 36000
        
        var result = assessmentEngine.evaluateApplication(app);
        assertEquals("High Risk/Rejected", result.riskClass);
        // Asserts that multiple non-blocking reasons are captured simultaneously
        assertTrue(result.rejectionReasons.size() >= 4);
    }

    @Test
    public void testInputValidation_ThrowsException() {
        Customer customer = new Customer("Negative Value", 25, "ID001", -4000.0, 700, 1000.0);
        LoanApplication app = new LoanApplication("APP04", customer, 15000.0);

        assertThrows(InvalidInputException.class, () -> {
            assessmentEngine.evaluateApplication(app);
        });
    }
}
