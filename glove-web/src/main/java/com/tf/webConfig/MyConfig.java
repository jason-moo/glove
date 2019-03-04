package com.tf.webConfig;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Properties;

/**
 * Created by jason_moo on 2018/11/26.
 */
@Configuration
public class MyConfig extends WebMvcConfigurerAdapter{

//    @Override
//    public void configurePathMatch(PathMatchConfigurer configurer) {
//        configurer.setUseRegisteredSuffixPatternMatch(true);
//    }

    /**
     * 设置匹配*.action后缀请求
     * @param dispatcherServlet
     * @return
     */
    @Bean
    public ServletRegistrationBean servletRegistrationBean(DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean servletServletRegistrationBean = new ServletRegistrationBean(dispatcherServlet);
        servletServletRegistrationBean.addUrlMappings("*.do");
        return servletServletRegistrationBean;
    }


    @Bean
    public Interceptor interceptor(){

        return new Interceptor() {
            @Override
            public Object intercept(Invocation invocation) throws Throwable {
                return null;
            }

            @Override
            public Object plugin(Object o) {
                return null;
            }

            @Override
            public void setProperties(Properties properties) {

            }
        };
    }

}
