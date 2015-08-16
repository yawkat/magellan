package at.yawk.magellan.nbt.lexer;

import lombok.RequiredArgsConstructor;

/**
 * @author yawkat
 */
@RequiredArgsConstructor
class RootWalker implements Walker {
    private final Lexer lexer;

    private boolean done = false;

    @Override
    public Event next() throws NeedInputException {
        if (done) {
            lexer.popWalker();
            return null;
        } else {
            lexer.checkRemaining(1);
            byte tagId = lexer.input.get();
            // getString *first* since this might need input
            lexer.name = lexer.getString();
            Event event = Lexer.elementWalker(tagId).apply(lexer);
            done = true;
            return event;
        }
    }
}
