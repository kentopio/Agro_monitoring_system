package com.agro;

import com.agro.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class HttpAgroClient {
    private static final String BASE = "http://localhost:8080/agro-server";
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    public List<Field> getFields() {
        return Arrays.asList(read(get("/fields"), Field[].class));
    }
    public void addField(Field f) {
        post("/fields", "action=add&name=" + enc(f.getName())
                + "&cropType=" + enc(f.getCropType())
                + "&area=" + f.getArea());
    }
    public void updateField(Field f) {
        post("/fields", "action=update&id=" + f.getId()
                + "&name=" + enc(f.getName())
                + "&cropType=" + enc(f.getCropType())
                + "&area=" + f.getArea());
    }
    public void deleteField(int id) {
        post("/fields", "action=delete&id=" + id);
    }
    public List<Observation> getObservations() {
        return Arrays.asList(read(get("/observations"), Observation[].class));
    }
    public void addObservation(Observation o) {
        post("/observations", "action=add&fieldId=" + o.getFieldId()
                + "&observationDate=" + enc(o.getObservationDate().toString())
                + "&inspectorName=" + enc(o.getInspectorName()));
    }
    public void updateObservation(Observation o) {
        post("/observations", "action=update&id=" + o.getId()
                + "&fieldId=" + o.getFieldId()
                + "&observationDate=" + enc(o.getObservationDate().toString())
                + "&inspectorName=" + enc(o.getInspectorName()));
    }
    public void deleteObservation(int id) {
        post("/observations", "action=delete&id=" + id);
    }
    public List<Problem> getProblems() {
        return Arrays.asList(read(get("/problems"), Problem[].class));
    }
    public void addProblem(Problem p) {
        post("/problems", problemForm("add", p));
    }
    public void updateProblem(Problem p) {
        post("/problems", problemForm("update", p) + "&id=" + p.getId());
    }
    public void changeProblemStatus(int id, ProblemStatus status) {
        post("/problems", "action=transition&id=" + id + "&status=" + enc(status.name()));
    }
    public void deleteProblem(int id) {
        post("/problems", "action=delete&id=" + id);
    }
    public List<Treatment> getTreatments() {
        return Arrays.asList(read(get("/treatments"), Treatment[].class));
    }
    public void addTreatment(Treatment t) {
        post("/treatments", treatmentForm("add", t));
    }
    public void updateTreatment(Treatment t) {
        post("/treatments", treatmentForm("update", t) + "&id=" + t.getId());
    }
    public void deleteTreatment(int id) {
        post("/treatments", "action=delete&id=" + id);
    }

    private String problemForm(String action, Problem p) {
        return "action=" + action
                + "&observationId=" + p.getObservationId()
                + "&description=" + enc(p.getDescription())
                + "&f1=" + p.getF1InfectedArea()
                + "&f2=" + p.getF2Severity()
                + "&f3=" + p.getF3Sensitivity();
    }
    private String treatmentForm(String action, Treatment t) {
        return "action=" + action
                + "&problemId=" + t.getProblemId()
                + "&treatmentName=" + enc(t.getTreatmentName())
                + "&plannedDate=" + enc(t.getPlannedDate().toString())
                + "&completed=" + t.isCompleted();
    }
    private String get(String path) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path)).GET().build();
        return send(req);
    }
    private String post(String path, String form) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
        return send(req);
    }
    private String send(HttpRequest req) {
        try {
            HttpResponse<String> r = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (r.statusCode() >= 400) {
                throw new RuntimeException(r.body());
            }
            return r.body();
        } catch (Exception e) {
            throw new RuntimeException("Помилка HTTP: " + e.getMessage(), e);
        }
    }
    private <T> T read(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Помилка JSON: " + e.getMessage(), e);
        }
    }
    private String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}