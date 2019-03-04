package com.tf.autoconfig;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.StringUtils;
import com.tf.constant.DataSourceConstant;
import com.tf.prop.HsDataSourceProperties;
import com.tf.util.BeanDefinitionRegistryUtil;
import com.tf.util.HsDatasourcePropertiesValidator;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.bind.PropertySourceUtils;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多数据源自动配置
 *
 * @author guoqw
 * @since 2017-04-13 13:56
 */
@Configuration
@EnableConfigurationProperties(value = HsDataSourceProperties.class)
public class MultipleDataSourceAutoConfig implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    /**
     * 是否进行多数据源及mybatis配置
     */
    private Boolean enable;

    /**
     * 所有数据源配置信息
     */
    private Map<String, HsDataSourceProperties.DruidDatasourceProperties> allDatasources = new HashMap<>();

    /**
     * 是否需要添加druid logFilter
     */
    private boolean needAddDruidLogFilter = false;


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // do nothing
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (!enable) {
            return;
        }
        // 注册数据源
        allDatasources.keySet().forEach(beanName -> registerDatasourceBean(registry, beanName));
    }

    @Override
    public void setEnvironment(Environment environment) {
        ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
        Map<String, Object> map = PropertySourceUtils
                .getSubProperties(env.getPropertySources(), DataSourceConstant.DATASOURCE_PREFIX);
        HsDataSourceProperties hsDataSourceProperties = new HsDataSourceProperties();
        RelaxedDataBinder relaxedDataBinder = new RelaxedDataBinder(hsDataSourceProperties,
                null);
        relaxedDataBinder.setConversionService(new DefaultConversionService());
        // 添加自定义校验类
        relaxedDataBinder.addValidators(new HsDatasourcePropertiesValidator());
        // 设置数据源属性
        relaxedDataBinder.bind(new MutablePropertyValues(map));
        // 校验
        relaxedDataBinder.validate();
        // 获取校验结果
        BindingResult bindingResult = relaxedDataBinder.getBindingResult();
        if (bindingResult.hasErrors()) {
            ObjectError objectError = bindingResult.getAllErrors().get(0);
            throw new IllegalArgumentException("其他数据源=>" + objectError.getDefaultMessage());
        }
        // 加入主数据源配置信息
        allDatasources.put(DataSourceConstant.DEFAULT_MAIN_DATASOURCE_NAME,
                hsDataSourceProperties.getMainDatasource());
        // 加入其它数据源配置信息
        Map<String, HsDataSourceProperties.DruidDatasourceProperties> otherDataSources =
                hsDataSourceProperties.getOtherDataSources();
        if (!CollectionUtils.isEmpty(otherDataSources)) {
            allDatasources.putAll(otherDataSources);
        }

        // 判断是否需要添加druid logFilter
        needAddDruidLogFilter = needAddDruidLogFilter(env);

        // 判断是否要进行自动配置
        enable = hsDataSourceProperties.getEnable();
    }

    /**
     * 注册数据源bean
     */
    private void registerDatasourceBean(BeanDefinitionRegistry registry, String beanName) {
        AnnotatedGenericBeanDefinition abd = BeanDefinitionRegistryUtil.decorateAbd(DruidDataSource.class);
        abd.setDestroyMethodName("close");
        HsDataSourceProperties.DruidDatasourceProperties druidDatasourceProperties = allDatasources.get(beanName);
        // 主数据源设置为默认注入的数据源
        boolean isMainDatasource = StringUtils.equals(beanName, DataSourceConstant.DEFAULT_MAIN_DATASOURCE_NAME);
        if (isMainDatasource) {
            abd.setPrimary(true);
        }
        // 设置属性
        Map<String, Object> druidDataSourceProperties = objectToMap(
                druidDatasourceProperties.addLogFilter(needAddDruidLogFilter));
        // 移除mybatis相关的配置，只保留druid相关的配置，以免注入失败
        druidDataSourceProperties.remove("mybatis");
        abd.setPropertyValues(new MutablePropertyValues(
                druidDataSourceProperties));
        // 注册datasource bean
        beanName = BeanDefinitionRegistryUtil.doRegistBean(abd, beanName, registry);

    }

    /**
     * 对象转map
     */
    private static Map<String, Object> objectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        //获取关联的所有类，本类以及所有父类
        Class oo = obj.getClass();
        List<Class> clazzs = new ArrayList<>();
        while (oo != null && oo != Object.class) {
            clazzs.add(oo);
            oo = oo.getSuperclass();
        }
        Map<String, Object> map = new HashMap<>(clazzs.size() * 8);
        try {
            for (Class clazz : clazzs) {
                Field[] declaredFields = clazz.getDeclaredFields();
                for (Field field : declaredFields) {
                    int mod = field.getModifiers();
                    //过滤 static 和 final 类型
                    if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                        continue;
                    }
                    field.setAccessible(true);
                    map.put(field.getName(), field.get(obj));
                }
            }
        } catch (IllegalAccessException e) {
            // ignore
        }
        return map;
    }

    /**
     * 是否需要添加druid的logFilter
     */
    private boolean needAddDruidLogFilter(ConfigurableEnvironment env) {
        // 获取是否需要设置druid logFilter
        Map<String, Object> loggingMap = PropertySourceUtils.getSubProperties(env.getPropertySources(),
                "logging.level.");
        return loggingMap.entrySet()
                .stream()
                .anyMatch(entry -> "druid.sql".equals(entry.getKey())
                        && StringUtils.equalsIgnoreCase(entry.getValue().toString(), "debug"));
    }
}
