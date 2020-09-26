public class Token {

    private final String value;
    private final Type type;
    private final int line;

    public Token(String value, Type type, int line) {
        this.value = value;
        this.type = type;
        this.line = line;
    }

    public String getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    public int getLine() {
        return line;
    }
}
