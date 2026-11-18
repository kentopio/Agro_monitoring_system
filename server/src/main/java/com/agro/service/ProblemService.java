package com.agro.service;

import com.agro.dao.ObservationDao;
import com.agro.dao.ProblemDao;
import com.agro.models.Problem;
import com.agro.models.ProblemStatus;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProblemService {
    private static final Logger LOGGER = Logger.getLogger(ProblemService.class.getName());
    private final ProblemDao problemDao;
    private final ObservationDao observationDao;
    public ProblemService() {
        this.problemDao = new ProblemDao();
        this.observationDao = new ObservationDao();
    }
    public ProblemService(ProblemDao problemDao, ObservationDao observationDao) {
        this.problemDao = problemDao;
        this.observationDao = observationDao;
    }
    public void createProblem(Problem problem) throws SQLException {
        validateProblem(problem);
        if (observationDao.findById(problem.getObservationId()) == null) {
            LOGGER.log(Level.WARNING, "Спостереження з ID=" + problem.getObservationId() + " не існує");
            throw new IllegalArgumentException("Пов'язане спостереження з ID=" + problem.getObservationId() + " не знайдено");
        }
        if (problem.getStatus() == null) {
            problem.setStatus(ProblemStatus.ВИЯВЛЕНО);
        }
        problemDao.create(problem);
        LOGGER.info("Створено проблему ID=" + problem.getId() + " зі статусом " + problem.getStatus()
                + ", інтегральний показник I=" + calculateIntegralIndicator(problem));
    }
    public double calculateIntegralIndicator(int f1, int f2, int f3) {
        validateFactors(f1, f2, f3);
        return Math.round((0.40 * f1 + 0.35 * f2 + 0.25 * f3) * 100.0) / 100.0;
    }
    public double calculateIntegralIndicator(Problem problem) {
        return calculateIntegralIndicator(
                problem.getF1InfectedArea(),
                problem.getF2Severity(),
                problem.getF3Sensitivity()
        );
    }
    public boolean isTransitionAllowed(ProblemStatus current, ProblemStatus next) {
        if (current == null || next == null) {
            return false;
        }
        return switch (current) {
            case ВИЯВЛЕНО -> next == ProblemStatus.ПІДТВЕРДЖЕНО;
            case ПІДТВЕРДЖЕНО -> next == ProblemStatus.ОБРОБКУ_ЗАПЛАНОВАНО;
            case ОБРОБКУ_ЗАПЛАНОВАНО -> next == ProblemStatus.УСУНЕНО;
            case УСУНЕНО -> false;
        };
    }
    public void changeStatus(int problemId, ProblemStatus newStatus) throws SQLException {
        Problem problem = problemDao.findById(problemId);
        if (problem == null) {
            throw new IllegalArgumentException("Проблему з ID=" + problemId + " не знайдено");
        }
        ProblemStatus currentStatus = problem.getStatus();
        if (!isTransitionAllowed(currentStatus, newStatus)) {
            LOGGER.log(Level.WARNING, "Заборонений перехід стану: " + currentStatus + " -> " + newStatus);
            throw new IllegalStateException("Недопустимий перехід життєвого циклу: з '"
                    + currentStatus + "' у '" + newStatus + "'");
        }
        problem.setStatus(newStatus);
        problemDao.update(problem);
        LOGGER.info("Статус проблеми ID=" + problemId + " змінено: " + currentStatus + " -> " + newStatus);
    }
    public Problem getProblemById(int id) throws SQLException {
        return problemDao.findById(id);
    }
    public List<Problem> getAllProblems() throws SQLException {
        return problemDao.findAll();
    }
    public boolean updateProblem(Problem problem) throws SQLException {
        validateProblem(problem);
        if (observationDao.findById(problem.getObservationId()) == null) {
            throw new IllegalArgumentException("Пов'язане спостереження з ID=" + problem.getObservationId() + " не знайдено");
        }
        boolean updated = problemDao.update(problem);
        if (updated) {
            LOGGER.info("Оновлено проблему ID=" + problem.getId());
        }
        return updated;
    }
    public boolean deleteProblem(int id) throws SQLException {
        boolean deleted = problemDao.delete(id);
        if (deleted) {
            LOGGER.info("Видалено проблему ID=" + id);
        }
        return deleted;
    }
    public void validateProblem(Problem problem) {
        if (problem == null) {
            throw new IllegalArgumentException("Проблема не може бути null");
        }
        if (problem.getObservationId() <= 0) {
            throw new IllegalArgumentException("Некоректний ID спостереження");
        }
        if (problem.getDescription() == null || problem.getDescription().trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Помилка валідації: порожній опис проблеми");
            throw new IllegalArgumentException("Опис проблеми є обов'язковим полем");
        }
        validateFactors(problem.getF1InfectedArea(), problem.getF2Severity(), problem.getF3Sensitivity());
    }
    public void validateFactors(int f1, int f2, int f3) {
        if (f1 < 0 || f1 > 100 || f2 < 0 || f2 > 100 || f3 < 0 || f3 > 100) {
            LOGGER.log(Level.WARNING, "Фактори виходять за межі діапазону 0-100: F1=" + f1 + ", F2=" + f2 + ", F3=" + f3);
            throw new IllegalArgumentException("Значення факторів F1, F2, F3 повинні бути в діапазоні від 0 до 100");
        }
    }
}