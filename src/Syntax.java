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
     * lista_declarações_variáveis -> lista_de_identificadores: tipo; lista_declarações_variáveis2
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
    private void lista_declaracoes_variaveis2() throws SyntaxException {
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
    private void lista_de_identificadores2() throws SyntaxException {
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

    /** Não é necessário criar novo método para eliminar recursão pela esquerda, pois a segunda opção da regra é o vazio 
     * declarações_de_subprogramas → vazio | declarações_de_subprogramas2
     * declarações_de_subprogramas2 → declaração_de_subprograma ; declarações_de_subprogramas2 | vazio
     */
    @Override
    public void declaracoes_de_subprogramas() throws SyntaxException {
        token = getNext();
        if(token.getValue().equals("procedure")) { // ausência de "procedure" equivale a vazio
        	declaracao_de_subprograma();
        	token = getNext();
        	if (!token.getValue().equals(";")) {
                throw new SyntaxException("';' não acompanha o subprograma", token.getLine());
            }
            declaracoes_de_subprogramas();
        } else { // Lido um 'vazio', volta na leitura para deixar a leitura do símbolo para outra chamada
            token = getPrevious();
        }
    }

    @Override
    public void declaracao_de_subprograma() throws SyntaxException {
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
        if(token.getValue().equals("(")) { // A ausência de '(' equivale ao vazio
        	lista_de_parametros();
            token = getNext();
            if(!token.getValue().equals(")")) {
                throw new SyntaxException("Argumentos com ')' faltando (Lido : '" + token.getValue() + "')", token.getLine());
            }
        } else {
        	token = getPrevious();
        }    
    }

    /** lista_de_parametros
     * Reescrito como
     * lista_de_parametros -> lista_de_identificadores: tipo lista_de_parametros2
     * @throws SyntaxException Erro sintático
     */
    @Override
    public void lista_de_parametros() throws SyntaxException {
        lista_de_identificadores();
        token = getNext();
        if(token.getValue().equals(":")) {
            tipo();
            //token = getNext();
            //if(token.getValue().equals(";")) {
                lista_de_parametros2();
            //} else {
                //throw new SyntaxException("';' faltando após o tipo", token.getLine());
            //}
        } else {
            throw new SyntaxException("':' faltando após a lista de identificadores", token.getLine());
        }
    }

    /** lista_de_parametros2
     * lista_de_parametros2 -> ;id lista_de_identificadores2:tipo | vazio
     * @throws SyntaxException Erro sintático
     */
    private void lista_de_parametros2() throws SyntaxException {
        token = getNext();
        if(token.getValue().equals(";")) {
            token = getNext();
            if (token.getType() == Type.IDENTIFICADOR) {
                lista_de_identificadores2();
                token = getNext();
                if (token.getValue().equals(":")) {
                    tipo();
                } else {
                    throw new SyntaxException("A lista de identificadores não é seguida por ':'", token.getLine());
                }
            } else {
                throw new SyntaxException("O símbolo lido '" + token.getValue() + "' não é um identificador", token.getLine());
            }
        } else { // A ausência de ; significa entrada vazia
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

    /** lista_de_comandos
     * Reescrito como
     * lista_de_comandos -> comando lista_de_comandos2
     * @throws SyntaxException Erro sintático
     */
    @Override
    public void lista_de_comandos() throws SyntaxException {
        comando();
        lista_de_comandos2();
    }

    /** lista_de_comandos2
     * Adicionado para remover a recursão à esquerda de lista_de_comandos
     * lista_de_comandos2 -> ; comando lista_de_comandos2 | VAZIO
     * @throws SyntaxException Erro sintático
     */
    private void lista_de_comandos2() throws SyntaxException {
        token = getNext();
        if(token.getValue().equals(";")) {
            comando();
            lista_de_comandos2();
        } else { // Símbolo vazio
            token = getPrevious();
        }
    }

    @Override
    public void comando() throws SyntaxException {

    }

    @Override
    public void parte_else() throws SyntaxException {
        token = getNext();
        if(token.getValue().equals("else")) {
            comando();
        } else { // Comando vazio
            token = getPrevious();
        }
    }

    @Override
    public void variavel() throws SyntaxException {
        token = getNext();
        if(token.getType() != Type.IDENTIFICADOR) {
            throw new SyntaxException(token.getValue() + " não é um identificador para variável", token.getLine());
        }
    }

    @Override
    public void ativacao_de_procedimento() throws SyntaxException {
        token = getNext();
        if(token.getType() == Type.IDENTIFICADOR) {
            token = getNext();
            if(token.getValue().equals("(")) {
                lista_de_expressoes();
                token = getNext();
                if(!token.getValue().equals(")")) {
                    throw new SyntaxException("Ausência de ')' no fim da ativação de procedimento", token.getLine());
                }
            } else {
                token = getPrevious();
            }
        } else {
            throw new SyntaxException("Identificador não encontrado na ativação de procedimento", token.getLine());
        }
    }

    /** lista_de_expressoes
     * Reescrito como
     * lista_de_expressoes -> expressao lista_de_expressoes2
     * @throws SyntaxException Erro sintático
     */
    @Override
    public void lista_de_expressoes() throws SyntaxException {
        expressao();
        lista_de_expressoes2();
    }

    /** lista_de_expressoes2
     * Adicionado para remover a recursão à esquerda em lista_de_expressoes
     * lista_de_expressoes2 -> , expressao lista_de_expressoes2 | VAZIO
     * @throws SyntaxException Erro sintático
     */
    private void lista_de_expressoes2() throws SyntaxException {
        token = getNext();
        if(token.getValue().equals(",")) {
            expressao();
            lista_de_expressoes2();
        } else { // Símbolo vazio
            token = getPrevious();
        }
    }

    @Override
    public void expressao() throws SyntaxException {
        expressao_simples();
        try { // Vejamos se é um op relacional
            op_relacional();
        } catch (SyntaxException ex) { // Caso entre aqui não é um op_relacional
            token = getPrevious(); // Voltar na leitura, analisamos como expressão -> expressão_simples
            return;
        }
        expressao_simples(); // Caso passe pelo try, analisamos como expressão -> expressão_simpes op_relacional expressão_simples
    }

    /** expressao_simples
     * Reescrito como
     * expressao_simples -> termo expressao_simples2 | sinal termo expressao_simples2
     * @throws SyntaxException Erro sintático
     */
    @Override
    public void expressao_simples() throws SyntaxException {
        try { // Assumimos inicialmente que vai ser lido um sinal
            sinal();
        } catch (SyntaxException ex) { // Caso entre aqui não é um sinal
            token = getPrevious(); // Analisamos como expressao_simples -> termo expressao_simples2
        }
        termo();
        expressao_simples2();
    }

    /** expressao_simples2
     * Adicionado para remover a recursão à esquerda em expressao_simples
     * expressao_simples2 -> op_aditivo termo expressao_simples2| VAZIO
     * @throws SyntaxException Erro sintático
     */
    private void expressao_simples2() throws SyntaxException {
        try { // Assumimos que é um op_aditivo o próximo símbolo
            op_aditivo();
        } catch(SyntaxException ex) { // Caso entre aqui, o símbolo não é um op_aditivo, logo consideramos como vazio
            token = getPrevious();
            return;
        }
        termo();
        expressao_simples2();
    }

    /** termo
     * Reescrito como
     * termo -> fator termo2
     * @throws SyntaxException Erro sintático
     */
    @Override
    public void termo() throws SyntaxException {
        fator();
        termo2();
    }

    /** termo2
     * Adicionado para remover a recursão à esquerda em expressão simples
     * termo2 -> op_multiplicativo fator termo2| VAZIO
     * @throws SyntaxException Erro sintático
     */
    private void termo2() throws SyntaxException {
        try {
            op_multiplicativo();
        } catch (SyntaxException ex) { // Op_multiplicativo lançou uma exceção, consideramos como símbolo vazio
            token = getPrevious();
            return;
        }
        fator();
        termo2();
    }

    @Override
    public void fator() throws SyntaxException {
        token = getNext();
        switch (token.getType()) {
            case IDENTIFICADOR:
                token = getNext();
                if(token.getValue().equals("(")) {
                    lista_de_expressoes();
                    token = getNext();
                    if(!token.getValue().equals(")")) {
                        throw new SyntaxException("'(' não acompanhado por ')'", token.getLine());
                    }
                }
                break;
            case INTEIRO:
            case REAL:
                // Faz nada
                break;
            default:
                switch(token.getValue()) {
                    case "true":
                    case "false":
                        // Faz nada
                        break;
                    case "(":
                        expressao();
                        token = getNext();
                        if(!token.getValue().equals(")")) {
                            throw new SyntaxException("'(' não acompanhado por ')'", token.getLine());
                        }
                        break;
                    case "not":
                        expressao();
                        break;
                    default:
                        throw new SyntaxException("Fator não reconhecido", token.getLine());
                }
                break;
        }
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
