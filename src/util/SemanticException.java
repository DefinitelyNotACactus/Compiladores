package util;

public class SemanticException extends Exception {
    public SemanticException(String message, int line) {
        super("(Linha " + line + ") Erro semântico: " + message);
    }
}
