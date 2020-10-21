import util.SyntaxException;

import java.util.List;

/** @noinspection ALL*/
public class Syntax implements Grammar {
    private Token token;
    private List<Token> tokenTable;
    private int currentIndex;

    public Syntax(List<Token> tokenTable) {
        this.tokenTable = tokenTable;

        currentIndex = -1;
    }

    private Token getNext() {
        currentIndex++;
        return tokenTable.get(currentIndex);
    }

    private Token getPrevious() {
        currentIndex--;
        return tokenTable.get(currentIndex);
    }


    @Override
    public void programa() throws SyntaxException {
        token = getNext();
        if(token.getValue().equals("program")) {
            token = getNext();
            if(token.getType() == Type.IDENTIFICADOR) {
                token = getNext();
                if(token.getValue().equals(";")) {
                    token = getNext();
                    declaracoes_variaveis();
                    declaracoes_de_subprogramas();
                    comando_composto();
                    token = getNext();
                    if(!token.getValue().equals(".")) {
                        throw new SyntaxException("Programa não terminado com ponto", token.getLine());
                    }
                } else {
                    throw new SyntaxException("Identificador não acompanhado por ';'", token.getLine());
                }
            } else {
                throw new SyntaxException("Programa sem identificador", token.getLine());
            }
        } else {
            throw new SyntaxException("Primeiro símbolo lido não é 'program'", token.getLine());
        }
    }

    @Override
    public void declaracoes_variaveis() throws SyntaxException {
        if(token.getValue().equals("var")) {
            lista_declaracoes_variaveis();
        }
    }

    /** lista_declarações_variáveis
     * Reescrito como
     * lista_declarações_variáveis -> lista_de_idenficiadores: tipo; lista_declarações_variáveis2
     * @throws SyntaxException Erro sintático
     */
    @Override
    public void lista_declaracoes_variaveis() throws SyntaxException {
        lista_de_identificadores();
        token = getNext();
        if(token.getValue().equals(":")) {
            tipo();
            token = getNext();
            if(!token.getValue().equals(";")) {
                throw new SyntaxException("';' não acompanha o tipo", token.getLine());
            }
            lista_declaracoes_variaveis2();
        } else {
            throw new SyntaxException("':' não acompanha a lista de identificadores", token.getLine());
        }
    }

    /** Remover recursão a esquerda de lista_declarações_variáveis
     * lista_declarações_variáveis2 -> id lista_de_identificadores2: tipo; lista_declarações_variáveis2 | vazio
     * @throws SyntaxException Erro sintático
     */
    @Override
    public void lista_declaracoes_variaveis2() throws SyntaxException {
        token = getNext();
        if(token.getType() == Type.IDENTIFICADOR) { // Caso contratrário foi lido o "vazio"
            lista_de_identificadores2();
            token = getNext();
            if (token.getValue().equals(":")) {
                tipo();
                if (!token.getValue().equals(";")) {
                    throw new SyntaxException("';' não acompanha o tipo", token.getLine());
                }
                lista_declaracoes_variaveis2();
            } else {
                throw new SyntaxException("':' não acompanha a lista de identificadores", token.getLine());
            }
        } else { // O símbolo lido não ser identificador equivale ao 'vazio', voltar atrás na leitura
            token = getPrevious();
        }
    }

    /** lista_de_identificadores
     * Reescrito como
     * lista_de_identificadores -> id lista_de_identificadores2
     * @throws SyntaxException Erro sintático
     */
    @Override
    public void lista_de_identificadores() throws SyntaxException {
        token = getNext();
        if(token.getType() != Type.IDENTIFICADOR) {
            throw new SyntaxException("Identificador não localizado", token.getLine());
        }
        lista_de_identificadores2();
    }

    /** Remover recursão a esquerda de lista_de_identificadores (lista_de_identificadores2)
     * lista_de_identificadores2 -> ,id lista_de_identificadores2 | vazio
     * @throws SyntaxException Erro sintático
     */
    @Override
    public void lista_de_identificadores2() throws SyntaxException {
        token = getNext();
        if(token.getValue().equals(",")) { // Ausência de ',' equivale a vazio
            token = getNext();
            if(token.getType() != Type.IDENTIFICADOR) {
                throw new SyntaxException("',' não acompanhado de identificador", token.getLine());
            }
            lista_de_identificadores2();
        } else { // Lido um 'vazio', volta na leitura para deixar a leitura do símbolo para outra chamada
            token = getPrevious();
        }
    }

    @Override
    public void tipo() throws SyntaxException {
        token = getNext();
        if(!token.getValue().equals("integer") && !token.getValue().equals("real") && !token.getValue().equals("boolean")) {
            throw new SyntaxException(token.getValue() + " não é um tipo", token.getLine());
        }
    }

    @Override
    public void declaracoes_de_subprogramas() throws SyntaxException {
        // TODO
    }

    @Override
    public void declaracao_de_subprograma() throws SyntaxException {
        token = getNext();
        if(token.getValue().equals("procedure")) {
            token = getNext();
            if(token.getType() == Type.IDENTIFICADOR) {
                argumentos();
                token = getNext();
                if(token.getValue().equals(";")) {
                    declaracoes_variaveis();
                    declaracoes_de_subprogramas();
                    comando_composto();
                } else {
                    throw new SyntaxException("';' Faltando", token.getLine());
                }
            } else {
                throw new SyntaxException(token.getValue() + " não é um identificador", token.getLine());
            }
        }
    }

    @Override
    public void argumentos() throws SyntaxException {
        token = getNext();
        if(!token.getValue().equals("(")) { // A ausência de '(' equivale ao vazio
            token = getPrevious();
        }
        lista_de_parametros();
        token = getNext();
        if(!token.getValue().equals(")")) {
            throw new SyntaxException("Argumentos com ')' faltando", token.getLine());
        }
    }

    /** lista_de_parametros
     * Reescrito como
     * lista_de_parametros -> lista_de_identificadores: tipo; lista_de_parametros2
     * @throws SyntaxException Erro sintático
     */
    @Override
    public void lista_de_parametros() throws SyntaxException {
        lista_de_identificadores();
        token = getNext();
        if(!token.getValue().equals(":")) {
            throw new SyntaxException("':' faltando após a lista de identificadores", token.getLine());
        }
        tipo();
        token = getNext();
        if(!token.getValue().equals(";")) {
            throw new SyntaxException("';' faltando após o tipo", token.getLine());
        }
        lista_de_parametros2();
    }

    /** lista_de_parametros2
     * lista_de_parametros2 -> id lista_de_identificadores2:tipo; | vazio
     * @throws SyntaxException
     */
    @Override
    public void lista_de_parametros2() throws SyntaxException {
        token = getNext();
        if(token.getType() == Type.IDENTIFICADOR) {
            lista_de_identificadores2();
            token = getNext();
            if(!token.getValue().equals(":")) {
                throw new SyntaxException("A lista de identificadores não é seguida por ':'", token.getLine());
            }
            tipo();
            token = getNext();
            if(!token.getValue().equals(";")) {
                throw new SyntaxException("Lista de parâmetros não encerrada com ';'", token.getLine());
            }
        } else { // A ausência de identificador significa entrada vazia
            token = getPrevious();
        }
    }

    @Override
    public void comando_composto() throws SyntaxException {
        token = getNext();
        if(token.getValue().equals("begin")) {
            comandos_opcionais();
            token = getNext();
            if(!token.getValue().equals("end")) {
                throw new SyntaxException("Comando composto não encerrado com end", token.getLine());
            }
        } else {
            throw new SyntaxException("Comando composto não iniciado com begin", token.getLine());
        }
    }

    @Override
    public void comandos_opcionais() throws SyntaxException {

    }

    @Override
    public void lista_de_comandos() throws SyntaxException {

    }

    @Override
    public void comando() throws SyntaxException {

    }

    @Override
    public void parte_else() throws SyntaxException {

    }

    @Override
    public void variavel() throws SyntaxException {

    }

    @Override
    public void ativacao_de_procedimento() throws SyntaxException {

    }

    @Override
    public void lista_de_expressoes() throws SyntaxException {

    }

    @Override
    public void expressao() throws SyntaxException {

    }

    @Override
    public void expressao_simples() throws SyntaxException {

    }

    @Override
    public void termo() throws SyntaxException {

    }

    @Override
    public void fator() throws SyntaxException {

    }

    @Override
    public void sinal() throws SyntaxException {
        token = getNext();
        if(!token.getValue().equals("+") && !token.getValue().equals("-")) {
            throw new SyntaxException(token.getValue() + " não é um sinal", token.getLine());
        }
    }

    @Override
    public void op_relacional() throws SyntaxException {
        token = getNext();
        if(!token.getValue().equals("=") && !token.getValue().equals("<") && !token.getValue().equals(">")
                && !token.getValue().equals("<=") && !token.getValue().equals(">=") && !token.getValue().equals("<>")) {
            throw new SyntaxException(token.getValue() + " não é um operador relacional", token.getLine());
        }
    }

    @Override
    public void op_aditivo() throws SyntaxException {
        token = getNext();
        if(!token.getValue().equals("+") && !token.getValue().equals("-") && !token.getValue().equals("or")) {
            throw new SyntaxException(token.getValue() + " não é um operador aditivo", token.getLine());
        }
    }

    @Override
    public void op_multiplicativo() throws SyntaxException {
        token = getNext();
        if(!token.getValue().equals("*") && !token.getValue().equals("/") && !token.getValue().equals("and")) {
            throw new SyntaxException(token.getValue() + " não é um operador multiplicativo", token.getLine());
        }
    }
}
