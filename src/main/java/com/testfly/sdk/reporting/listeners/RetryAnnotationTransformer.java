package com.testfly.sdk.reporting.listeners;

import com.testfly.sdk.core.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class RetryAnnotationTransformer implements IAnnotationTransformer {

    private static final Logger logger = LogManager.getLogger(RetryAnnotationTransformer.class);

    @Override
    public void transform(ITestAnnotation annotation, Class testClass,
            Constructor testConstructor, Method testMethod) {
        if (ConfigManager.get().isRetryEnabled()) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
            logger.debug("Retry enabled - attached RetryAnalyzer to test: {}",
                    testMethod != null ? testMethod.getName() : "unknown");
        }
    }
}