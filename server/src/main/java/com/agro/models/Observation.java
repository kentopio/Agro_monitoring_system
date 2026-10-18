package com.agro.models;

import java.time.LocalDate;

public class Observation {
    private int id;
    private int fieldId;
    private LocalDate observationDate;
    private String inspectorName;

    public Observation() {
    }

    public Observation(int id, int fieldId, LocalDate observationDate, String inspectorName) {
        this.id = id;
        this.fieldId = fieldId;
        this.observationDate = observationDate;
        this.inspectorName = inspectorName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFieldId() { return fieldId; }
    public void setFieldId(int fieldId) { this.fieldId = fieldId; }

    public LocalDate getObservationDate() { return observationDate; }
    public void setObservationDate(LocalDate observationDate) { this.observationDate = observationDate; }

    public String getInspectorName() { return inspectorName; }
    public void setInspectorName(String inspectorName) { this.inspectorName = inspectorName; }
}