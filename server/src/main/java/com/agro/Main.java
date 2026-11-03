package com.agro;

import com.agro.models.*;
import com.agro.service.*;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== ДЕМОНСТРАЦІЯ ЕТАПУ II: DAO, CRUD, SERVICE, ВАЛІДАЦІЯ ТА ЖИТТЄВИЙ ЦИКЛ ===");
        FieldService fieldService = new FieldService();
        ObservationService observationService = new ObservationService();
        ProblemService problemService = new ProblemService();
        TreatmentService treatmentService = new TreatmentService();
        try {
            Field field = new Field(0, "Поле №5 Північне", "Озима пшениця", 85.5);
            fieldService.createField(field);

            Observation obs = new Observation(0, field.getId(), LocalDate.now(), "Ілля Наретя");
            observationService.createObservation(obs);

            Problem problem = new Problem(0, obs.getId(), "Виявлено осередки борошнистої роси",
                    ProblemStatus.ВИЯВЛЕНО, 60, 80, 40);
            problemService.createProblem(problem);
            double indicator = problemService.calculateIntegralIndicator(problem);
            System.out.println("-> Розрахований інтегральний показник I = " + indicator);

            problemService.changeStatus(problem.getId(), ProblemStatus.ПІДТВЕРДЖЕНО);

            Treatment treatment = new Treatment(0, problem.getId(), "Обприскування фунгіцидом", LocalDate.now().plusDays(2), false);
            treatmentService.createTreatment(treatment);
            problemService.changeStatus(problem.getId(), ProblemStatus.ОБРОБКУ_ЗАПЛАНОВАНО);

            treatment.setCompleted(true);
            treatmentService.updateTreatment(treatment);
            problemService.changeStatus(problem.getId(), ProblemStatus.УСУНЕНО);

            try {
                problemService.changeStatus(problem.getId(), ProblemStatus.ВИЯВЛЕНО);
            } catch (IllegalStateException ex) {
                System.out.println("-> Перевірка захисту життєвого циклу: " + ex.getMessage());
            }

            try {
                problemService.calculateIntegralIndicator(150, 50, 50);
            } catch (IllegalArgumentException ex) {
                System.out.println("-> Перевірка валідації факторів: " + ex.getMessage());
            }

            System.out.println("-> Усього полів у БД: " + fieldService.getAllFields().size());
            System.out.println("-> Усього проблем у БД: " + problemService.getAllProblems().size());
            System.out.println("=== УСІ ОПЕРАЦІЇ ЕТАПУ II ВИКОНАНО УСПІШНО! ===");
        } catch (Exception e) {
            System.err.println("Помилка виконання: " + e.getMessage());
        }
    }
}