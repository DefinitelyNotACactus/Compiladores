public class Validator {
    public static boolean isLetter(Character c) {
        return c.toString().matches("^[a-zA-Z]$");
    }

    public static boolean isNumber(Character c) {
        return c.toString().matches("^[0-9]$");
    }

    public static boolean isNumberOrCharacter(Character c) {
        return c.toString().matches("^[a-zA-Z0-9]$");
    }
}
