package com.aerocar.web;

import com.aerocar.model.Models.*;
import com.aerocar.service.*;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** GET /api/health | /api/tesla | /api/market ; POST /api/thruster | /api/hover | /api/rocket */
@WebServlet(name = "api", urlPatterns = "/api/*")
public class ApiServlet extends HttpServlet {
    private static final Gson GSON = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();
        switch (path) {
            case "/health" -> send(resp, 200, Map.of("status", "UP", "java", System.getProperty("java.version")));
            case "/tesla" -> send(resp, 200, ContentService.tesla());
            case "/market" -> send(resp, 200, ContentService.market());
            default -> send(resp, 404, Map.of("error", "Not found"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo() == null ? "" : req.getPathInfo();
        try {
            Object out = switch (path) {
                case "/thruster" -> ThrusterService.simulate(read(req, ThrusterReq.class));
                case "/hover" -> HoverService.compare(read(req, HoverReq.class));
                case "/rocket" -> RocketService.compare(read(req, RocketReq.class));
                default -> null;
            };
            if (out == null) send(resp, 404, Map.of("error", "Unknown module: " + path));
            else send(resp, 200, out);
        } catch (IllegalArgumentException | JsonParseException e) {
            send(resp, 400, Map.of("error", e.getMessage() == null ? "Invalid request" : e.getMessage()));
        } catch (Exception e) {
            getServletContext().log("API failure on " + path, e);
            send(resp, 500, Map.of("error", "Internal error"));
        }
    }

    private static <T> T read(HttpServletRequest req, Class<T> type) throws IOException {
        req.setCharacterEncoding("UTF-8");
        T body = GSON.fromJson(req.getReader(), type);
        if (body == null) throw new IllegalArgumentException("Empty request body");
        return body;
    }

    private static void send(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(GSON.toJson(body));
    }
}
