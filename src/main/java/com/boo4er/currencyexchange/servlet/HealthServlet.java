package com.boo4er.currencyexchange.servlet;

import com.boo4er.currencyexchange.util.DatabaseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;

@WebServlet("/health")
public class HealthServlet extends BaseServlet {

    private static final Logger log = LoggerFactory.getLogger(HealthServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connection = DatabaseUtil.getConnection()) {
            sendJson(resp, SC_OK, Map.of("status", "UP"));
        } catch (SQLException e) {
            log.error("Health check: БД недоступна", e);
            sendJson(resp, SC_SERVICE_UNAVAILABLE, Map.of("status", "DOWN"));
        }
    }
}
