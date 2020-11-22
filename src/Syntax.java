import util.SemanticException;
import util.SyntaxException;
import util.EmptyCommandException;

import java.util.LinkedList;
import java.util.List;

/** @noinspection ALL*/
public class Syntax implements Grammar {
    private Token token;
    private List<Token> tokenTable;
    private List<Token> identifierTable = null;
    private List<Type> pct;
    private SymbolTable symbolTable;
    private int currentIndex;
    private int counter;

    public Syntax(List<Token> tokenTable) {
        this.tokenTable = tokenTable;

        pct = new LinkedList<>();
        symbolTable = new SymbolTable();
        currentIndex = -1;
        counter = 0;
    }

    private void insertIdentifier(Token identifier) {
        if(identifierTable == null) {
            identifierTable = new LinkedList<>();
        }
        identifierTable.add(identifier);
    }

    private void markIdentifiers(Type type) {
        if(identifierTable != null) {
            for (Token identifier : identifierTable) { // Marca cada identificador como o tipo passado como parâmetro
                identifier.setType(type);
            }
            identifierTable = null; // "Apaga" a tabela
        }
    }

    private Token getNext() {
        currentIndex++;
        return tokenTable.get(currentIndex);
    }

    private Token getPrevious() {
        currentIndex--;
        return tokenTable.get(currentIndex);
    }

    private Type topPct() {
        return pct.get(pct.size() - 1);
    }

    private Type subtopPct() {
        return pct.get(pct.size() - 2);
    }

    private void updatePct(Type result) {
        pct.remove(pct.size() - 1);
        pct.remove(pct.size() - 1);
        pct.add(result);
    }

    private void relationalOperationResult() throws SemanticException {
        switch (topPct()) {
            case REAL:
            case INTEIRO:
                switch (subtopPct()) {
                    case REAL:
                    case INTEIRO: // <Num> <op_rel> <Num> tem como resultado um booleano
                        updatePct(Type.BOOLEANO);
                        break;
                    default:
                        throw new SemanticException("Incompabilidade de tipos, operação relacional envolvendo " + topPct().name + " e " + subtopPct().name,  token.getLine());
                }
            break;
            default:
                throw new SemanticException("Incompabilidade de tipos, operação relacional envolvendo " + topPct().name + " e " + subtopPct().name,  token.getLine());
        }
    }

    private void operationResult(Token operationToken) throws SemanticException {
        switch (topPct()) {
            case INTEIRO:
                switch (subtopPct()) {
                    case INTEIRO:
                        updatePct(Type.INTEIRO);
                        break;
                    case REAL:
                        updatePct(Type.REAL);
                        break;
                    default:
                        throw new SemanticException("Incompatibilidade de tipos, operação envolvendo " + subtopPct().name + " e " + topPct().name, token.getLine());
                }
                break;
            case REAL:
                switch (subtopPct()) {
                    case INTEIRO:
                    case REAL:
                        updatePct(Type.REAL);
                        break;
                    default:
                        throw new SemanticException("Incompatibilidade de tipos, operação envolvendo " + subtopPct().name + " e " + topPct().name, token.getLine());
                }
                break;
            case BOOLEANO:
                switch (subtopPct()) {
                    case BOOLEANO:
                        if(operationToken.getValue().equals("and") || operationToken.getValue().equals("or")) { // Dois booleanos só podem ser envolvidos em operações do tipo and, or.
                            updatePct(Type.BOOLEANO);
                        } else {
                            throw new SemanticException("Incompatibilidade de tipos, operação '" + operationToken.getValue() + "' envolvendo dois operadores booleanos", token.getLine());
                        }
                        break;
                    default:
                        throw new SemanticException("Incompatibilidade de tipos, operação envolvendo " + subtopPct().name + " e " + topPct().name, token.getLine());
                }
                break;
            default:
                throw new SemanticException("Incompatibilidade de tipos, operação envolvendo " + subtopPct().name + " e " + topPct().name, token.getLine());
        }
    }

    private void attributionResult(Type type) throws SemanticException {
        switch (type) {
            case INTEIRO:
                switch (topPct()) {
                    case INTEIRO: //Semanticamente correto.
                        pct.clear();
                        break;
                    default:
                        throw new SemanticException("Incompatibilidade de tipos, a expressão tem como resultado: " + topPct().name + ", e era esperado: Número Inteiro", token.getLine());
                }
                break;
            case REAL:
                switch (topPct()) {
                    case REAL:
                    case INTEIRO:// Faz nada, semanticamente correto.
                        pct.clear();
                        break;
                    default:
                        throw new SemanticException("Incompatibilidade de tipos, a expressão tem como resultado: " + topPct().name + ", e era esperado: Número Real ou Inteiro", token.getLine());
                }
                break;
            case BOOLEANO:
                switch (topPct()) {
                    case BOOLEANO: // Faz nada, semanticamente correto.
                        pct.clear();
                        break;
                    default:
                        throw new SemanticException("Incompatibilidade de tipos, a expressão tem como resultado: " + topPct().name + ", e era esperado: Booleano", token.getLine());
                }
                break;
            default:
                throw new SemanticException("Incompatibilidade de tipos, tipo da expressão: " + topPct().name, token.getLine());
        }
    }

    private void tableAction(Token token) throws SemanticException {
        if(counter == 0) {
            symbolTable.addSymbol(token);
        } else {
            symbolTable.checkSymbolOnTable(token);
        }
    }

    @Override
    public void programa() throws SyntaxException, SemanticException {
        token = getNext();
        if(token.getValue().equals("program")) {
            token = getNext();
            symbolTable.startNewScope();
            if(token.getType() == Type.IDENTIFICADOR) {
            	tableAction(token);
            	token.setType(Type.PROGRAM); // Atualizar o identificador como PROGRAM
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
    public void declaracoes_variaveis() throws SyntaxException, SemanticException {
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
    public void lista_declaracoes_variaveis() throws SyntaxException, SemanticException {
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
    private void lista_declaracoes_variaveis2() throws SyntaxException, SemanticException {
        token = getNext();
        if(token.getType() == Type.IDENTIFICADOR) { // Caso contratrário foi lido o "vazio"
            tableAction(token);
            insertIdentifier(token);
            lista_de_identificadores2();
            token = getNext();
            if (token.getValue().equals(":")) {
                tipo();
                token = getNext();
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
    public void lista_de_identificadores() throws SyntaxException, SemanticException {
        token = getNext();
        if(token.getType() != Type.IDENTIFICADOR) {
            throw new SyntaxException("Esperado um identificador, encontrado: '" + token.getValue() + "'", token.getLine());
        }
        tableAction(token);
        insertIdentifier(token); // Adicionamos o identificador em uma lista temporária para marcarmos o tipo posteriormente
        lista_de_identificadores2();
    }

    /** Remover recursão a esquerda de lista_de_identificadores (lista_de_identificadores2)
     * lista_de_identificadores2 -> ,id lista_de_identificadores2 | vazio
     * @throws SyntaxException Erro sintático
     */
    private void lista_de_identificadores2() throws SyntaxException, SemanticException {
        token = getNext();
        if(token.getValue().equals(",")) { // Ausência de ',' equivale a vazio
            token = getNext();
            if(token.getType() != Type.IDENTIFICADOR) {
                throw new SyntaxException("Esperado ',' após o identificador, encontrado: '" + token.getValue() + "'", token.getLine());
            }
            tableAction(token);
            insertIdentifier(token);
            lista_de_identificadores2();
        } else { // Lido um 'vazio', volta na leitura para deixar a leitura do símbolo para outra chamada
            token = getPrevious();
        }
    }

    @Override
    public void tipo() throws SyntaxException {
        token = getNext();
        switch (token.getValue()) {
            case "integer":
                markIdentifiers(Type.INTEIRO);
                break;
            case "real":
                markIdentifiers(Type.REAL);
                break;
            case "boolean":
                markIdentifiers(Type.BOOLEANO);
                break;
            default: // Não foi lido um tipo válido
                throw new SyntaxException("Esperado um tipo, encontrado: '" + token.getValue() + "'", token.getLine());
        }
    }

    /** Não é necessário criar novo método para eliminar recursão pela esquerda, pois a segunda opção da regra é o vazio 
     * declarações_de_subprogramas → vazio declarações_de_subprogramas2
     * declarações_de_subprogramas2 → declaração_de_subprograma ; declarações_de_subprogramas2 | vazio
     */
    @Override
    public void declaracoes_de_subprogramas() throws SyntaxException, SemanticException {
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
    public void declaracao_de_subprograma() throws SyntaxException, SemanticException {
        if(token.getValue().equals("procedure")) {
            token = getNext();
            if(token.getType() == Type.IDENTIFICADOR) {
            	tableAction(token);
                token.setType(Type.PROCEDURE);
            	symbolTable.startNewScope();
                argumentos();
                token = getNext();
                if(token.getValue().equals(";")) {
                    declaracoes_variaveis();
                    declaracoes_de_subprogramas();
                    comando_composto();
                } else {
                    throw new SyntaxException("';' Faltando ao fim", token.getLine());
                }
            } else {
                throw new SyntaxException("Esperado um identificador, encontrado: '" + token.getValue() + "'", token.getLine());
            }
        }
    }

    @Override
    public void argumentos() throws SyntaxException, SemanticException {
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
    public void lista_de_parametros() throws SyntaxException, SemanticException {
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
    private void lista_de_parametros2() throws SyntaxException, SemanticException {
        token = getNext();
        if(token.getValue().equals(";")) {
            token = getNext();
            if (token.getType() == Type.IDENTIFICADOR) {
                tableAction(token);
                insertIdentifier(token);
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
    public void comando_composto() throws SyntaxException, SemanticException {
        token = getNext();
        if(token.getValue().equals("begin")) {
            counter++; // Lido um begin
            comandos_opcionais();
            token = getNext();
            if(!token.getValue().equals("end")) {
                throw new SyntaxException("Esperado 'end' ao fim do comando composto, encontrado: '" + token.getValue() + "'", token.getLine());
            }
            counter--; // Lido um end
            if(counter == 0) {
                symbolTable.removeScope();
            }
        } else {
            throw new SyntaxException("Esperado 'begin' ao início do comando composto, encontrado: '" + token.getValue() + "'", token.getLine());
        }
    }

    @Override
    public void comandos_opcionais() throws SyntaxException, SemanticException {
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
    public void lista_de_comandos() throws SyntaxException, SemanticException {
        comando();
        lista_de_comandos2();
    }

    /** lista_de_comandos2
     * Adicionado para remover a recursão à esquerda de lista_de_comandos
     * lista_de_comandos2 -> ; comando lista_de_comandos2 | VAZIO
     * @throws SyntaxException Erro sintático
     */
    private void lista_de_comandos2() throws SyntaxException, SemanticException {
        token = getNext();
        if(token.getValue().equals(";")) {
            comando();
            lista_de_comandos2();
        } else { // Símbolo vazio
            token = getPrevious();
        }
    }

    @Override
    public void comando() throws SyntaxException, SemanticException {
    	token = getNext();
    	if(variavel()) {
    	    tableAction(token);
            Type idType = token.getType();
    		token = getNext();
    		if(token.getValue().equals(":=")) {
    			expressao();
                attributionResult(idType);
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
    public void parte_else() throws SyntaxException, SemanticException {
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
    public void ativacao_de_procedimento() throws SyntaxException, SemanticException {
        token = getNext();
        if(token.getType() == Type.IDENTIFICADOR) {
        	tableAction(token);
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
    public void lista_de_expressoes() throws SyntaxException, SemanticException {
        expressao();
        lista_de_expressoes2();
    }

    /** lista_de_expressoes2
     * Adicionado para remover a recursão à esquerda em lista_de_expressoes
     * lista_de_expressoes2 -> , expressao lista_de_expressoes2 | VAZIO
     * @throws SyntaxException Erro sintático
     */
    private void lista_de_expressoes2() throws SyntaxException, SemanticException {
        token = getNext();
        if(token.getValue().equals(",")) {
            expressao();
            lista_de_expressoes2();
        } else { // Símbolo vazio
            token = getPrevious();
        }
    }

    @Override
    public void expressao() throws SyntaxException, SemanticException {
        expressao_simples();
        try { // Vejamos se é um op relacional
            op_relacional();
        } catch (SyntaxException ex) { // Caso entre aqui não é um op_relacional
            token = getPrevious(); // Voltar na leitura, analisamos como expressão -> expressão_simples
            return;
        }
        expressao_simples(); // Caso passe pelo try, analisamos como expressão -> expressão_simpes op_relacional expressão_simples
        relationalOperationResult();
    }

    /** expressao_simples
     * Reescrito como
     * expressao_simples -> termo expressao_simples2 | sinal termo expressao_simples2
     * @throws SyntaxException Erro sintático
     */
    @Override
    public void expressao_simples() throws SyntaxException, SemanticException {
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
     * expressao_simples2 -> op_aditivo termo expressao_simples2 | VAZIO
     * @throws SyntaxException Erro sintático
     */
    private void expressao_simples2() throws SyntaxException, SemanticException {
        try { // Assumimos que é um op_aditivo o próximo símbolo
            op_aditivo();
        } catch(SyntaxException ex) { // Caso entre aqui, o símbolo não é um op_aditivo, logo consideramos como vazio
            token = getPrevious();
            return;
        }
        Token operationToken = token;
        termo();
        operationResult(operationToken);
        expressao_simples2();
    }

    /** termo
     * Reescrito como
     * termo -> fator termo2
     * @throws SyntaxException Erro sintático
     */
    @Override
    public void termo() throws SyntaxException, SemanticException {
        fator();
        termo2();
    }

    /** termo2
     * Adicionado para remover a recursão à esquerda em expressão simples
     * termo2 -> op_multiplicativo fator termo2| VAZIO
     * @throws SyntaxException Erro sintático
     */
    private void termo2() throws SyntaxException, SemanticException {
        try {
            op_multiplicativo();
        } catch (SyntaxException ex) { // Op_multiplicativo lançou uma exceção, consideramos como símbolo vazio
            token = getPrevious();
            return;
        }
        Token operationToken = token;
        fator();
        operationResult(operationToken);
        termo2();
    }

    @Override
    public void fator() throws SyntaxException, SemanticException {
        token = getNext();
        switch (token.getType()) {
            case IDENTIFICADOR:
            	tableAction(token);
            	pct.add(token.getType());
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
                pct.add(Type.INTEIRO);
                break;
            case REAL:
                pct.add(Type.REAL);
                break;
            case BOOLEANO:
                pct.add(Type.BOOLEANO);
                break;
            default:
                switch(token.getValue()) {
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
