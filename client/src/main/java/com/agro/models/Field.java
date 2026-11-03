package com.agro.models;

public class Field {
    private int id;
    private String name;
    private String cropType;
    private double area;
    public Field() {
    }
    public Field(int id, String name, String cropType, double area) {
        this.id = id;
        this.name = name;
        this.cropType = cropType;
        this.area = area;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCropType() { return cropType; }
    public void setCropType(String cropType) { this.cropType = cropType; }
    public double getArea() { return area; }
    public void setArea(double area) { this.area = area; }
}