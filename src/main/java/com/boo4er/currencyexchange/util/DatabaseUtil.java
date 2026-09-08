package com.boo4er.currencyexchange.util;

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

    private static final String DB_PATH = "src/main/resources/currency.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private static final String INIT_SCRIPT_PATH = "db/init.sql";
    private static boolean isInitialized = false;

    public static Connection getConnection() throws SQLException {
        if (!isInitialized) {
            initializeDatabase();
        }
        return DriverManager.getConnection(DB_URL);
    }

    private static void initializeDatabase() {
        File dbFile = new File(DB_PATH);
        if (!dbFile.exists()) {
            executeInitScript();
        }
        isInitialized = true;
    }

    private static void executeInitScript() {
        InputStream is = DatabaseUtil.class.getClassLoader().getResourceAsStream(INIT_SCRIPT_PATH);

        if (is == null) {
            System.err.println("Файл init.sql не найден в ресурсах");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

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
                System.out.println("База данных успешно инициализирована");
            } catch (SQLException e) {
                System.err.println("Ошибка при инициализации базы жданных");
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("Ошибка при чтении файла init.sql");
            e.printStackTrace();
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
