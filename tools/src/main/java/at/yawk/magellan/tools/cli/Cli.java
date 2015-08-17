package at.yawk.magellan.tools.cli;

import javax.annotation.concurrent.ThreadSafe;
import org.slf4j.impl.LoggingProvider;

/**
 * @author yawkat
 */
@ThreadSafe
public interface Cli extends LoggingProvider {
    static Cli createSystemCli() {
        return System.console() == null ?
                new StreamCli(System.out) :
                new AnsiCli(System.out);
    }

    Progress createProgress();
}
