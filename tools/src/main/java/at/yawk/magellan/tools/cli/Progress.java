package at.yawk.magellan.tools.cli;

import javax.annotation.concurrent.ThreadSafe;

/**
 * @author yawkat
 */
@ThreadSafe
public interface Progress {
    void setValue(int value);

    void incrementValue(int amount);

    default void incrementValue() {
        incrementValue(1);
    }

    void setLabel(String label);

    void setMax(int max);

    void incrementMax(int amount);

    default void incrementMax() {
        incrementMax(1);
    }

    void remove();
}
