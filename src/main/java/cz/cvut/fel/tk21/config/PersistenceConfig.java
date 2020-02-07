package cz.cvut.fel.tk21.config;

import com.jolbox.bonecp.BoneCPDataSource;
import cz.cvut.fel.tk21.config.properties.DatabaseProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties(DatabaseProperties.class)
@ComponentScan("cz.cvut.fel.tk21.dao")
public class PersistenceConfig {

    private DatabaseProperties databaseProperties;

    @Autowired
    public PersistenceConfig(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    @Bean(name = "myDataSource")
    public DataSource dataSource() {
        final BoneCPDataSource ds = new BoneCPDataSource();
        ds.setJdbcUrl(this.databaseProperties.getUrl());
        ds.setDriverClass(this.databaseProperties.getDriverClassName());
        return ds;
    }
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("myDataSource") DataSource ds) {
        final LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(ds);
        emf.setJpaVendorAdapter(new EclipseLinkJpaVendorAdapter());
        emf.setPackagesToScan("cz.cvut.fel.tk21.model");

        final Properties props = new Properties();
        props.setProperty("generateDdl", "true");
        props.setProperty("showSql", "true");
        props.setProperty("eclipselink.weaving", "static");
        props.setProperty("eclipselink.ddl-generation", this.databaseProperties.getDdlgeneration());
        emf.setJpaProperties(props);
        return emf;
    }

    @Bean(name = "txManager")
    JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

}
