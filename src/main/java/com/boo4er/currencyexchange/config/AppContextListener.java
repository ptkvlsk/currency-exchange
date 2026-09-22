package com.boo4er.currencyexchange.config;

import com.boo4er.currencyexchange.util.DatabaseUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            log.info("База данных инициализирована при старте приложения");
        } catch (SQLException e) {
            log.error("Не удалось инициализировать БД", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
