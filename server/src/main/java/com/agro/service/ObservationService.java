package com.agro.service;

import com.agro.dao.FieldDao;
import com.agro.dao.ObservationDao;
import com.agro.models.Observation;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ObservationService {
    private static final Logger LOGGER = Logger.getLogger(ObservationService.class.getName());
    private final ObservationDao observationDao;
    private final FieldDao fieldDao;
    public ObservationService() {
        this.observationDao = new ObservationDao();
        this.fieldDao = new FieldDao();
    }
    public ObservationService(ObservationDao observationDao, FieldDao fieldDao) {
        this.observationDao = observationDao;
        this.fieldDao = fieldDao;
    }
    public void createObservation(Observation obs) throws SQLException {
        validateObservation(obs);
        if (fieldDao.findById(obs.getFieldId()) == null) {
            LOGGER.log(Level.WARNING, "Поле з ID=" + obs.getFieldId() + " не існує");
            throw new IllegalArgumentException("Пов'язане поле з ID=" + obs.getFieldId() + " не знайдено в базі даних");
        }
        observationDao.create(obs);
        LOGGER.info("Створено спостереження ID=" + obs.getId() + " для поля ID=" + obs.getFieldId());
    }
    public Observation getObservationById(int id) throws SQLException {
        return observationDao.findById(id);
    }
    public List<Observation> getAllObservations() throws SQLException {
        return observationDao.findAll();
    }
    public boolean updateObservation(Observation obs) throws SQLException {
        validateObservation(obs);
        if (fieldDao.findById(obs.getFieldId()) == null) {
            throw new IllegalArgumentException("Пов'язане поле з ID=" + obs.getFieldId() + " не знайдено");
        }
        boolean updated = observationDao.update(obs);
        if (updated) {
            LOGGER.info("Оновлено спостереження ID=" + obs.getId());
        }
        return updated;
    }
    public boolean deleteObservation(int id) throws SQLException {
        boolean deleted = observationDao.delete(id);
        if (deleted) {
            LOGGER.info("Видалено спостереження ID=" + id);
        }
        return deleted;
    }
    public void validateObservation(Observation obs) {
        if (obs == null) {
            throw new IllegalArgumentException("Спостереження не може бути null");
        }
        if (obs.getFieldId() <= 0) {
            throw new IllegalArgumentException("Некоректний ID поля");
        }
        if (obs.getObservationDate() == null) {
            throw new IllegalArgumentException("Дата спостереження є обов'язковою");
        }
        if (obs.getInspectorName() == null || obs.getInspectorName().trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Помилка валідації! Порожнє ПІБ інспектора");
            throw new IllegalArgumentException("ПІБ інспектора є обов'язковим полем");
        }
    }
}