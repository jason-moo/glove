package com.tf.propertity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.Map;

/**
 * Created by jason_moo on 2018/11/27.
 */
@ConfigurationProperties(prefix = "mulit.dataSource")
public class DataSourceProperties {

    private String driverClass;

    private Map<String,DataSource> dataSourceMap;

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public Map<String, DataSource> getDataSourceMap() {
        return dataSourceMap;
    }

    public void setDataSourceMap(Map<String, DataSource> dataSourceMap) {
        this.dataSourceMap = dataSourceMap;
    }
}
