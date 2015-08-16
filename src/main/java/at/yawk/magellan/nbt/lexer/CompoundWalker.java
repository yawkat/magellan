package at.yawk.magellan.nbt.lexer;

import lombok.RequiredArgsConstructor;

/**
 * @author yawkat
 */
@RequiredArgsConstructor
class CompoundWalker implements Walker {
    private final Lexer lexer;

    @Override
    public Event next() throws NeedInputException {
        lexer.checkRemaining(1);
        byte tagId = lexer.input.get();
        if (tagId == TagId.END) {
            lexer.popWalker();
            return Event.END_COMPOUND;
        } else {
            // getString *first* since this might need input
            lexer.name = lexer.getString();
            return Lexer.elementWalker(tagId).apply(lexer);
        }
    }
}
