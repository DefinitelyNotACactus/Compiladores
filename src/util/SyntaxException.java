package util;

public class SyntaxException extends Exception {
    public SyntaxException(String message, int line) {
        super("(Linha " + line + ") Erro sintático: " + message);
    }
}
