package com.boo4er.currencyexchange.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {

    private static final String DB_PATH = "data/currency.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private static final String INIT_SCRIPT_PATH = "db/init.sql";
    private static boolean isInitialized = false;
    private static final Logger log = LoggerFactory.getLogger(DatabaseUtil.class);

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Не удалось загрузить драйвер SQLite", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (!isInitialized) {
            initializeDatabase();
        }
        return DriverManager.getConnection(DB_URL);
    }

    private static void initializeDatabase() {
        File dbFile = new File(DB_PATH);
        File parentDir = dbFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        if (!dbFile.exists()) {
            executeInitScript();
        }
        isInitialized = true;
    }

    private static void executeInitScript() {
        InputStream inputStream = DatabaseUtil.class.getClassLoader().getResourceAsStream(INIT_SCRIPT_PATH);

        if (inputStream == null) {
            log.error("Файл init.sql не найден в ресурсах");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            StringBuilder sql = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sql.append(line).append("\n");
            }

            String[] queries = sql.toString().split(";");

            try (Connection connection = DriverManager.getConnection(DB_URL);
                 Statement statement = connection.createStatement()) {
                for (String query : queries) {
                    String trimmed = query.trim();
                    if (!trimmed.isEmpty()) {
                        statement.execute(trimmed);
                    }
                }
                log.info("База данных успешно инициализирована");
            } catch (SQLException e) {
                log.error("Ошибка при инициализации базы данных", e);
            }
        } catch (Exception e) {
            log.error("Ошибка при чтении файла init.sql", e);
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("Ошибка при закрытии соединения", e);
            }
        }
    }
}
