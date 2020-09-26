public class InvalidSymbolException extends Exception {
    public InvalidSymbolException(Character invalidSymbol) {
        super("O símbolo '" + invalidSymbol + "' não é reconhecido pelo alfabeto");
    }
}
