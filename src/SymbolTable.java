import util.SemanticException;

import java.util.LinkedList;

public class SymbolTable {
	private LinkedList<Token> stack;
	
	public SymbolTable() {
		this.stack = new LinkedList<>();
	}
	
	public void startNewScope() {
		this.stack.add(new Token("$", Type.MARK, 0));
	}
	
	private boolean symbolOnScope(Token token) {
        int index = stack.size() - 1;
        Token currentToken = stack.get(index);
        while(currentToken.getType() != Type.MARK) {
        	if(currentToken.getValue().equals(token.getValue())) {
        		return true;
        	}
        	
        	index--;
        	currentToken = stack.get(index);
        }
        
        return false;
    }
    
    public void addSymbol(Token token) throws SemanticException {
        if(symbolOnScope(token)) {
			throw new SemanticException("O identificador " + token.getValue() + " já está declarado neste escopo.", token.getLine());
		}
        stack.addLast(token);  
    }
    
    public void checkSymbolOnTable(Token token) throws SemanticException {
        Token currentToken;
        for(int index = stack.size() - 1; index >= 0; index--) {
        	currentToken = stack.get(index);
        	if(currentToken.getValue().equals(token.getValue())) {
				return;
			}
        }
        throw new SemanticException("O identificador " + token.getValue() + " não está declarado neste escopo.", token.getLine());
    }
    
    public void removeScope(){
    	Token currentToken;
    	for(int index = stack.size() - 1; index >= 0; index--) {
        	currentToken = stack.removeLast();
        	if(currentToken.getType() == Type.IDENTIFICADOR) {
				break;
			}
        }
    }

}
