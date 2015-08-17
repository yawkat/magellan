package at.yawk.magellan.tools.cli;

import java.io.PrintStream;
import lombok.RequiredArgsConstructor;
import org.slf4j.impl.Level;

/**
 * @author yawkat
 */
@RequiredArgsConstructor
class StreamCli implements Cli {
    private static final Progress NOOP_PROGRESS = new Progress() {
        @Override
        public void setValue(int value) {}

        @Override
        public void incrementValue(int amount) {}

        @Override
        public void setLabel(String label) {}

        @Override
        public void setMax(int max) {}

        @Override
        public void incrementMax(int amount) {}

        @Override
        public void remove() {}
    };

    final PrintStream stream;

    @Override
    public Progress createProgress() {
        return NOOP_PROGRESS;
    }

    @Override
    public boolean isLevelEnabled(int level) {
        return level >= Level.INFO;
    }

    @Override
    public synchronized void log(int level, String msg, Throwable t) {
        String levelString;
        switch (level) {
        case Level.TRACE:
            levelString = "TRACE";
            break;
        case Level.DEBUG:
            levelString = "DEBUG";
            break;
        case Level.INFO:
            levelString = " INFO";
            break;
        case Level.WARN:
            levelString = " WARN";
            break;
        case Level.ERROR:
            levelString = "ERROR";
            break;
        default:
            levelString = "     ";
            break;
        }

        stream.println("[" + levelString + "] " + msg);
        if (t != null) {
            t.printStackTrace(stream);
        }
    }
}
