package es.upm.grise.profundizacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class LoanApprovalServiceTest {
    
    private final LoanApprovalService service = new LoanApprovalService();

    @Test
    void path1_scoreBelow500() {
        var a = new LoanApprovalService.Applicant(3000, 450, false, false);
        assertEquals(LoanApprovalService.Decision.REJECTED,
                service.evaluateLoan(a, 1000, 12));
    }

    // C2: 500 ≤ score < 650, income alto, sin defaults, no VIP → MANUAL_REVIEW
    @Test
    void path2_midScoreGoodIncomeNoDefaultsNotVip() {
        var a = new LoanApprovalService.Applicant(3000, 600, false, false);
        assertEquals(LoanApprovalService.Decision.MANUAL_REVIEW,
                service.evaluateLoan(a, 1000, 12));
    }

    // C3: 500 ≤ score < 650 y (income bajo o defaults) → REJECTED
    @Test
    void path3_midScoreLowIncome() {
        var a = new LoanApprovalService.Applicant(2000, 600, false, false);
        assertEquals(LoanApprovalService.Decision.REJECTED,
                service.evaluateLoan(a, 1000, 12));
    }

    // C4: score ≥ 650 y amount ≤ income * 8 → APPROVED
    @Test
    void path4_highScoreAffordableAmount() {
        var a = new LoanApprovalService.Applicant(3000, 700, false, false);
        assertEquals(LoanApprovalService.Decision.APPROVED,
                service.evaluateLoan(a, 24000, 12));
    }

    // C5: score ≥ 650 y amount > income * 8, no VIP → MANUAL_REVIEW
    @Test
    void path5_highScoreTooHighAmountNotVip() {
        var a = new LoanApprovalService.Applicant(3000, 700, false, false);
        assertEquals(LoanApprovalService.Decision.MANUAL_REVIEW,
                service.evaluateLoan(a, 30000, 12));
    }

    // C6: upgrade VIP desde MANUAL_REVIEW → APPROVED
    @Test
    void path6_vipUpgradeFromManualReview() {
        var a = new LoanApprovalService.Applicant(3000, 620, false, true);
        assertEquals(LoanApprovalService.Decision.APPROVED,
                service.evaluateLoan(a, 1000, 12));
    }

    @Test
    void path3b_midScoreGoodIncomeButHasDefaults() {
        var a = new LoanApprovalService.Applicant(3000, 600, true, false);
        assertEquals(LoanApprovalService.Decision.REJECTED,
                service.evaluateLoan(a, 1000, 12));
    }
    



}
