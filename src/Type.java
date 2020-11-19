public enum Type {
    RESERVADO("Palavra reservada"),
    IDENTIFICADOR("Identificador"),
    INTEIRO("Número Inteiro"),
    REAL("Número Real"),
    DELIMITADOR("Delimitador"),
    ATRIBUICAO("Atribuição"),
    OP_RELACIONAL("Operador relacional"),
    OP_ADITIVO("Operador aditivo"),
    OP_MULTIPLICATIVO("Operador multiplicativo"),
    MARK("Mark"); // Usado para o analisador semântico

    public final String name;

    Type(String name) {
        this.name = name;
    }
}
