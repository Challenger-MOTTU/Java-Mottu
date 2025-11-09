package com.motogrid.api.config;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DbConfig {

    // ---- H2 (PRIMÁRIO) ----
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties h2Props() {
        return new DataSourceProperties();
    }

    @Bean(name = "dataSource")
    @Primary
    public DataSource h2DataSource(@Qualifier("h2Props") DataSourceProperties props) {
        return props.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    // ---- ORACLE (opcional; só cria se existir oracle.datasource.url) ----
    @Bean
    @ConfigurationProperties("oracle.datasource")
    @ConditionalOnProperty(prefix = "oracle.datasource", name = "url")
    public DataSourceProperties oracleProps() {
        return new DataSourceProperties();
    }

    @Bean(name = "oracleDataSource")
    @ConditionalOnProperty(prefix = "oracle.datasource", name = "url")
    public DataSource oracleDataSource(@Qualifier("oracleProps") DataSourceProperties props) {
        return props.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "oracleJdbcTemplate")
    @ConditionalOnBean(name = "oracleDataSource")
    public JdbcTemplate oracleJdbcTemplate(@Qualifier("oracleDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}
