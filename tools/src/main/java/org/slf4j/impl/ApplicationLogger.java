package org.slf4j.impl;

import java.util.Objects;

/**
 * @author yawkat
 */
public class ApplicationLogger {
    static LoggingProvider provider;

    public static void setLoggingProvider(LoggingProvider provider) {
        if (ApplicationLogger.provider != null) {
            throw new IllegalStateException("Provider already set");
        }
        ApplicationLogger.provider = Objects.requireNonNull(provider, "provider");
    }
}
