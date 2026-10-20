package com.boo4er.currencyexchange.util;

import com.boo4er.currencyexchange.exception.DatabaseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseUtil {

    private static volatile HikariDataSource dataSource;
    private static final String INIT_SCRIPT_PATH = "db/init.sql";
    private static final Logger log = LoggerFactory.getLogger(DatabaseUtil.class);

    public static Connection getConnection() throws SQLException {
        HikariDataSource ds = dataSource;
        if (ds == null) {
            throw new IllegalStateException("База дынных не инициализирована: сначала нужно вызвать DatabaseUtil.init()");
        }
        return ds.getConnection();
    }

    public static synchronized void init() {
        if (dataSource != null) {
            return;
        }

        String dbPath = resolveDbPath();
        File parentDir = new File(dbPath).getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + dbPath);
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(5);

        try {
            dataSource = new HikariDataSource(config);
            executeInitScript();
        } catch (RuntimeException e) {
            if (dataSource != null) {
                dataSource.close();
                dataSource = null;
            }
            throw e;
        }
    }

    public static synchronized void close() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
            log.info("Пул соединений закрыт");
        }
    }

    private static void executeInitScript() {
        InputStream inputStream = DatabaseUtil.class.getClassLoader().getResourceAsStream(INIT_SCRIPT_PATH);

        if (inputStream == null) {
            throw new DatabaseException("Файл " + INIT_SCRIPT_PATH + " не найден в ресурсах");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            StringBuilder sql = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sql.append(line).append("\n");
            }

            String[] queries = sql.toString().split(";");

            try (Connection connection = dataSource.getConnection();
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
                throw new DatabaseException("Не удалось инициализировать базу данных", e);
            }
        } catch (IOException e) {
            log.error("Ошибка при чтении файла init.sql", e);
            throw new DatabaseException("Не удалось прочитать файл init.sql", e);
        }
    }

    private static Properties loadProperties() {
        try (InputStream in = DatabaseUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new DatabaseException("Файл db.properties не найден в ресурсах");
            }
            Properties properties = new Properties();
            properties.load(in);
            return properties;
        } catch (IOException e) {
            throw new DatabaseException("Не удалось прочитать файл db.properties", e);
        }
    }

    private static String resolveDbPath() {
        String fromEnv = System.getenv("DB_PATH");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.trim();
        }
        String fromFile = loadProperties().getProperty("db.path");
        if (fromFile == null || fromFile.isBlank()) {
            throw new DatabaseException("В db.properties не задан параметр db.path");
        }
        return fromFile.trim();
    }
}
