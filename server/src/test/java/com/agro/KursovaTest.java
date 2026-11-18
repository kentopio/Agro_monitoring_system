package com.agro;

import com.agro.models.Field;
import com.agro.models.Problem;
import com.agro.models.ProblemStatus;
import com.agro.service.FieldService;
import com.agro.service.ProblemService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class KursovaTest {
    private final ProblemService problemService = new ProblemService();
    private final FieldService fieldService = new FieldService();
    @Test
    void integralIndicatorForZeroFactorsIsZero() {
        assertEquals(0.0, problemService.calculateIntegralIndicator(0, 0, 0));
    }
    @Test
    void integralIndicatorForDemoDataIs62() {
        assertEquals(62.0, problemService.calculateIntegralIndicator(60, 80, 40));
    }
    @Test
    void integralIndicatorForMaxFactorsIs100() {
        assertEquals(100.0, problemService.calculateIntegralIndicator(100, 100, 100));
    }
    @Test
    void indicatorRejectsFactorAbove100() {
        assertThrows(IllegalArgumentException.class,
                () -> problemService.calculateIntegralIndicator(101, 50, 50));
    }
    @Test
    void detectedToConfirmedAllowed() {
        assertTrue(problemService.isTransitionAllowed(
                ProblemStatus.ВИЯВЛЕНО, ProblemStatus.ПІДТВЕРДЖЕНО));
    }
    @Test
    void confirmedToPlannedAllowed() {
        assertTrue(problemService.isTransitionAllowed(
                ProblemStatus.ПІДТВЕРДЖЕНО, ProblemStatus.ОБРОБКУ_ЗАПЛАНОВАНО));
    }
    @Test
    void plannedToResolvedAllowed() {
        assertTrue(problemService.isTransitionAllowed(
                ProblemStatus.ОБРОБКУ_ЗАПЛАНОВАНО, ProblemStatus.УСУНЕНО));
    }
    @Test
    void detectedToResolvedRejected() {
        assertFalse(problemService.isTransitionAllowed(
                ProblemStatus.ВИЯВЛЕНО, ProblemStatus.УСУНЕНО));
    }
    @Test
    void fieldValidationRejectsEmptyName() {
        assertThrows(IllegalArgumentException.class,
                () -> fieldService.validateField(new Field(1, "", "Пшениця", 50.0)));
    }
    @Test
    void problemValidationRejectsInvalidFactor() {
        assertThrows(IllegalArgumentException.class,
                () -> problemService.validateProblem(new Problem(1, 1, "Шкідники", ProblemStatus.ВИЯВЛЕНО, 30, 120, 40)));
    }
}