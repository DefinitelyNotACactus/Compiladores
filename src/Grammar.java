import util.SyntaxException;

/** @noinspection ALL*/
public interface Grammar {
    void programa() throws SyntaxException;
    void declaracoes_variaveis() throws SyntaxException;
    void lista_declaracoes_variaveis() throws SyntaxException;
    void lista_declaracoes_variaveis2() throws SyntaxException;
    void lista_de_identificadores() throws SyntaxException;
    void lista_de_identificadores2() throws SyntaxException;
    void tipo() throws SyntaxException;
    void declaracoes_de_subprogramas() throws SyntaxException;
    void declaracao_de_subprograma() throws SyntaxException;
    void argumentos() throws SyntaxException;
    void lista_de_parametros() throws SyntaxException;
    void lista_de_parametros2() throws SyntaxException;
    void comando_composto() throws SyntaxException;
    void comandos_opcionais() throws SyntaxException;
    void lista_de_comandos() throws SyntaxException;
    void comando() throws SyntaxException;
    void parte_else() throws SyntaxException;
    void variavel() throws SyntaxException;
    void ativacao_de_procedimento() throws SyntaxException;
    void lista_de_expressoes() throws SyntaxException;
    void expressao() throws SyntaxException;
    void expressao_simples() throws SyntaxException;
    void termo() throws SyntaxException;
    void fator() throws SyntaxException;
    void sinal() throws SyntaxException;
    void op_relacional() throws SyntaxException;
    void op_aditivo() throws SyntaxException;
    void op_multiplicativo() throws SyntaxException;
}
