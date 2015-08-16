package at.yawk.magellan.nbt.lexer;

import at.yawk.magellan.nbt.TagType;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;
import javax.annotation.Nullable;
import lombok.Getter;

/**
 * @author yawkat
 */
public class Lexer {
    ByteBuffer input;
    private boolean maySliceInput;

    @Getter CharSequence name;

    @Getter TagType type;
    @Getter private CharSequence stringValue;
    @Getter private long longValue;
    @Getter private double doubleValue;
    @Getter private ByteBuffer byteArrayValue;

    private final Deque<Walker> walkerStack = new ArrayDeque<>();

    private Lexer() {}

    public static Lexer create() {
        Lexer lexer = new Lexer();
        lexer.pushWalker(new RootWalker(lexer));
        return lexer;
    }

    public void setInput(ByteBuffer input, boolean maySliceInput) {
        this.input = input;
        this.maySliceInput = maySliceInput;
    }

    @Nullable
    public Event next() {
        if (walkerStack.isEmpty()) { return null; }

        input.mark();
        try {
            return walkerStack.getLast().next();
        } catch (NeedInputException e) {
            input.reset();
            throw e;
        }
    }

    void popWalker() {
        walkerStack.removeLast();
    }

    void pushWalker(Walker walker) {
        walkerStack.addLast(walker);
    }

    private ByteBuffer getBytes(int n) throws NeedInputException {
        checkRemaining(n);
        if (maySliceInput) {
            int oldLimit = input.limit();
            int end = input.position() + n;

            input.limit(end);
            ByteBuffer slice = input.slice();
            input.position(end);
            input.limit(oldLimit);
            return slice;
        } else {
            ByteBuffer buffer = input.isDirect() ?
                    ByteBuffer.allocateDirect(n) :
                    ByteBuffer.allocate(n);

            int oldLimit = input.limit();
            int end = input.position() + n;
            input.limit(end);
            buffer.put(input);
            input.limit(oldLimit);
            buffer.flip();

            return buffer;
        }
    }

    CharSequence getString() {
        checkRemaining(2);
        int n = input.getShort() & 0xffff;
        return new LazyCharSequence(getBytes(n));
    }

    private Event walkByte() {
        checkRemaining(1);
        longValue = input.get() & 0xffL;
        type = TagType.BYTE;
        return Event.PRIMITIVE;
    }

    private Event walkShort() {
        checkRemaining(2);
        longValue = input.getShort() & 0xffffL;
        type = TagType.SHORT;
        return Event.PRIMITIVE;
    }

    private Event walkInt() {
        checkRemaining(4);
        longValue = input.getInt() & 0xffffffffL;
        type = TagType.INT;
        return Event.PRIMITIVE;
    }

    private Event walkLong() {
        checkRemaining(8);
        longValue = input.getLong();
        type = TagType.LONG;
        return Event.PRIMITIVE;
    }

    private Event walkFloat() {
        checkRemaining(4);
        doubleValue = input.getFloat();
        type = TagType.FLOAT;
        return Event.PRIMITIVE;
    }

    private Event walkDouble() {
        checkRemaining(8);
        doubleValue = input.getDouble();
        type = TagType.DOUBLE;
        return Event.PRIMITIVE;
    }

    private Event walkString() {
        stringValue = getString();
        type = TagType.STRING;
        return Event.PRIMITIVE;
    }

    private Event walkByteArray() {
        checkRemaining(4);
        int n = input.getInt();
        byteArrayValue = getBytes(n);
        type = TagType.BYTE_ARRAY;
        return Event.PRIMITIVE;
    }

    private Event walkIntArray() {
        checkRemaining(4);
        int n = Math.multiplyExact(input.getInt(), 4);
        byteArrayValue = getBytes(n);
        type = TagType.INT_ARRAY;
        return Event.PRIMITIVE;
    }

    private Event walkList() {
        checkRemaining(5);
        byte type = input.get();
        int length = input.getInt();
        pushWalker(new ListWalker(this, length, type));
        return Event.START_LIST;
    }

    private Event walkCompound() {
        pushWalker(new CompoundWalker(this));
        return Event.START_COMPOUND;
    }

    static Function<Lexer, Event> elementWalker(byte id) {
        switch (id) {
        case TagId.BYTE:
            return Lexer::walkByte;
        case TagId.SHORT:
            return Lexer::walkShort;
        case TagId.INT:
            return Lexer::walkInt;
        case TagId.LONG:
            return Lexer::walkLong;
        case TagId.FLOAT:
            return Lexer::walkFloat;
        case TagId.DOUBLE:
            return Lexer::walkDouble;
        case TagId.STRING:
            return Lexer::walkString;
        case TagId.BYTE_ARRAY:
            return Lexer::walkByteArray;
        case TagId.INT_ARRAY:
            return Lexer::walkIntArray;
        case TagId.LIST:
            return Lexer::walkList;
        case TagId.COMPOUND:
            return Lexer::walkCompound;
        default:
            throw new UnsupportedOperationException(String.valueOf(id));
        }
    }

    void checkRemaining(int n) {
        if (input.remaining() < n) { throw new NeedInputException(); }
    }

}
