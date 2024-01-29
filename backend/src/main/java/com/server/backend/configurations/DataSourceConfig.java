package com.server.backend.configurations;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

  @Bean
  public DataSource dataSource() {
    // You can set default values or load them from configuration
    // make the url a jdb url
    String url = "jdbc:mysql://localhost:9999";
    String username = "root";
    String password = "mysql";

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(url);
    config.setUsername(username);
    config.setPassword(password);
    System.out.println("Error in DataSourceConfig.java");

    return new HikariDataSource(config);
  }
}

// import org.springframework.beans.factory.annotation.Value;
// import
// org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// // import org.springframework.context.annotation.Bean;
// // import org.springframework.context.annotation.Configuration;
// import org.springframework.jdbc.core.JdbcTemplate;

// import javax.sql.DataSource;

// @Configuration
// public class DataSourceConfig {

// @Value("${custom.datasource.enabled:false}")
// private boolean dataSourceEnabled;

// @Bean
// @ConditionalOnProperty(name = "custom.datasource.enabled", havingValue =
// "true")
// public DataSource dataSource() {
// // Create and return your DataSource configuration here
// // ...

// return yourDataSource;
// }

// @Bean
// public JdbcTemplate jdbcTemplate(DataSource dataSource) {
// return new JdbcTemplate(dataSource);
// }
// }