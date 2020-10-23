package util;

public class EmptyCommandException extends SyntaxException {
    public EmptyCommandException(String message, int line) {
        super(message, line);
    }
}
