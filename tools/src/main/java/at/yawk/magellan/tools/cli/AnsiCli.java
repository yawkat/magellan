package at.yawk.magellan.tools.cli;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Queue;
import lombok.RequiredArgsConstructor;
import net.jcip.annotations.GuardedBy;
import org.slf4j.impl.Level;

/**
 * @author yawkat
 */
class AnsiCli implements Cli {
    final PrintStream stream;
    @GuardedBy("this")
    final Queue<ProgressImpl> progresses = new ArrayDeque<>();

    int width = 80; // todo

    AnsiCli(PrintStream stream) {
        this.stream = stream;
    }

    private void emit(String s) {
        stream.print(s);
    }

    private void csi(String s) {
        emit("\033[" + s);
    }

    @Override
    public Progress createProgress() {
        ProgressImpl impl = new ProgressImpl();
        synchronized (this) {
            progresses.offer(impl);
        }
        return impl;
    }

    @Override
    public boolean isLevelEnabled(int level) {
        return level >= Level.INFO;
    }

    @Override
    public void log(int level, String msg, Throwable t) {
        msg += '\n';
        if (t != null) {
            StringWriter writer = new StringWriter();
            t.printStackTrace(new PrintWriter(writer));
            msg += writer.toString();
        }
        switch (level) {
        case Level.TRACE:
            log("TRACE", 90, msg);
            break;
        case Level.DEBUG:
            log("DEBUG", 37, msg);
            break;
        case Level.INFO:
            log(" INFO", 36, msg);
            break;
        case Level.WARN:
            log(" WARN", 33, msg);
            break;
        case Level.ERROR:
            log("ERROR", 31, msg);
            break;
        default:
            log("     ", 0, msg);
            break;
        }
    }

    private synchronized void log(String levelName, int color, String msg) {
        csi("K");
        csi(color + "m");
        csi(1 + "m");
        emit("[" + levelName + "]");
        csi(0 + "m");
        emit(" " + msg);
        ProgressImpl progress = progresses.peek();
        if (progress != null) {
            progress.paintHere();
        }
        stream.flush();
    }

    synchronized void removeProgress(ProgressImpl progress) {
        progresses.remove(progress);
        ProgressImpl first = progresses.peek();
        if (first != null) {
            first.paint();
        }
    }

    @RequiredArgsConstructor
    private class ProgressImpl implements Progress {
        @GuardedBy("this")
        int value;
        @GuardedBy("this")
        int max;
        @GuardedBy("this")
        String label;

        @Override
        public synchronized void setValue(int value) {
            this.value = value;
            paint();
        }

        @Override
        public synchronized void incrementValue(int amount) {
            this.value += amount;
            paint();
        }

        @Override
        public synchronized void setLabel(String label) {
            this.label = label;
            paint();
        }

        @Override
        public synchronized void setMax(int max) {
            this.max = max;
            paint();
        }

        @Override
        public synchronized void remove() {
            removeProgress(this);
        }

        private void paint() {
            if (progresses.peek() != this) { return; }

            synchronized (AnsiCli.this) {
                if (progresses.peek() != this) { return; }
                paintHere();
                stream.flush();
            }
        }

        void paintHere() {
            int value;
            int max;
            String label;
            synchronized (this) {
                value = this.value;
                max = this.max;
                label = this.label;
            }

            String prefix = label + ": [";
            String suffix = "] " + value + "/" + max;

            StringBuilder barBuilder = new StringBuilder();
            int barWidth = width - prefix.length() - suffix.length();
            for (int i = 0; i < barWidth; i++) {
                int item = Math.round((float) i * max / barWidth);
                boolean complete = item <= value;
                barBuilder.append(complete ? '=' : ' ');
            }

            csi("K");
            emit(prefix + barBuilder + suffix + "\r");
        }
    }
}
