package cz.cvut.fel.tk21.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configurable
@EnableTransactionManagement
@ComponentScan({"cz.cvut.fel.tk21.service", "cz.cvut.fel.tk21.scraping"})
public class ServiceConfig {
}
