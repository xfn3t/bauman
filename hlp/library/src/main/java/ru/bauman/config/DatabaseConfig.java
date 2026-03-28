package ru.bauman.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class DatabaseConfig {

    private static final String PROPERTY_FILE = "application.yml";

    private static final String url;
    private static final String user;
    private static final String password;

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream(PROPERTY_FILE)) {
            if (input == null) {
                throw new RuntimeException(PROPERTY_FILE + " not found");
            }
            Yaml yaml = new Yaml();
            Map<String, Object> props = yaml.load(input);

            Map<String, Object> db = (Map<String, Object>) props.get("database");

            if (db == null) {
                throw new RuntimeException("database section not found in application.yml");
            }

            url = (String) db.get("url");
            user = (String) db.get("user");
            password = (String) db.get("password");
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}