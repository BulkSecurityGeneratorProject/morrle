package com.morrle.config;

import com.codahale.metrics.MetricRegistry;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableJpaRepositories("com.morrle.repository")
//@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class DatabaseConfiguration implements EnvironmentAware {
    private final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);

    private RelaxedPropertyResolver dataSourcePropertyResolver;


    private Environment env;

    @Autowired(required = false)
    private MetricRegistry metricRegistry;


    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
        this.dataSourcePropertyResolver = new RelaxedPropertyResolver(env, "spring.datasource.");
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnExpression("#{!environment.acceptsProfiles('cloud') && !environment.acceptsProfiles('heroku')}")
    public DataSource dataSource() {
        log.debug("Configuring Datasource");
        if (dataSourcePropertyResolver.getProperty("url") == null) {
            log.error("Your database connection pool configuration is incorrect! The application" +
                            " cannot start. Please check your Spring profile, current profiles are: {}",
                    Arrays.toString(env.getActiveProfiles()));

            throw new ApplicationContextException("Database connection pool is not configured correctly");
        }
        //hikari 高性能的数据库连接池

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(dataSourcePropertyResolver.getProperty("url"));
        config.setUsername(dataSourcePropertyResolver.getProperty("username"));
        config.setPassword(dataSourcePropertyResolver.getProperty("password"));


        if (metricRegistry != null) {
            config.setMetricRegistry(metricRegistry);
        }
        return new HikariDataSource(config);
    }


}
