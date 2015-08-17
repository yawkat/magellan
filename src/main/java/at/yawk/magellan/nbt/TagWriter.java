package at.yawk.magellan.nbt;

import at.yawk.magellan.nbt.lexer.Emitter;
import at.yawk.magellan.nbt.lexer.NeedOutputException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;

/**
 * @author yawkat
 */
public class TagWriter {
    final Emitter emitter;
    final Deque<Walker> walkerStack = new ArrayDeque<>();

    private final RootTag root;
    private boolean rootEmitted = false;

    private TagWriter(Emitter emitter, RootTag root) {
        this.emitter = emitter;
        this.root = root;
    }

    public static TagWriter create(Emitter emitter, RootTag root) {
        return new TagWriter(emitter, root);
    }

    public void emit() throws NeedOutputException {
        if (!rootEmitted) {
            emitter.setName(root.getName());
            push(root.getTag());
            rootEmitted = true;
        }
        while (!walkerStack.isEmpty()) {
            walkerStack.getLast().walk();
        }
    }

    void push(Tag tag) throws NeedOutputException {
        TagType type = tag.getType();
        switch (type) {
        case BYTE:
        case SHORT:
        case INT:
        case LONG:
            emitter.pushInteger(type, tag.longValue());
            break;
        case FLOAT:
        case DOUBLE:
            emitter.pushFloat(type, tag.doubleValue());
            break;
        case STRING:
            emitter.pushString(((StringTag) tag).getValue());
            break;
        case BYTE_ARRAY:
            emitter.pushByteArray(((ByteArrayTag) tag).getArray());
            break;
        case INT_ARRAY:
            emitter.pushIntArray(((IntArrayTag) tag).getArray());
            break;
        case LIST:
            ListTag list = (ListTag) tag;
            TagType ct = list.getComponentType();
            emitter.pushListStart(ct, list.size());
            walkerStack.addLast(new ListWalker(list));
            break;
        case COMPOUND:
            CompoundTag compound = (CompoundTag) tag;
            emitter.pushCompoundStart();
            walkerStack.addLast(new CompoundWalker(compound));
            break;
        default:
            throw new AssertionError(type);
        }
    }

    private interface Walker {
        void walk() throws NeedOutputException;
    }

    private class ListWalker implements Walker {
        private final ListTag tag;
        private int nextIndex;

        public ListWalker(ListTag tag) {
            this.tag = tag;
            this.nextIndex = 0;
        }

        @Override
        public void walk() throws NeedOutputException {
            if (nextIndex < tag.size()) {
                Tag tag = this.tag.getTag(nextIndex);
                push(tag);
                nextIndex++;
            } else {
                emitter.pushListEnd();
                walkerStack.removeLast();
            }
        }
    }

    private class CompoundWalker implements Walker {
        private final Iterator<Map.Entry<String, Tag>> iterator;
        private Map.Entry<String, Tag> next;

        public CompoundWalker(CompoundTag tag) {
            this.iterator = tag.getTags().entrySet().iterator();
        }

        private Map.Entry<String, Tag> next() {
            if (next == null && iterator.hasNext()) {
                next = iterator.next();
            }
            return next;
        }

        @Override
        public void walk() throws NeedOutputException {
            Map.Entry<String, Tag> next = next();
            if (next != null) {
                emitter.setName(next.getKey());
                push(next.getValue());
            } else {
                emitter.pushCompoundEnd();
                walkerStack.removeLast();
            }
            this.next = null;
        }
    }
}
