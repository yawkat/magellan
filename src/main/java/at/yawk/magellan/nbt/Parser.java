package at.yawk.magellan.nbt;

import at.yawk.magellan.nbt.lexer.Event;
import at.yawk.magellan.nbt.lexer.Lexer;
import at.yawk.magellan.nbt.lexer.NeedInputException;
import java.util.*;
import javax.annotation.Nullable;

/**
 * @author yawkat
 */
public class Parser {
    private final Lexer lexer;

    private final Deque<Tag> tagStack = new ArrayDeque<>();
    private RootTag rootTag;

    private Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public static Parser create(Lexer lexer) {
        return new Parser(lexer);
    }

    public RootTag parse() throws NeedInputException {
        if (rootTag == null) {
            // pass first token
            Tag tag = visit();
            rootTag = new RootTag(lexer.getName().toString(), tag);
        }

        while (!tagStack.isEmpty()) {
            Tag currentStruct = tagStack.getLast();
            Tag next = visit();
            if (next != null) {
                if (currentStruct.isList()) {
                    currentStruct.addTag(next);
                } else {
                    assert currentStruct.isCompound();
                    currentStruct.setTag(lexer.getName().toString(), next);
                }
            }
        }
        return rootTag;
    }

    @Nullable
    private Tag visit() {
        Event event = lexer.next();
        if (event == null) {
            throw new IllegalStateException("Already reached EOF");
        }
        switch (event) {
        case PRIMITIVE:
            return visitPrimitive();
        case START_LIST:
            Tag listTag = new ListTag(lexer.getComponentType(), new ArrayList<>());
            tagStack.addLast(listTag);
            return listTag;
        case END_LIST:
            tagStack.removeLast();
            return null;
        case START_COMPOUND:
            Tag compoundTag = new CompoundTag(new LinkedHashMap<>());
            tagStack.addLast(compoundTag);
            return compoundTag;
        case END_COMPOUND:
            tagStack.removeLast();
            return null;
        default:
            throw new AssertionError(event);
        }
    }

    private Tag visitPrimitive() {
        TagType type = lexer.getType();
        switch (type) {
        case BYTE:
        case SHORT:
        case INT:
        case LONG:
            return new IntegerTag(type, lexer.getLongValue());
        case FLOAT:
        case DOUBLE:
            return new FloatTag(type, lexer.getDoubleValue());
        case STRING:
            return new StringTag(lexer.getStringValue());
        case BYTE_ARRAY:
            return new ByteArrayTag(lexer.getByteArrayValue());
        case INT_ARRAY:
            return new IntArrayTag(lexer.getByteArrayValue().asIntBuffer());
        default:
            throw new AssertionError("Not primitive: " + type);
        }
    }
}
