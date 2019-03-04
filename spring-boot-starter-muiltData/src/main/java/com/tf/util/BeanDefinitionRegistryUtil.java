package com.tf.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.*;

/**
 * Created by ylc on 2017/11/3
 **/
public class BeanDefinitionRegistryUtil {

    // 装饰获取abd
    public static AnnotatedGenericBeanDefinition decorateAbd(Class clazz) {
        ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();
        AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(clazz);
        ScopeMetadata metadata = scopeMetadataResolver.resolveScopeMetadata(abd);
        abd.setScope(metadata.getScopeName());
        AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
        return abd;
    }

    // 注册bean
    public static String doRegistBean(AnnotatedGenericBeanDefinition abd, String beanName, BeanDefinitionRegistry registry) {
        return doRegistBean(abd, beanName, registry, null);
    }

    // 注册bean
    public static String doRegistBean(AnnotatedGenericBeanDefinition abd,
                                      String beanName,
                                      BeanDefinitionRegistry registry, String[] aliases) {
        if (StringUtils.isBlank(beanName)) {
            // 根据class名称生成bean name
            BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
            beanName = beanNameGenerator.generateBeanName(abd, registry);
        }
        BeanDefinitionHolder definitionHolder;
        if (aliases != null && aliases.length > 0) {
            definitionHolder = new BeanDefinitionHolder(abd, beanName, aliases);
        } else {
            definitionHolder = new BeanDefinitionHolder(abd, beanName);
        }
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
        return beanName;
    }
}
