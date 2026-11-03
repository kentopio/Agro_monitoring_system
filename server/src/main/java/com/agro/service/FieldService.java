package com.agro.service;

import com.agro.dao.FieldDao;
import com.agro.models.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FieldService {
    private static final Logger LOGGER = Logger.getLogger(FieldService.class.getName());
    private final FieldDao fieldDao;
    public FieldService() {
        this.fieldDao = new FieldDao();
    }
    public FieldService(FieldDao fieldDao) {
        this.fieldDao = fieldDao;
    }
    public void createField(Field field) throws SQLException {
        validateField(field);
        fieldDao.create(field);
        LOGGER.info("Успішно створено поле: " + field.getName() + " (ID=" + field.getId() + ")");
    }
    public Field getFieldById(int id) throws SQLException {
        return fieldDao.findById(id);
    }
    public List<Field> getAllFields() throws SQLException {
        return fieldDao.findAll();
    }
    public boolean updateField(Field field) throws SQLException {
        validateField(field);
        boolean updated = fieldDao.update(field);
        if (updated) {
            LOGGER.info("Оновлено поле ID=" + field.getId());
        }
        return updated;
    }
    public boolean deleteField(int id) throws SQLException {
        boolean deleted = fieldDao.delete(id);
        if (deleted) {
            LOGGER.info("Видалено поле ID=" + id);
        }
        return deleted;
    }
    public void validateField(Field field) {
        if (field == null) {
            LOGGER.log(Level.WARNING, "Спроба зберегти порожній об'єкт Field");
            throw new IllegalArgumentException("Об'єкт поля не може бути null");
        }
        if (field.getName() == null || field.getName().trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Помилка валідації: порожня назва поля");
            throw new IllegalArgumentException("Назва поля є обов'язковою");
        }
        if (field.getCropType() == null || field.getCropType().trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Помилка валідації: порожній тип культури");
            throw new IllegalArgumentException("Тип сільськогосподарської культури є обов'язковим");
        }
        if (field.getArea() <= 0) {
            LOGGER.log(Level.WARNING, "Помилка валідації: некоректна площа поля " + field.getArea());
            throw new IllegalArgumentException("Площа поля повинна бути більшою за 0");
        }
    }
}