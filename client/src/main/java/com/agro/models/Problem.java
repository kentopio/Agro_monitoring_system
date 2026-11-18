package com.agro.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Problem {
    private int id;
    private int observationId;
    private String description;
    private ProblemStatus status;
    private int f1InfectedArea;
    private int f2Severity;
    private int f3Sensitivity;
    private double integralIndicator;
    public Problem() {
    }
    public Problem(int id, int observationId, String description, ProblemStatus status,
                   int f1InfectedArea, int f2Severity, int f3Sensitivity) {
        this.id = id;
        this.observationId = observationId;
        this.description = description;
        this.status = status;
        this.f1InfectedArea = f1InfectedArea;
        this.f2Severity = f2Severity;
        this.f3Sensitivity = f3Sensitivity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getObservationId() { return observationId; }
    public void setObservationId(int observationId) { this.observationId = observationId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ProblemStatus getStatus() { return status; }
    public void setStatus(ProblemStatus status) { this.status = status; }
    public int getF1InfectedArea() { return f1InfectedArea; }
    public void setF1InfectedArea(int f1InfectedArea) { this.f1InfectedArea = f1InfectedArea; }
    public int getF2Severity() { return f2Severity; }
    public void setF2Severity(int f2Severity) { this.f2Severity = f2Severity; }
    public int getF3Sensitivity() { return f3Sensitivity; }
    public void setF3Sensitivity(int f3Sensitivity) { this.f3Sensitivity = f3Sensitivity; }
    public double getIntegralIndicator() { return integralIndicator; }
    public void setIntegralIndicator(double integralIndicator) { this.integralIndicator = integralIndicator; }
}