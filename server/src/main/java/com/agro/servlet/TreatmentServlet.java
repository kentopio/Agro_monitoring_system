package com.agro.servlet;

import com.agro.models.Treatment;
import com.agro.service.TreatmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public class TreatmentServlet extends HttpServlet {
    private final TreatmentService service = new TreatmentService();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            writeJson(resp, service.getAllTreatments());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String action = req.getParameter("action");
            if ("delete".equals(action)) {
                service.deleteTreatment(id(req));
            } else if ("add".equals(action) || "update".equals(action)) {
                Treatment value = new Treatment(
                        "update".equals(action) ? id(req) : 0,
                        Integer.parseInt(req.getParameter("problemId")),
                        req.getParameter("treatmentName"),
                        LocalDate.parse(req.getParameter("plannedDate")),
                        Boolean.parseBoolean(req.getParameter("completed"))
                );
                if ("add".equals(action)) {
                    service.createTreatment(value);
                } else {
                    service.updateTreatment(value);
                }
            } else {
                throw new IllegalArgumentException("Невідома операція");
            }
            writeJson(resp, service.getAllTreatments());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }
    private void writeError(HttpServletResponse resp, Exception e) throws IOException {
        boolean clientError = e instanceof IllegalArgumentException || e instanceof IllegalStateException;
        int status = clientError ? HttpServletResponse.SC_BAD_REQUEST : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String message = clientError
                ? (e.getMessage() == null ? "Некоректний запит" : e.getMessage())
                : "Внутрішня помилка сервера";
        resp.setStatus(status);
        writeJson(resp, Map.of("error", message));
    }
    private int id(HttpServletRequest req) {
        return Integer.parseInt(req.getParameter("id"));
    }
}