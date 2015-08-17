package at.yawk.magellan.nbt.lexer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import lombok.EqualsAndHashCode;

/**
 * @author yawkat
 */
@ThreadSafe
@EqualsAndHashCode(of = "source")
class LazyCharSequence implements CharSequence {
    static final Charset CHARSET = StandardCharsets.UTF_8;

    final ByteBuffer source;
    private String value; // lazily decoded

    LazyCharSequence(ByteBuffer source) {
        this.source = source;
    }

    private String getValue() {
        if (value == null) {
            value = CHARSET.decode(source.slice()).toString();
        }
        return value;
    }

    @Override
    public int length() {
        return getValue().length();
    }

    @Override
    public char charAt(int index) {
        return getValue().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return getValue().subSequence(start, end);
    }

    @Override
    @Nonnull
    public String toString() {
        return getValue();
    }
}
