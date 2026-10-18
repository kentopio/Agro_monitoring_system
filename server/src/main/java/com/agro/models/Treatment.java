package com.agro.models;

import java.time.LocalDate;

public class Treatment {
    private int id;
    private int problemId;
    private String treatmentName;
    private LocalDate plannedDate;
    private boolean isCompleted;

    public Treatment() {
    }

    public Treatment(int id, int problemId, String treatmentName, LocalDate plannedDate, boolean isCompleted) {
        this.id = id;
        this.problemId = problemId;
        this.treatmentName = treatmentName;
        this.plannedDate = plannedDate;
        this.isCompleted = isCompleted;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProblemId() { return problemId; }
    public void setProblemId(int problemId) { this.problemId = problemId; }

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }

    public LocalDate getPlannedDate() { return plannedDate; }
    public void setPlannedDate(LocalDate plannedDate) { this.plannedDate = plannedDate; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}