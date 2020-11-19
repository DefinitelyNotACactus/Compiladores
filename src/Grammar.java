import util.SemanticException;
import util.SyntaxException;

/** @noinspection ALL*/
public interface Grammar {
    void programa() throws SyntaxException, SemanticException;
    void declaracoes_variaveis() throws SyntaxException, SemanticException;
    void lista_declaracoes_variaveis() throws SyntaxException, SemanticException;
    void lista_de_identificadores() throws SyntaxException, SemanticException;
    void tipo() throws SyntaxException, SemanticException;
    void declaracoes_de_subprogramas() throws SyntaxException, SemanticException;
    void declaracao_de_subprograma() throws SyntaxException, SemanticException;
    void argumentos() throws SyntaxException, SemanticException;
    void lista_de_parametros() throws SyntaxException, SemanticException;
    void comando_composto() throws SyntaxException, SemanticException;
    void comandos_opcionais() throws SyntaxException, SemanticException;
    void lista_de_comandos() throws SyntaxException, SemanticException;
    void comando() throws SyntaxException, SemanticException;
    void parte_else() throws SyntaxException, SemanticException;
    boolean variavel() throws SyntaxException, SemanticException;
    void ativacao_de_procedimento() throws SyntaxException, SemanticException;
    void lista_de_expressoes() throws SyntaxException, SemanticException;
    void expressao() throws SyntaxException, SemanticException;
    void expressao_simples() throws SyntaxException, SemanticException;
    void termo() throws SyntaxException, SemanticException;
    void fator() throws SyntaxException, SemanticException;
    void sinal() throws SyntaxException, SemanticException;
    void op_relacional() throws SyntaxException, SemanticException;
    void op_aditivo() throws SyntaxException, SemanticException;
    void op_multiplicativo() throws SyntaxException, SemanticException;
}
