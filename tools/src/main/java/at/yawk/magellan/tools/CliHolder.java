package at.yawk.magellan.tools;

import at.yawk.magellan.tools.cli.Cli;
import org.slf4j.impl.ApplicationLogger;

/**
 * @author yawkat
 */
class CliHolder {
    static final Cli CLI;

    static {
        CLI = Cli.createSystemCli();
        ApplicationLogger.setLoggingProvider(CLI);
    }
}
