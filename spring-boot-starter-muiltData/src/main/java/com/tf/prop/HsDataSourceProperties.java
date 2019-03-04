package com.tf.prop;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * xkeshi数据源属性配置
 *
 * @author guoqw
 * @since 2017-04-14 16:13
 */
@ConfigurationProperties(prefix = "xkeshi.datasource")
@Validated
public class HsDataSourceProperties {

    /**
     * 是否开启多数据及mybatis配置
     */
    private Boolean enable = Boolean.TRUE;

    /**
     * 主数据源
     */
//    @NotNull(message = "主数据源不能为空")
    @Valid
    private DruidDatasourceProperties mainDatasource;

    /**
     * key:beanName,value:druid datasource properties
     */
/*    @NotBlankKey(message = "其他数据源的beanName不能为空")
    @Valid
    这里的注解对Collection不生效。。。改用XkeshiDatasourcePropertiesValidator校验
    */
    private Map<String, DruidDatasourceProperties> otherDataSources;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public DruidDatasourceProperties getMainDatasource() {
        return mainDatasource;
    }

    public void setMainDatasource(DruidDatasourceProperties mainDatasource) {
        this.mainDatasource = mainDatasource;
    }

    public Map<String, DruidDatasourceProperties> getOtherDataSources() {
        return otherDataSources;
    }

    public void setOtherDataSources(Map<String, DruidDatasourceProperties> otherDataSources) {
        this.otherDataSources = otherDataSources;
    }

    public static class DruidDatasourceProperties {
        private String driverClassName = "com.mysql.jdbc.Driver";
        @NotBlank(message = "数据库连接url不能为空")
        private String url;
        @NotBlank(message = "数据库连接用户名不能为空")
        private String username;
        @JSONField(serialize = false)
        private String password;
        private int maxActive = 15;
        private int initialSize = 2;
        private long maxWait = 60000;
        private int minIdle = 1;
        private long timeBetweenEvictionRunsMillis = 3000;
        private long minEvictableIdleTimeMillis = 300000;
        private String validationQuery = "SELECT 'x'";
        private boolean testWhileIdle = true;
        private boolean testOnBorrow = false;
        private boolean testOnReturn = false;
        private int maxPoolPreparedStatementPerConnectionSize = 20;
        private long timeBetweenLogStatsMillis = 300000;
        private boolean removeAbandoned = true;
        private int removeAbandonedTimeout = 180;
        private boolean logAbandoned = true;
        private boolean keepAlive = true;
        private String filters = "wall,stat";
        private List<Filter> proxyFilters;

        public DruidDatasourceProperties addLogFilter(boolean add) {
            if (add) {
                if (proxyFilters == null) {
                    proxyFilters = new ArrayList<>();
                }
                // slf4j
                Slf4jLogFilter slf4jLogFilter = new Slf4jLogFilter();
                slf4jLogFilter.setStatementLogEnabled(false);
                slf4jLogFilter.setConnectionLogEnabled(false);
                slf4jLogFilter.setResultSetLogEnabled(false);
                slf4jLogFilter.setStatementExecutableSqlLogEnable(true);
                slf4jLogFilter.setResultSetOpenAfterLogEnabled(false);
                slf4jLogFilter.setResultSetCloseAfterLogEnabled(false);
                proxyFilters.add(slf4jLogFilter);
            } else {
                if (StringUtils.isBlank(filters)) {
                    filters = "wall,stat,slf4j";
                } else {
                    filters = filters + ",slf4j";
                }
            }
            return this;
        }

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getMaxActive() {
            return maxActive;
        }

        public void setMaxActive(int maxActive) {
            this.maxActive = maxActive;
        }

        public int getInitialSize() {
            return initialSize;
        }

        public void setInitialSize(int initialSize) {
            this.initialSize = initialSize;
        }

        public long getMaxWait() {
            return maxWait;
        }

        public void setMaxWait(long maxWait) {
            this.maxWait = maxWait;
        }

        public int getMinIdle() {
            return minIdle;
        }

        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public long getTimeBetweenEvictionRunsMillis() {
            return timeBetweenEvictionRunsMillis;
        }

        public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
            this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        }

        public long getMinEvictableIdleTimeMillis() {
            return minEvictableIdleTimeMillis;
        }

        public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
            this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        }

        public String getValidationQuery() {
            return validationQuery;
        }

        public void setValidationQuery(String validationQuery) {
            this.validationQuery = validationQuery;
        }

        public boolean isTestWhileIdle() {
            return testWhileIdle;
        }

        public void setTestWhileIdle(boolean testWhileIdle) {
            this.testWhileIdle = testWhileIdle;
        }

        public boolean isTestOnBorrow() {
            return testOnBorrow;
        }

        public void setTestOnBorrow(boolean testOnBorrow) {
            this.testOnBorrow = testOnBorrow;
        }

        public boolean isTestOnReturn() {
            return testOnReturn;
        }

        public void setTestOnReturn(boolean testOnReturn) {
            this.testOnReturn = testOnReturn;
        }

        public int getMaxPoolPreparedStatementPerConnectionSize() {
            return maxPoolPreparedStatementPerConnectionSize;
        }

        public void setMaxPoolPreparedStatementPerConnectionSize(int maxPoolPreparedStatementPerConnectionSize) {
            this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
        }

        public long getTimeBetweenLogStatsMillis() {
            return timeBetweenLogStatsMillis;
        }

        public void setTimeBetweenLogStatsMillis(long timeBetweenLogStatsMillis) {
            this.timeBetweenLogStatsMillis = timeBetweenLogStatsMillis;
        }

        public boolean isRemoveAbandoned() {
            return removeAbandoned;
        }

        public void setRemoveAbandoned(boolean removeAbandoned) {
            this.removeAbandoned = removeAbandoned;
        }

        public int getRemoveAbandonedTimeout() {
            return removeAbandonedTimeout;
        }

        public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
            this.removeAbandonedTimeout = removeAbandonedTimeout;
        }

        public boolean isLogAbandoned() {
            return logAbandoned;
        }

        public void setLogAbandoned(boolean logAbandoned) {
            this.logAbandoned = logAbandoned;
        }

        public boolean isKeepAlive() {
            return keepAlive;
        }

        public void setKeepAlive(boolean keepAlive) {
            this.keepAlive = keepAlive;
        }

        public String getFilters() {
            return filters;
        }

        public void setFilters(String filters) {
            this.filters = filters;
        }

        public List<Filter> getProxyFilters() {
            return proxyFilters;
        }

        public void setProxyFilters(List<Filter> proxyFilters) {
            this.proxyFilters = proxyFilters;
        }

        @Override
        public String toString() {
            return "DruidDatasourceProperties{" +
                    "driverClassName='" + driverClassName + '\'' +
                    ", url='" + url + '\'' +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    ", maxActive=" + maxActive +
                    ", initialSize=" + initialSize +
                    ", maxWait=" + maxWait +
                    ", minIdle=" + minIdle +
                    ", timeBetweenEvictionRunsMillis=" + timeBetweenEvictionRunsMillis +
                    ", minEvictableIdleTimeMillis=" + minEvictableIdleTimeMillis +
                    ", validationQuery='" + validationQuery + '\'' +
                    ", testWhileIdle=" + testWhileIdle +
                    ", testOnBorrow=" + testOnBorrow +
                    ", testOnReturn=" + testOnReturn +
                    ", maxPoolPreparedStatementPerConnectionSize=" + maxPoolPreparedStatementPerConnectionSize +
                    ", timeBetweenLogStatsMillis=" + timeBetweenLogStatsMillis +
                    ", removeAbandoned=" + removeAbandoned +
                    ", removeAbandonedTimeout=" + removeAbandonedTimeout +
                    ", logAbandoned=" + logAbandoned +
                    ", keepAlive=" + keepAlive +
                    ", filters='" + filters + '\'' +
                    ", proxyFilters=" + proxyFilters +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "XkeshiDataSourceProperties{" +
                "enable=" + enable +
                ", mainDatasource=" + mainDatasource +
                ", otherDataSources=" + otherDataSources +
                '}';
    }
}
