import util.SyntaxException;

import java.util.List;

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
    public void program() throws SyntaxException {
        token = getNext();
        if(token.getValue().equals("program")) {
            token = getNext();
            if(token.getType() == Type.IDENTIFICADOR) {
                token = getNext();
                if(token.getValue().equals(";")) {
                    token = getNext();
                    varDeclaration();
                    subprogramsDeclaration();
                    compositeCommand();
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
    public void varDeclaration() throws SyntaxException {
        if(token.getValue().equals("var")) {
            varDeclarationList();
        }
    }

    /** lista_declarações_variáveis
     * Reescrito como
     * lista_declarações_variáveis -> lista_de_idenficiadores: tipo; lista_declarações_variáveis2
     * @throws SyntaxException Erro sintático
     */
    @Override
    public void varDeclarationList() throws SyntaxException {
        idList();
        token = getNext();
        if(token.getValue().equals(":")) {
            type();
            if(!token.getValue().equals(";")) {
                throw new SyntaxException("';' não acompanha o tipo", token.getLine());
            }
            varDeclarationList2();
        } else {
            throw new SyntaxException("':' não acompanha a lista de identificadores", token.getLine());
        }
    }

    /** Remover recursão a esquerda de lista_declarações_variáveis
     * lista_declarações_variáveis2 -> id lista_de_identificadores2: tipo; lista_declarações_variáveis2 | vazio
     * @throws SyntaxException Erro sintático
     */
    @Override
    public void varDeclarationList2() throws SyntaxException {
        token = getNext();
        if(token.getType() == Type.IDENTIFICADOR) { // Caso contratrário foi lido o "vazio"
            idList2();
            token = getNext();
            if (token.getValue().equals(":")) {
                type();
                if (!token.getValue().equals(";")) {
                    throw new SyntaxException("';' não acompanha o tipo", token.getLine());
                }
                varDeclarationList2();
            } else {
                throw new SyntaxException("':' não acompanha a lista de identificadores", token.getLine());
            }
        } else { // O símbolo lido não ser identificador equivale ao 'vazio', voltar atrás na leitura
            token = getPrevious();
        }
    }

    @Override
    public void idList() throws SyntaxException {
        token = getNext();
        if(token.getType() != Type.IDENTIFICADOR) {
            throw new SyntaxException("Identificador não localizado", token.getLine());
        }
        idList2();
    }

    /** Remover recursão a esquerda de lista_de_identificadores
     *
     * @throws SyntaxException
     */
    @Override
    public void idList2() throws SyntaxException {
        token = getNext();
        if(token.getValue().equals(",")) { // Ausência de ',' equivale a vazio
            token = getNext();
            if(token.getType() != Type.IDENTIFICADOR) {
                throw new SyntaxException("',' não acompanhado de identificador", token.getLine());
            }
            idList2();
        } else { // Lido um 'vazio', volta na leitura para deixar a leitura do símbolo para outra chamada
            token = getPrevious();
        }
    }

    @Override
    public void type() throws SyntaxException {
        token = getNext();
        if(!token.getValue().equals("integer") && !token.getValue().equals("real") && !token.getValue().equals("boolean")) {
            throw new SyntaxException(token.getValue() + " não é um tipo", token.getLine());
        }
    }

    @Override
    public void subprogramsDeclaration() throws SyntaxException {
        // TODO
    }

    @Override
    public void subprogramDeclaration() throws SyntaxException {
        token = getNext();
        if(token.getValue().equals("procedure")) {
            token = getNext();
            if(token.getType() == Type.IDENTIFICADOR) {
                args();
                token = getNext();
                if(token.getValue().equals(";")) {
                    varDeclaration();
                    subprogramsDeclaration();
                    compositeCommand();
                } else {
                    throw new SyntaxException("';' Faltando", token.getLine());
                }
            } else {
                throw new SyntaxException(token.getValue() + " não é um identificador", token.getLine());
            }
        }
    }

    @Override
    public void args() throws SyntaxException {

    }

    @Override
    public void paramList() throws SyntaxException {

    }

    @Override
    public void compositeCommand() throws SyntaxException {
        token = getNext();
        if(token.getValue().equals("begin")) {
            optionalCommand();
            token = getNext();
            if(!token.getValue().equals("end")) {
                throw new SyntaxException("Comando composto não encerrado com end", token.getLine());
            }
        } else {
            throw new SyntaxException("Comando composto não iniciado com begin", token.getLine());
        }
    }

    @Override
    public void optionalCommand() throws SyntaxException {

    }

    @Override
    public void commandList() throws SyntaxException {

    }

    @Override
    public void command() throws SyntaxException {

    }

    @Override
    public void elsePart() throws SyntaxException {

    }

    @Override
    public void var() throws SyntaxException {

    }

    @Override
    public void procedureActivation() throws SyntaxException {

    }

    @Override
    public void expressionList() throws SyntaxException {

    }

    @Override
    public void expression() throws SyntaxException {

    }

    @Override
    public void simpleExpression() throws SyntaxException {

    }

    @Override
    public void term() throws SyntaxException {

    }

    @Override
    public void factor() throws SyntaxException {

    }

    @Override
    public void signal() throws SyntaxException {

    }

    @Override
    public void relationalOp() throws SyntaxException {

    }

    @Override
    public void additiveOp() throws SyntaxException {

    }

    @Override
    public void multiplicativeOp() throws SyntaxException {

    }
}
