package org.example.pswrd_manager;

import org.example.pswrd_manager.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

@SpringBootApplication
public class PswrdManagerApplication {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(PswrdManagerApplication.class, args);
    }

    @Bean
    public CommandLineRunner testConnection() {
        return args -> {
            System.out.println("🔍 Проверка подключения к БД...");

            try (Connection conn = dataSource.getConnection()) {
                DatabaseMetaData metaData = conn.getMetaData();

                System.out.println("   Подключение успешно!");
                System.out.println("   База данных: " + metaData.getDatabaseProductName());
                System.out.println("   Версия: " + metaData.getDatabaseProductVersion());
                System.out.println("   URL: " + metaData.getURL());
                System.out.println("   Пользователь: " + metaData.getUserName());

                // Проверим существующие таблицы
                String tablesQuery = """
                    SELECT table_name 
                    FROM information_schema.tables 
                    WHERE table_schema = 'public'
                    ORDER BY table_name
                """;

                var tables = jdbcTemplate.queryForList(tablesQuery);
                System.out.println("Таблицы в public схеме: " + tables.size());
                tables.forEach(table ->
                        System.out.println("   - " + table.get("table_name"))
                );

            } catch (Exception e) {
                System.err.println("Ошибка подключения: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    @Bean
    CommandLineRunner generateAdminHash(CryptoService cryptoService) {
        return args -> {
            byte[] salt = "adminsalt".getBytes(); // Та соль, что в SQL
            String hash = cryptoService.hashPassword("admin", salt);
            System.out.println("НОВЫЙ ХЕШ ДЛЯ SQL: " + hash);
        };
    }
}
