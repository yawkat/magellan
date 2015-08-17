package at.yawk.magellan.nbt;

import java.nio.Buffer;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Wither;

/**
 * @author yawkat
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Wither
public class Printer {
    private static final Printer PRETTY = new Printer()
            .withIndent("  ")
            .withMaxNumericPerLine(4)
            .withMaxOtherPerLine(1)
            .withCompact(false)
            .withQuoteCompoundKeys(false)
            .withTypedPrimitives(false);
    private static final Printer COMPACT = new Printer()
            .withIndent(null)
            .withMaxNumericPerLine(Integer.MAX_VALUE)
            .withMaxOtherPerLine(Integer.MAX_VALUE)
            .withCompact(true)
            .withQuoteCompoundKeys(true)
            .withTypedPrimitives(true);

    public static Printer pretty() {
        return PRETTY;
    }

    public static Printer compact() {
        return COMPACT;
    }

    @Nullable private final String indent;
    private final int maxNumericPerLine;
    private final int maxOtherPerLine;
    private final boolean compact;
    private final boolean quoteCompoundKeys;
    private final boolean typedPrimitives;

    private Printer() {
        maxOtherPerLine = -1;
        maxNumericPerLine = -1;
        indent = null;
        compact = false;
        quoteCompoundKeys = false;
        typedPrimitives = false;
    }

    public String print(Tag tag) {
        Worker worker = new Worker();
        worker.print(tag);
        return worker.builder.toString();
    }

    private class Worker {
        final StringBuilder builder = new StringBuilder();
        private int indentLevel = 0;

        void print(Tag tag) {
            switch (tag.getType()) {
            case BYTE:
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
                if (typedPrimitives) { print(tag.getType().name().toLowerCase() + "("); }
                print(tag.stringValue());
                if (typedPrimitives) { print(")"); }
                break;
            case STRING:
                if (typedPrimitives) { print("string("); }
                print('"' + tag.stringValue() + '"');
                if (typedPrimitives) { print(")"); }
                break;
            case INT_ARRAY:
                printArray(tag, ((IntArrayTag) tag).getArray());
                break;
            case BYTE_ARRAY:
                printArray(tag, ((ByteArrayTag) tag).getArray());
                break;
            case LIST:
                printList((ListTag) tag);
                break;
            case COMPOUND:
                printCompound((CompoundTag) tag);
                break;
            }
        }

        private void print(String line) {
            builder.append(line);
        }

        private void newline() {
            if (indent != null) {
                builder.append('\n');
                for (int i = 0; i < indentLevel; i++) {
                    builder.append(indent);
                }
            }
        }

        private void printArray(Tag tag, Buffer buffer) {
            switch (tag.getType()) {
            case INT_ARRAY:
                print("IntArray");
                break;
            case BYTE_ARRAY:
                print("IntArray");
                break;
            default:
                throw new AssertionError(tag.getType());
            }
            print("[length=" + buffer.capacity() + "]");
        }

        private void printList(ListTag tag) {
            print(compact ? "List[" : "List [");
            if (tag.size() == 0) {
                print("]");
                return;
            }

            TagType componentType = tag.getComponentType();
            assert componentType != null; // size would be 0
            int maxPerLine = componentType.isNumber() ? maxNumericPerLine : maxOtherPerLine;
            if (tag.size() <= maxPerLine && !componentType.isComposite()) {
                int i = 0;
                for (Tag child : tag) {
                    print(child);
                    if (++i != tag.size()) {
                        print(", ");
                    }
                }
            } else {
                indentLevel++;
                int i = 0;
                for (Tag child : tag) {
                    if ((i % maxPerLine) == 0) {
                        newline();
                    }
                    print(child);
                    if (++i != tag.size()) {
                        print(", ");
                    }
                }
                indentLevel--;
                newline();
            }
            print("]");
        }

        private void printCompound(CompoundTag tag) {
            print(compact ? "Compound{" : "Compound {");
            if (tag.size() == 0) {
                print("}");
                return;
            }

            indentLevel++;
            int i = 0;
            for (Map.Entry<String, Tag> child : tag.getTags().entrySet()) {
                newline();
                if (quoteCompoundKeys) { print("\""); }
                print(child.getKey());
                if (quoteCompoundKeys) { print("\""); }
                print(compact ? "=" : " = ");
                print(child.getValue());
                if (++i != tag.size()) {
                    print(", ");
                }
            }
            indentLevel--;
            newline();

            print("}");
        }
    }
}
