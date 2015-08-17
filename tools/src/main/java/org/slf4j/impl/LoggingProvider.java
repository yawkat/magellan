package org.slf4j.impl;

/**
 * @author yawkat
 */
public interface LoggingProvider {
    boolean isLevelEnabled(int level);

    void log(int level, String msg, Throwable t);
}
