package com.ijson;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import java.util.List;

/**
 * 简单的构建验证测试
 * 验证所有主要依赖是否可以正确导入和使用
 */
public class GradleBuildTest {

    private static final Logger logger = LoggerFactory.getLogger(GradleBuildTest.class);

    @Test
    public void testGuavaDependency() {
        // 测试Guava依赖
        String testString = null;
        boolean isEmpty = Strings.isNullOrEmpty(testString);
        assert isEmpty;
        
        List<String> list = Lists.newArrayList("test");
        assert list.size() == 1;
        
        logger.info("Guava dependency test passed");
    }

    @Test
    public void testSlf4jDependency() {
        // 测试SLF4J依赖
        logger.info("SLF4J dependency test passed");
        logger.debug("Debug message");
        logger.warn("Warning message");
    }

    @Test
    public void testCuratorDependency() {
        // 测试Curator依赖（只测试类加载，不实际连接）
        try {
            Class.forName("org.apache.curator.framework.CuratorFramework");
            logger.info("Curator dependency test passed");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Curator dependency not found", e);
        }
    }

    @Test
    public void testLogbackDependency() {
        // 测试Logback依赖
        try {
            Class.forName("ch.qos.logback.classic.Logger");
            logger.info("Logback dependency test passed");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Logback dependency not found", e);
        }
    }

    @Test
    public void testProjectClasses() {
        // 测试项目自身的类是否可以加载
        try {
            Class.forName("com.ijson.config.ConfigFactory");
            Class.forName("com.ijson.config.base.Config");
            Class.forName("com.ijson.config.helper.ConfigHelper");
            logger.info("Project classes test passed");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Project classes not found", e);
        }
    }
}
