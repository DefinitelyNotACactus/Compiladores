public enum Type {
    RESERVADO("Palavra reservada"),
    IDENTIFICADOR("Identificador"),
    INTEIRO("Número Inteiro"),
    REAL("Número Real"),
    DELIMITADOR("Delimitador"),
    ATRIBUICAO("Comando de atribuição"),
    OP_RELACIONAL("Operador relacional"),
    OP_ADITIVO("Operador aditivo"),
    OP_MULTIPLICATIVO("Operador multiplicativo");

    public final String name;

    Type(String name) {
        this.name = name;
    }
}
