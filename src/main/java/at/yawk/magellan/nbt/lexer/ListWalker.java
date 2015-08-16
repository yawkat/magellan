package at.yawk.magellan.nbt.lexer;

import java.util.function.Function;

/**
 * @author yawkat
 */
class ListWalker implements Walker {
    private final Lexer lexer;
    private final Function<Lexer, Event> elementWalker;
    private int remaining;

    ListWalker(Lexer lexer, int count, byte id) {
        this.lexer = lexer;
        this.remaining = count;
        this.elementWalker = Lexer.elementWalker(id);
    }

    @Override
    public Event next() throws NeedInputException {
        if (remaining <= 0) {
            lexer.popWalker();
            return Event.END_LIST;
        } else {
            Event event = elementWalker.apply(lexer);
            remaining--;
            return event;
        }
    }
}
