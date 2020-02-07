package cz.cvut.fel.tk21.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "database")
public class DatabaseProperties {

    private String url;

    private String driverClassName;

    private String ddlgeneration;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getDdlgeneration() {
        return ddlgeneration;
    }

    public void setDdlgeneration(String ddlgeneration) {
        this.ddlgeneration = ddlgeneration;
    }
}
