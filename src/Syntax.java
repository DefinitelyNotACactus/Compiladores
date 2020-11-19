import util.SyntaxException;
import util.EmptyCommandException;

import java.util.List;

/** @noinspection ALL*/
public class Syntax implements Grammar {
    private Token token;
    private List<Token> tokenTable;
    private SymbolTable symbolTable;
    private int currentIndex;

    public Syntax(List<Token> tokenTable) {
        this.tokenTable = tokenTable;
        this.symbolTable = new SymbolTable();

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
            symbolTable.startNewScope();
            if(token.getType() == Type.IDENTIFICADOR) {
            	symbolTable.addSymbol(token);
                token = getNext();
                if(token.getValue().equals(";")) {
                    declaracoes_variaveis();
                    declaracoes_de_subprogramas();
                    comando_composto();
                    token = getNext();
                    if(!token.getValue().equals(".")) {
                        throw new SyntaxException("Esperado '.' encontrado: '" + token.getValue() + "'", token.getLine());
                    }
                } else {
                    throw new SyntaxException("Identificador não acompanhado por ';', encontrado no lugar: '" + token.getValue() + "'", token.getLine());
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
        token = getNext();
        if(token.getValue().equals("var")) {
            lista_declaracoes_variaveis();
        } else { // Não ler um var significa ler 'vazio'
            token = getPrevious();
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
                throw new SyntaxException("Esperado ';' após o tipo, encontrado: '" + token.getValue() + "'", token.getLine());
            }
            lista_declaracoes_variaveis2();
        } else {
            throw new SyntaxException("Esperado ':' após a lista de identificadores, encontrado: '" + token.getValue() + "'", token.getLine());
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
                    throw new SyntaxException("Esperado ';' após o tipo, encontrado: '" + token.getValue() + "'", token.getLine());
                }
                lista_declaracoes_variaveis2();
            } else {
                throw new SyntaxException("Esperado ':' após a lista de identificadores, encontrado: '" + token.getValue() + "'", token.getLine());
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
            throw new SyntaxException("Esperado um identificador, encontrado: '" + token.getValue() + "'", token.getLine());
        }
        symbolTable.addSymbol(token);
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
                throw new SyntaxException("Esperado ',' após o identificador, encontrado: '" + token.getValue() + "'", token.getLine());
            }
            symbolTable.addSymbol(token);
            lista_de_identificadores2();
        } else { // Lido um 'vazio', volta na leitura para deixar a leitura do símbolo para outra chamada
            token = getPrevious();
        }
    }

    @Override
    public void tipo() throws SyntaxException {
        token = getNext();
        if(!token.getValue().equals("integer") && !token.getValue().equals("real") && !token.getValue().equals("boolean")) {
            throw new SyntaxException("Esperado um tipo, encontrado: '" + token.getValue() + "'", token.getLine());
        }
    }

    /** Não é necessário criar novo método para eliminar recursão pela esquerda, pois a segunda opção da regra é o vazio 
     * declarações_de_subprogramas → vazio declarações_de_subprogramas2
     * declarações_de_subprogramas2 → declaração_de_subprograma ; declarações_de_subprogramas2 | vazio
     */
    @Override
    public void declaracoes_de_subprogramas() throws SyntaxException {
        token = getNext();
        if(token.getValue().equals("procedure")) { // ausência de "procedure" equivale a vazio
        	declaracao_de_subprograma();
        	token = getNext();
        	if (!token.getValue().equals(";")) {
                throw new SyntaxException("Esperado ';' após o subprograma, encontrado: '" + token.getValue() + "'", token.getLine());
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
            	symbolTable.addSymbol(token);
            	symbolTable.startNewScope();
                argumentos();
                token = getNext();
                if(token.getValue().equals(";")) {
                    declaracoes_variaveis();
                    declaracoes_de_subprogramas();
                    comando_composto();
                    symbolTable.removeScope();
                } else {
                    throw new SyntaxException("';' Faltando ao fim", token.getLine());
                }
            } else {
                throw new SyntaxException("Esperado um identificador, encontrado: '" + token.getValue() + "'", token.getLine());
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
                throw new SyntaxException("Esperado um ')' ao fim dos argumentos, encontrado: '" + token.getValue() + "'", token.getLine());
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
            lista_de_parametros2();
        } else {
            throw new SyntaxException("Esperado um ':' ao fim da lista de identificadores, encontrado: '" + token.getValue() + "'", token.getLine());
        }
    }

    /** lista_de_parametros2
     * lista_de_parametros2 -> ;id lista_de_identificadores2:tipo lista_de_parametros2 | vazio
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
                    lista_de_parametros2();
                } else {
                    throw new SyntaxException("Esperado um ':' ao fim da lista de identificadores, encontrado: '" + token.getValue()  + "'", token.getLine());
                }
            } else {
                throw new SyntaxException("Esperado um identificador, encontrado: '" + token.getValue() + "'", token.getLine());
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
                throw new SyntaxException("Esperado 'end' ao fim do comando composto, encontrado: '" + token.getValue() + "'", token.getLine());
            }
        } else {
            throw new SyntaxException("Esperado 'begin' ao início do comando composto, encontrado: '" + token.getValue() + "'", token.getLine());
        }
    }

    @Override
    public void comandos_opcionais() throws SyntaxException {
    	try {
    		lista_de_comandos();
    	} catch(EmptyCommandException ex) {
    		token = getPrevious();
    	}
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
    	token = getNext();
    	if(variavel()) {
    		token = getNext();
    		if(token.getValue().equals(":=")) {
    			expressao();
    		} else {
                throw new SyntaxException("Esperado ':=' após a variável, encontrado: '" + token.getValue() + "'", token.getLine());
            }
    	} else if(token.getType() == Type.IDENTIFICADOR) {
    		token = getPrevious();
    		ativacao_de_procedimento();
    	} else if(token.getValue().equals("begin")) {
    		token = getPrevious();
    		comando_composto();
    	} else if(token.getValue().equals("if")) {
    		expressao();
    		token = getNext();
    		if(!token.getValue().equals("then")) {
    			throw new SyntaxException("Esperado 'then' após a expressão, encontrado: '" + token.getValue() + "'", token.getLine());
    		}
    		comando();
			parte_else();
    	} else if(token.getValue().equals("while")) {
    		expressao();
    		token = getNext();
    		if(!token.getValue().equals("do")) {
    			throw new SyntaxException("Esperado 'do' após a expressão do 'while', encontrado: '" + token.getValue() + "'", token.getLine());
    		}
    		comando();
    	} else {
    		throw new EmptyCommandException("O Comando se encontra vazio", token.getLine());
    	}
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
    public boolean variavel() throws SyntaxException {
        return token.getType() == Type.IDENTIFICADOR;
    }

    @Override
    public void ativacao_de_procedimento() throws SyntaxException {
        token = getNext();
        if(token.getType() == Type.IDENTIFICADOR) {
        	symbolTable.checkSymbolOnTable(token);
            token = getNext();
            if(token.getValue().equals("(")) {
                lista_de_expressoes();
                token = getNext();
                if(!token.getValue().equals(")")) {
                    throw new SyntaxException("Esperado ')' ao fim da ativação de procedimento, encontrado: '" + token.getValue() + "'", token.getLine());
                }
            } else {
                token = getPrevious();
            }
        } else {
            throw new SyntaxException("Esperado um identificador na ativação de procedimento, encontrado: '" + token.getValue() + "'", token.getLine());
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
            	symbolTable.checkSymbolOnTable(token);
                token = getNext();
                if(token.getValue().equals("(")) {
                    lista_de_expressoes();
                    token = getNext();
                    if(!token.getValue().equals(")")) {
                        throw new SyntaxException("'(' não acompanhado por ')', encontrado: '" + token.getValue() + "'", token.getLine());
                    }
                } else {
                    token = getPrevious(); // Voltar atrás na leitura pois não lemos um '('
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
                            throw new SyntaxException("'(' não acompanhado por ')', encontrado: '" + token.getValue() + "'", token.getLine());
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
        switch (token.getValue()) {
            case "+":
            case "-":
                // Faz nada
                break;
            default: // Foi lido alguma coisa que não seja um sinal
                throw new SyntaxException("Esperado um sinal ('+', '-'), encontrado: '" + token.getValue() + "'", token.getLine());
        }
    }

    @Override
    public void op_relacional() throws SyntaxException {
        token = getNext();
        switch (token.getValue()) {
            case "=":
            case "<":
            case ">":
            case "<=":
            case ">=":
            case "<>":
                // Faz nada
                break;
            default: // Foi lido alguma coisa que não seja um op_relacional
                throw new SyntaxException("Esperado um operador relacional ('=', '<', '>', '<=', '>=', '<>'), encontrado: '" + token.getValue() + "'", token.getLine());
        }
    }

    @Override
    public void op_aditivo() throws SyntaxException {
        token = getNext();
        switch (token.getValue()) {
            case "+":
            case "-":
            case "or":
                // Faz nada
                break;
            default: // Foi lido alguma coisa que não seja um op_aditivo
                throw new SyntaxException("Esperado um operador aditivo ('+', '-', 'or'), encontrado: '" + token.getValue() + "'", token.getLine());
        }
    }

    @Override
    public void op_multiplicativo() throws SyntaxException {
        token = getNext();
        switch (token.getValue()) {
            case "*":
            case "/":
            case "and":
                // Faz nada
                break;
            default: // Foi lido alguma coisa que não seja um op_multiplicativo
                throw new SyntaxException("Esperado um operador multiplicativo ('*', '/', 'and'), encontrado: '" + token.getValue() +"'", token.getLine());
        }
    }
}
