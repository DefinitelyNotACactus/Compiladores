package util;

public class UnfinishedCommentException extends Exception {
    public UnfinishedCommentException() {
        super("O comentário iniciado não foi fechado!");
    }
}
