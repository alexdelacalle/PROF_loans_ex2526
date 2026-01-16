package es.upm.grise.profundizacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class LoanApprovalServiceTest {
    
    private final LoanApprovalService service = new LoanApprovalService();

    @Test
    void shouldThrowWhenApplicantIsNull() {
        assertThrows(NullPointerException.class,
                () -> service.evaluateLoan(null, 1000, 12));
    }

    @Test
    void shouldThrowWhenAmountIsZeroOrNegative() {
        LoanApprovalService.Applicant a =
                new LoanApprovalService.Applicant(3000, 700, false, false);

        assertThrows(IllegalArgumentException.class,
                () -> service.evaluateLoan(a, 0, 12));
    }

    @Test
    void shouldThrowWhenTermIsOutOfRange() {
        LoanApprovalService.Applicant a =
                new LoanApprovalService.Applicant(3000, 700, false, false);

        assertThrows(IllegalArgumentException.class,
                () -> service.evaluateLoan(a, 1000, 3));
        assertThrows(IllegalArgumentException.class,
                () -> service.evaluateLoan(a, 1000, 100));
    }

    @Test
    void shouldThrowWhenIncomeIsNonPositive() {
        LoanApprovalService.Applicant a =
                new LoanApprovalService.Applicant(0, 700, false, false);

        assertThrows(IllegalArgumentException.class,
                () -> service.evaluateLoan(a, 1000, 12));
    }

    @Test
    void shouldThrowWhenScoreIsOutOfRange() {
        LoanApprovalService.Applicant a1 =
                new LoanApprovalService.Applicant(3000, -1, false, false);
        LoanApprovalService.Applicant a2 =
                new LoanApprovalService.Applicant(3000, 900, false, false);

        assertThrows(IllegalArgumentException.class,
                () -> service.evaluateLoan(a1, 1000, 12));
        assertThrows(IllegalArgumentException.class,
                () -> service.evaluateLoan(a2, 1000, 12));
    }

    // ---------- DECISION LOGIC ----------

    @Test
    void shouldRejectWhenScoreBelow500() {
        LoanApprovalService.Applicant a =
                new LoanApprovalService.Applicant(4000, 450, false, false);
        assertEquals(LoanApprovalService.Decision.REJECTED,
                service.evaluateLoan(a, 1000, 12));
    }

    @Test
    void shouldManualReviewWhenScoreBetween500And649AndGoodIncomeNoDefaults() {
        LoanApprovalService.Applicant a =
                new LoanApprovalService.Applicant(3000, 600, false, false);

        assertEquals(LoanApprovalService.Decision.MANUAL_REVIEW,
                service.evaluateLoan(a, 1000, 12));
    }
    
    @Test
    void shouldRejectWhenScoreBetween500And649AndLowIncomeOrDefaults() {
        LoanApprovalService.Applicant lowIncome =
                new LoanApprovalService.Applicant(2000, 600, false, false);
        LoanApprovalService.Applicant hasDefaults =
                new LoanApprovalService.Applicant(3000, 600, true, false);

        assertEquals(LoanApprovalService.Decision.REJECTED,
                service.evaluateLoan(lowIncome, 1000, 12));
        assertEquals(LoanApprovalService.Decision.REJECTED,
                service.evaluateLoan(hasDefaults, 1000, 12));
    }

    @Test
    void shouldApproveWhenScoreAtLeast650AndAmountWithinLimit() {
        LoanApprovalService.Applicant a =
                new LoanApprovalService.Applicant(3000, 700, false, false);

        assertEquals(LoanApprovalService.Decision.APPROVED,
                service.evaluateLoan(a, 24000, 12)); // 3000 * 8 = 24000
    }

    @Test
    void shouldManualReviewWhenScoreAtLeast650AndAmountTooHigh() {
        LoanApprovalService.Applicant a =
                new LoanApprovalService.Applicant(3000, 700, false, false);

        assertEquals(LoanApprovalService.Decision.MANUAL_REVIEW,
                service.evaluateLoan(a, 30000, 12));
    }

    @Test
    void shouldUpgradeToApprovedForVipInManualReview() {
        LoanApprovalService.Applicant a =
                new LoanApprovalService.Applicant(3000, 620, false, true);

        assertEquals(LoanApprovalService.Decision.APPROVED,
                service.evaluateLoan(a, 1000, 12));
    }

    @Test
    void shouldNotUpgradeIfVipButHasDefaults() {
        LoanApprovalService.Applicant a =
                new LoanApprovalService.Applicant(3000, 620, true, true);

        assertEquals(LoanApprovalService.Decision.REJECTED,
                service.evaluateLoan(a, 1000, 12));
    }
    
    @Test
    void shouldStayManualReviewWhenNotVip() {
        LoanApprovalService.Applicant a =
                new LoanApprovalService.Applicant(3000, 620, false, false);
        // Base decision = MANUAL_REVIEW, no es VIP → no se eleva

        assertEquals(LoanApprovalService.Decision.MANUAL_REVIEW,
                service.evaluateLoan(a, 1000, 12));
    }


}
