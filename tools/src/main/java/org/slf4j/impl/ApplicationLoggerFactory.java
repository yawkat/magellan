package org.slf4j.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * @author yawkat
 */
@RequiredArgsConstructor
class ApplicationLoggerFactory implements ILoggerFactory {
    private final LoggingProvider provider;

    @Override
    public Logger getLogger(String s) {
        return new ApplicationLoggerImpl(provider);
    }
}
