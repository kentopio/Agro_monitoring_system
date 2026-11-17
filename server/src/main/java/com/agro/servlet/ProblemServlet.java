package com.agro.servlet;

import com.agro.models.Problem;
import com.agro.models.ProblemStatus;
import com.agro.service.ProblemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class ProblemServlet extends HttpServlet {
    private final ProblemService service = new ProblemService();
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            writeJson(resp, service.getAllProblems());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String action = req.getParameter("action");
            if ("add".equals(action) || "update".equals(action)) {
                Problem existing = "update".equals(action) ? service.getProblemById(id(req)) : null;
                ProblemStatus status = existing != null ? existing.getStatus() : ProblemStatus.ВИЯВЛЕНО;
                Problem value = new Problem(
                        "update".equals(action) ? id(req) : 0,
                        Integer.parseInt(req.getParameter("observationId")),
                        req.getParameter("description"),
                        status,
                        Integer.parseInt(req.getParameter("f1")),
                        Integer.parseInt(req.getParameter("f2")),
                        Integer.parseInt(req.getParameter("f3"))
                );
                if ("add".equals(action)) {
                    service.createProblem(value);
                } else {
                    service.updateProblem(value);
                }
            } else if ("transition".equals(action)) {
                String stateValue = req.getParameter("status");
                if (stateValue == null || stateValue.isBlank()) {
                    throw new IllegalArgumentException("Не задано новий стан проблеми");
                }
                service.changeStatus(id(req), ProblemStatus.valueOf(stateValue));
            } else if ("delete".equals(action)) {
                service.deleteProblem(id(req));
            } else {
                throw new IllegalArgumentException("Невідома операція");
            }
            writeJson(resp, service.getAllProblems());
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