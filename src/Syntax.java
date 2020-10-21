import util.SyntaxException;

import java.util.List;

public class Syntax implements Grammar {
    private Token token;
    private List<Token> tokenTable;
    private int currentIndex;

    public Syntax(List<Token> tokenTable) throws SyntaxException {
        this.tokenTable = tokenTable;

        currentIndex = -1;
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
                    subprogramDeclaration();
                    compositeCommand();
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
            token = getNext();
            varDeclarationList();
        } else {
            throw new SyntaxException("Declarações de variáveis não inicia com 'var'", token.getLine());
        }
    }

    @Override
    public void varDeclarationList() throws SyntaxException {

    }

    @Override
    public void idList() throws SyntaxException {

    }

    @Override
    public void type() throws SyntaxException {

    }

    public void subprogramDeclaration() {
        // TODO
    }

    @Override
    public void args() throws SyntaxException {

    }

    @Override
    public void paramList() throws SyntaxException {

    }

    @Override
    public void compositeCommand() {
        // TODO
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

    private Token getNext() throws SyntaxException {
        currentIndex++;
        return tokenTable.get(currentIndex);
    }
}
