package org.example.pswrd_manager.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {
    // Настройка для Spring 7 конфигурации через бин поверх application.yml

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:db/changelog/changelog-master.yaml");
        liquibase.setDataSource(dataSource);
        return liquibase;
    }


}
