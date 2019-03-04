package com.tf.util;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

/**
 * 资源加载工具类
 *
 * @author guoqw
 * @since 2017-04-19 16:41
 */
public class ResourceResolverUtil {

    private ResourceResolverUtil() {
    }

    private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();

    private static final DefaultResourceLoader RESOURCE_LOADER = new DefaultResourceLoader();

    public static Resource loadResource(String path) {
        return RESOURCE_LOADER.getResource(path);
    }

    public static Resource loadResourceFirst(String path) {
        Resource[] resources = loadResources(path);
        if (resources != null && resources.length > 0) {
            return resources[0];
        }
        return null;
    }

    public static Resource[] loadResources(String path) {
        try {
            return RESOURCE_PATTERN_RESOLVER.getResources(path);
        } catch (IOException e) {
            throw new IllegalArgumentException("无法解析路径path:" + path);
        }
    }

}
