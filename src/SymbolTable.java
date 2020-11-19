import java.util.LinkedList;

public class SymbolTable {
	private LinkedList<Token> stack;
	
	public SymbolTable() {
		this.stack = new LinkedList<Token>();
	}
	
	public void startNewScope() {
		this.stack.add(new Token("$", Type.IDENTIFICADOR, 0));
	}
	
	private boolean symbolOnScope(Token token) {
        int index = stack.size() - 1;
        Token currentToken = stack.get(index);
        while(!currentToken.getValue().equals("$")) {
        	if(currentToken.getValue().equals(token.getValue())) {
        		return true;
        	}
        	
        	index--;
        	currentToken = stack.get(index);
        }
        
        return false;
    }
    
    public void addSymbol(Token token) {
        if(symbolOnScope(token)) {
            // EXCEPTION
        }
        
        stack.addLast(token);  
    }
    
    public void checkSymbolOnTable(Token token) {
        Token currentToken;
        for(int index = stack.size() - 1; index >= 0; index--) {
        	currentToken = stack.get(index);
        	if(currentToken.getValue().equals(token.getValue()))
        		return;
        }
        
        // EXCEPTION
    }
    
    public void removeScope(){
    	Token currentToken;
    	for(int index = stack.size() - 1; index >= 0; index--) {
        	currentToken = stack.removeLast();
        	if(currentToken.getValue().equals("$"))
        		break;
        }
    }

}
