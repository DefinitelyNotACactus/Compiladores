package util;

public class LexicalException extends Exception {
    public LexicalException(String message) {
        super("Erro léxico: " + message);
    }
}
