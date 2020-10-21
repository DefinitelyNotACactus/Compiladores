import util.SyntaxException;

public interface Grammar {
    void program() throws SyntaxException;
    void varDeclaration() throws SyntaxException;
    void varDeclarationList() throws SyntaxException;
    void idList() throws SyntaxException;
    void type() throws SyntaxException;
    void subprogramDeclaration() throws SyntaxException;
    void args() throws SyntaxException;
    void paramList() throws SyntaxException;
    void compositeCommand() throws SyntaxException;
    void optionalCommand() throws SyntaxException;
    void commandList() throws SyntaxException;
    void command() throws SyntaxException;
    void elsePart() throws SyntaxException;
    void var() throws SyntaxException;
    void procedureActivation() throws SyntaxException;
    void expressionList() throws SyntaxException;
    void expression() throws SyntaxException;
    void simpleExpression() throws SyntaxException;
    void term() throws SyntaxException;
    void factor() throws SyntaxException;
    void signal() throws SyntaxException;
    void relationalOp() throws SyntaxException;
    void additiveOp() throws SyntaxException;
    void multiplicativeOp() throws SyntaxException;
}
