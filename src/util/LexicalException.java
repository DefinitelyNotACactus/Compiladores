package util;

public class LexicalException extends Exception {
    public LexicalException(String message, int line) {
        super("(Linha " + line + ") Erro léxico: " + message);
    }
}
