public enum Type {
    RESERVADO("Palavra reservada"),
    IDENTIFICADOR("Identificador"),
    INTEIRO("Número Inteiro"),
    BOOLEANO("Booleano"),
    REAL("Número Real"),
    DELIMITADOR("Delimitador"),
    ATRIBUICAO("Atribuição"),
    OP_RELACIONAL("Operador relacional"),
    OP_ADITIVO("Operador aditivo"),
    OP_MULTIPLICATIVO("Operador multiplicativo"),
    // Tipos utilizados para o analisador semântico
    MARK("Marcação"),
    PROCEDURE("Procedimento"),
    PROGRAM("Programa");

    public final String name;

    Type(String name) {
        this.name = name;
    }
}
