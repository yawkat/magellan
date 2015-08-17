package org.slf4j.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

/**
 * @author yawkat
 */
@RequiredArgsConstructor
class ApplicationLoggerImpl extends MarkerIgnoringBase {
    final LoggingProvider provider;

    private boolean isLevelEnabled(int level) {
        return provider.isLevelEnabled(level);
    }

    private void log0(int level, String msg, Throwable t) {
        provider.log(level, msg, t);
    }

    private void log(int level, String msg) {
        if (isLevelEnabled(level)) {
            log0(level, msg, null);
        }
    }

    private void log(int level, String format, Object arg) {
        if (isLevelEnabled(level)) {
            FormattingTuple tuple = MessageFormatter.format(format, arg);
            log0(level, tuple.getMessage(), tuple.getThrowable());
        }
    }

    private void log(int level, String format, Object arg1, Object arg2) {
        if (isLevelEnabled(level)) {
            FormattingTuple tuple = MessageFormatter.format(format, arg1, arg2);
            log0(level, tuple.getMessage(), tuple.getThrowable());
        }
    }

    private void log(int level, String format, Object... arguments) {
        if (isLevelEnabled(level)) {
            FormattingTuple tuple = MessageFormatter.arrayFormat(format, arguments);
            log0(level, tuple.getMessage(), tuple.getThrowable());
        }
    }

    private void log(int level, String msg, Throwable t) {
        if (isLevelEnabled(level)) {
            log0(level, msg, t);
        }
    }

    @Override
    public boolean isTraceEnabled() {
        return isLevelEnabled(Level.TRACE);
    }

    @Override
    public void trace(String msg) {
        log(Level.TRACE, msg);
    }

    @Override
    public void trace(String format, Object arg) {
        log(Level.TRACE, format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        log(Level.TRACE, format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments) {
        log(Level.TRACE, format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        log(Level.TRACE, msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return isLevelEnabled(Level.DEBUG);
    }

    @Override
    public void debug(String msg) {
        log(Level.DEBUG, msg);
    }

    @Override
    public void debug(String format, Object arg) {
        log(Level.DEBUG, format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        log(Level.DEBUG, format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        log(Level.DEBUG, format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        log(Level.DEBUG, msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return isLevelEnabled(Level.INFO);
    }

    @Override
    public void info(String msg) {
        log(Level.INFO, msg);
    }

    @Override
    public void info(String format, Object arg) {
        log(Level.INFO, format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        log(Level.INFO, format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        log(Level.INFO, format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        log(Level.INFO, msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return isLevelEnabled(Level.WARN);
    }

    @Override
    public void warn(String msg) {
        log(Level.WARN, msg);
    }

    @Override
    public void warn(String format, Object arg) {
        log(Level.WARN, format, arg);
    }

    @Override
    public void warn(String format, Object... arguments) {
        log(Level.WARN, format, arguments);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        log(Level.WARN, format, arg1, arg2);
    }

    @Override
    public void warn(String msg, Throwable t) {
        log(Level.WARN, msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return isLevelEnabled(Level.ERROR);
    }

    @Override
    public void error(String msg) {
        log(Level.ERROR, msg);
    }

    @Override
    public void error(String format, Object arg) {
        log(Level.ERROR, format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        log(Level.ERROR, format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        log(Level.ERROR, format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        log(Level.ERROR, msg, t);
    }
}
