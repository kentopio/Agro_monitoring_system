package com.agro.service;

import com.agro.dao.ProblemDao;
import com.agro.dao.TreatmentDao;
import com.agro.models.Treatment;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TreatmentService {
    private static final Logger LOGGER = Logger.getLogger(TreatmentService.class.getName());
    private final TreatmentDao treatmentDao;
    private final ProblemDao problemDao;
    public TreatmentService() {
        this.treatmentDao = new TreatmentDao();
        this.problemDao = new ProblemDao();
    }
    public TreatmentService(TreatmentDao treatmentDao, ProblemDao problemDao) {
        this.treatmentDao = treatmentDao;
        this.problemDao = problemDao;
    }
    public void createTreatment(Treatment treatment) throws SQLException {
        validateTreatment(treatment);
        if (problemDao.findById(treatment.getProblemId()) == null) {
            LOGGER.log(Level.WARNING, "Проблема з ID=" + treatment.getProblemId() + " не існує");
            throw new IllegalArgumentException("Пов'язану проблему з ID=" + treatment.getProblemId() + " не знайдено");
        }
        treatmentDao.create(treatment);
        LOGGER.info("Створено захід обробки ID=" + treatment.getId() + " для проблеми ID=" + treatment.getProblemId());
    }
    public Treatment getTreatmentById(int id) throws SQLException {
        return treatmentDao.findById(id);
    }
    public List<Treatment> getAllTreatments() throws SQLException {
        return treatmentDao.findAll();
    }
    public boolean updateTreatment(Treatment treatment) throws SQLException {
        validateTreatment(treatment);
        if (problemDao.findById(treatment.getProblemId()) == null) {
            throw new IllegalArgumentException("Пов'язану проблему з ID=" + treatment.getProblemId() + " не знайдено");
        }
        boolean updated = treatmentDao.update(treatment);
        if (updated) {
            LOGGER.info("Оновлено захід обробки ID=" + treatment.getId());
        }
        return updated;
    }
    public boolean deleteTreatment(int id) throws SQLException {
        boolean deleted = treatmentDao.delete(id);
        if (deleted) {
            LOGGER.info("Видалено захід обробки ID=" + id);
        }
        return deleted;
    }
    public void validateTreatment(Treatment treatment) {
        if (treatment == null) {
            throw new IllegalArgumentException("Захід обробки не може бути null");
        }
        if (treatment.getProblemId() <= 0) {
            throw new IllegalArgumentException("Некоректний ID проблеми");
        }
        if (treatment.getTreatmentName() == null || treatment.getTreatmentName().trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Помилка валідації: порожня назва заходу обробки");
            throw new IllegalArgumentException("Назва заходу обробки є обов'язковою");
        }
        if (treatment.getPlannedDate() == null) {
            throw new IllegalArgumentException("Запланована дата обробки є обов'язковою");
        }
    }
}