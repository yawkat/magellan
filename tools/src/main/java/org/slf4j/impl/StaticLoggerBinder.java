package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 * @author yawkat
 */
public class StaticLoggerBinder implements LoggerFactoryBinder {
    private static final StaticLoggerBinder singleton = new StaticLoggerBinder();

    private final ApplicationLoggerFactory factory =
            new ApplicationLoggerFactory(ApplicationLogger.provider);

    public static StaticLoggerBinder getSingleton() {
        return singleton;
    }

    @Override
    public ILoggerFactory getLoggerFactory() {
        if (factory == null) {
            throw new IllegalStateException("Application logger not initialized");
        }
        return factory;
    }

    @Override
    public String getLoggerFactoryClassStr() {
        return "org.slf4j.impl.ApplicationLoggerFactory";
    }
}
