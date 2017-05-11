package com.nokia.configuration;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.nokia.dao")
@PropertySource("classpath:/application.properties")
public class DataConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(){

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        factory.setDataSource(dataSource());
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan(environment.getProperty("database_manager.entity.package"));
        factory.setJpaProperties(getHibernateProperties());

        return factory;
    }

    @Bean
    public DataSource dataSource(){

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(environment.getProperty("database_manager.db.driver"));
        basicDataSource.setUrl(environment.getProperty("database_manager.db.url_live"));
        basicDataSource.setUsername(environment.getProperty("database_manager.db.username_live"));
        basicDataSource.setPassword(environment.getProperty("database_manager.db.password_live"));

        return basicDataSource;
    }

    private Properties getHibernateProperties(){
        Properties properties = new Properties();
        properties.put("hibernate.dialect", environment.getProperty("hibernate.dialect"));
        properties.put("hibernate.implicit_naming_strategy", environment.getProperty("hibernate.implicit_naming_strategy"));
        properties.put("hibernate.format_sql", environment.getProperty("hibernate.format_sql"));
        properties.put("hibernate.show_sql", environment.getProperty("hibernate.show_sql"));
        properties.put("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto"));

        return properties;
    }
}
