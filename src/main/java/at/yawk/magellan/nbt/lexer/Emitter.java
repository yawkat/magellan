package at.yawk.magellan.nbt.lexer;

import at.yawk.magellan.nbt.TagType;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.annotation.Nullable;
import lombok.Setter;

/**
 * @author yawkat
 */
public class Emitter {
    @Setter ByteBuffer output;
    @Setter String name;
    Deque<CompositeType> composites = new ArrayDeque<>();

    private Emitter() {}

    public static Emitter create() {
        return new Emitter();
    }

    public void pushInteger(TagType type, long value) throws NeedOutputException {
        switch (type) {
        case BYTE:
            pushPrimitive(TagId.BYTE, 1);
            output.put((byte) value);
            break;
        case SHORT:
            pushPrimitive(TagId.SHORT, 2);
            output.putShort((short) value);
            break;
        case INT:
            pushPrimitive(TagId.INT, 4);
            output.putInt((int) value);
            break;
        case LONG:
            pushPrimitive(TagId.LONG, 8);
            output.putLong(value);
            break;
        default:
            throw new IllegalArgumentException(type + " is not an integer type");
        }
    }

    public void pushFloat(TagType type, double value) throws NeedOutputException {
        switch (type) {
        case FLOAT:
            pushPrimitive(TagId.FLOAT, 4);
            output.putFloat((float) value);
            break;
        case DOUBLE:
            pushPrimitive(TagId.DOUBLE, 8);
            output.putDouble(value);
            break;
        default:
            throw new IllegalArgumentException(type + " is not a float type");
        }
    }

    public void pushString(CharSequence string) throws NeedOutputException {
        if (string instanceof LazyCharSequence) {
            pushString(((LazyCharSequence) string).source);
        } else {
            pushString(LazyCharSequence.CHARSET.encode(CharBuffer.wrap(string)));
        }
    }

    /**
     * Push the given string primitive. The buffer positions will not be modified.
     */
    private void pushString(ByteBuffer value) throws NeedOutputException {
        pushPrimitive(TagId.STRING, 2 + value.remaining());
        pushString0(value);
    }

    /**
     * Push the given byte array primitive. The buffer positions will not be modified.
     */
    public void pushByteArray(ByteBuffer buffer) throws NeedOutputException {
        pushPrimitive(TagId.BYTE_ARRAY, 4 + buffer.remaining());
        output.putInt(buffer.remaining());
        output.put(buffer.slice());
    }

    /**
     * Push the given int array primitive. The buffer positions will not be modified.
     */
    public void pushIntArray(IntBuffer buffer) throws NeedOutputException {
        pushPrimitive(TagId.INT_ARRAY, 4 + buffer.remaining() * 4);
        output.putInt(buffer.remaining());
        output.asIntBuffer().put(buffer.slice());
        output.position(output.position() + buffer.remaining() * 4);
    }

    private void pushPrimitive(byte id, int dataLen) throws NeedOutputException {
        CompositeType compositeType = composites.peekLast();

        if (compositeType == CompositeType.COMPOUND ||
            // root tag
            compositeType == null) {

            ByteBuffer encodedName = LazyCharSequence.CHARSET.encode(name);
            checkRemaining(1 + 2 + encodedName.remaining() + dataLen);
            output.put(id);
            pushString0(encodedName);
        } else if (compositeType == CompositeType.LIST) {
            // todo: validate list member type
            checkRemaining(dataLen);
        } else {
            throw new AssertionError(compositeType);
        }
    }

    public void pushCompoundStart() {
        pushPrimitive(TagId.COMPOUND, 0);
        composites.addLast(CompositeType.COMPOUND);
    }

    public void pushCompoundEnd() {
        if (composites.removeLast() != CompositeType.COMPOUND) {
            throw new IllegalStateException("Not in compound");
        }
        output.put(TagId.END);
    }

    public void pushListStart(@Nullable TagType type, int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be positive");
        }
        if (type == null && n != 0) {
            throw new IllegalArgumentException("Need type when n != 0");
        }

        pushPrimitive(TagId.LIST, 5);
        output.put(type == null ? TagId.END : getId(type));
        output.putInt(n);
        composites.addLast(CompositeType.LIST);
    }

    private static byte getId(TagType type) {
        switch (type) {
        case BYTE:
            return TagId.BYTE;
        case SHORT:
            return TagId.SHORT;
        case INT:
            return TagId.INT;
        case LONG:
            return TagId.LONG;
        case FLOAT:
            return TagId.FLOAT;
        case DOUBLE:
            return TagId.DOUBLE;
        case BYTE_ARRAY:
            return TagId.BYTE_ARRAY;
        case INT_ARRAY:
            return TagId.INT_ARRAY;
        case COMPOUND:
            return TagId.COMPOUND;
        case LIST:
            return TagId.LIST;
        default:
            throw new AssertionError(type);
        }
    }

    public void pushListEnd() {
        if (composites.removeLast() != CompositeType.LIST) {
            throw new IllegalStateException("Not in list");
        }
    }

    private void pushString0(ByteBuffer value) {
        if (value.remaining() > 0xffff) {
            throw new IllegalArgumentException(
                    "String too large: maximum is " + 0xffff + " bytes but got " + value.remaining());
        }
        checkRemaining(2 + value.remaining());
        output.putShort((short) value.remaining());
        output.put(value.slice());
    }

    void checkRemaining(int n) {
        if (output.remaining() < n) { throw new NeedOutputException(); }
    }

    enum CompositeType {
        LIST,
        COMPOUND,
    }
}
