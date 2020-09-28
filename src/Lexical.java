import util.InvalidSymbolException;
import util.LexicalException;
import util.UnfinishedCommentException;

import java.util.*;

public class Lexical {
    /** Conjunto de estados da máquina de estados do analisador léxico */
    enum Status {
        Q0(null),
        Q1(Type.IDENTIFICADOR),
        Q2(Type.INTEIRO),
        Q3(Type.REAL),
        Q4(Type.DELIMITADOR),
        Q5(Type.DELIMITADOR),
        Q6(Type.ATRIBUICAO),
        Q7(Type.OP_RELACIONAL),
        Q8(Type.OP_RELACIONAL),
        Q9(Type.OP_RELACIONAL),
        Q10(Type.OP_ADITIVO),
        Q11(Type.OP_MULTIPLICATIVO),
        Q12(null);

        public final Type statusType;

        Status(Type statusType) {
            this.statusType = statusType;
        }
    }

    private List<Token> table;
    private final Map<String, Type> keywords;
    private final Set<Character> delimiters;
    private Status status;
    
    private int currentLine;
    private int errorLine;

    public Lexical() {
        table = new ArrayList<>();
        keywords = createKeywordsMap();
        delimiters = new HashSet<>(Arrays.asList(';', '.', '(', ')', ','));
        status = Status.Q0;
        
        this.currentLine = 0;
        this.errorLine = 0;
    }

    /** Cria a lista de palavras reservadas (chave), onde o valor é o tipo associado a tal palavra */
    private Map<String, Type> createKeywordsMap() {
        Map<String, Type> keywords = new HashMap<>();

        keywords.put("program", Type.RESERVADO);
        keywords.put("var", Type.RESERVADO);
        keywords.put("integer", Type.RESERVADO);
        keywords.put("real", Type.RESERVADO);
        keywords.put("boolean", Type.RESERVADO);
        keywords.put("procedure", Type.RESERVADO);
        keywords.put("begin", Type.RESERVADO);
        keywords.put("end", Type.RESERVADO);
        keywords.put("if", Type.RESERVADO);
        keywords.put("then", Type.RESERVADO);
        keywords.put("else", Type.RESERVADO);
        keywords.put("while", Type.RESERVADO);
        keywords.put("do", Type.RESERVADO);
        keywords.put("not", Type.RESERVADO);
        keywords.put("for", Type.RESERVADO);
        keywords.put("to", Type.RESERVADO);
        keywords.put("and", Type.OP_MULTIPLICATIVO);
        keywords.put("or", Type.OP_ADITIVO);

        return keywords;
    }

    /** Método para obter o próximo estado da máquina
     *
     * @param current O estado atual da máquina
     * @param c O caracter a ser lido pela máquina
     * @return O próximo estado
     * @throws InvalidSymbolException Em caso da máquina ler um símbolo não pertencente ao alfabeto
     */
    private Status getNextStatus(Status current, Character c) throws InvalidSymbolException {
        switch(current) {
            case Q0: // Estado inicial
                switch (c) {
                    case ':': // c pode ser um delimitador ou atribuição
                        return Status.Q5;
                    case '=': // operador relacional
                        return Status.Q7;
                    case '<': // mesma coisa para < e > porém necessita-se ser checado se é acompanhado por um = ou um > no caso de <
                        return Status.Q8;
                    case '>':
                        return Status.Q9;
                    case '+': // Operadores aditivos
                    case '-':
                        return Status.Q10;
                    case '*': // Operadores multiplicativos
                    case '/':
                        return Status.Q11;
                    case '{': // Início de um comentário
                    	errorLine = currentLine;
                        return Status.Q12;
                    case ' ': // Caracteres para serem ignorados
                    case '\t':
                    case '\r':
                    case '\n':
                        return Status.Q0;
                    default: // Outros caracteres
                        if(delimiters.contains(c)) { // c é um delimitador
                            return Status.Q4;
                        } else if(Validator.isLetter(c)) { // c é uma letra
                            return Status.Q1;
                        } else if(Validator.isNumber(c)) { // c é um número
                            return Status.Q2;
                        }
                        throw new InvalidSymbolException(c); // Se chegar aqui, é pelo fato de c ser um símbolo não reconhecido
                }
            case Q1: // Nesse estado, se supõe que o token é um identificador
                if(Validator.isNumberOrCharacter(c) || c == '_') {
                    return Status.Q1;
                }
                return Status.Q0;
            case Q2:
                if(Validator.isNumber(c)) {
                    return Status.Q2; // Continua a ler o número
                } else if (c == '.') {
                    return Status.Q3; // O token passa a ser um número real
                }
                return Status.Q0;
            case Q3:
                if(Validator.isNumber(c)) { // Continua a ler o número real
                    return Status.Q3;
                }
                return Status.Q0;
            case Q4:
            case Q6:
            case Q7:
            case Q10:
            case Q11:
                return Status.Q0;
            case Q5:
                if(c == '=') { // O token passa a ser uma atribuição
                    return Status.Q6;
                }
                return Status.Q0;
            case Q8:
                if(c == '>' || c == '=') {
                    return Status.Q8;
                }
                return Status.Q0;
            case Q9:
                if(c == '=') {
                    return Status.Q9;
                }
                return Status.Q0;
            case Q12:
                if(c == '}') { // Fim do comentário
                    return Status.Q0;
                }
                return Status.Q12; // o comentário continua
        }
        throw new InvalidSymbolException(c);
    }

    /** Método para criar a tabela de símbolos (tokens)
     *
     * @param input O texto a ser processado
     * @throws LexicalException Em caso da análise léxica dar um erro (e.g. símbolo não pertencente ao alfabeto ou comentário não finalizado)
     */
    public void buildTokenTable(List<String> input) throws LexicalException {
        table.clear();
        Status previous;
        String token = "";
        // Laço de leitura
        for(String line : input) { // Para cada linha
            currentLine++;
            for(Character character : line.toCharArray()) { // Para cada caractere da linha
                // Obter o próximo e o estado atual
                previous = status;
                try {
                    status = getNextStatus(previous, character);

                    if (status == Status.Q0) { // O token foi lido
                        switch (previous) {
                            case Q0: // No caso anterior ser 0 ou 12 (comentário), apenas se limpa o token.
                            case Q12:
                                token = "";
                                break;
                            case Q1: // No caso de 1, significa que foi lido um idenficador
                                // Contudo, tal idenficador pode ser uma palavra reservada ou um operador (and, or), verifica-se se o token lido está na tabela de palavras chave
                                if (keywords.containsKey(token)) { // Em caso positivo, pegamos o tipo associado a tal palavra chave
                                    table.add(new Token(token, keywords.get(token), currentLine));
                                } else { // Caso contrário é um token
                                    table.add(new Token(token, previous.statusType, currentLine));
                                }
                                token = "";
                                // Necessitamos pegar o verdadeiro estado do caractere lido, pois seja : precedido por um identificador,
                                // o estado da máquina após ler o : irá a 0 indicando que foi terminada a leitura do identificador, porém, a leitura de um : resulta no estado 5
                                status = getNextStatus(status, character);
                                if (status != Status.Q0 && status != Status.Q12) { // No caso do estado resultante não ser 0 ou 12 (comentário), adiciona o caractere lido ao token
                                    token += character;
                                }
                                break;
                            default: // Em outros casos, não é necessário verificar se o token é uma palavra reservada ou um operador
                                table.add(new Token(token, previous.statusType, currentLine));
                                token = "";
                                status = getNextStatus(status, character);
                                if (status != Status.Q0 && status != Status.Q12) { // No caso do estado resultante não ser 0 ou 12 (comentário), adiciona o caractere lido ao token
                                    token += character;
                                }
                                break;
                        }
                    } else if (status != Status.Q12) { // Token ainda está sendo lido passar para o próximo caractere
                        token += character;
                    }
                } catch (InvalidSymbolException ex) {
                	errorLine = currentLine;
                	currentLine = 0;
                    throw new LexicalException("(Linha " + errorLine + ") " + ex.getMessage());
                }
            }
            // Fim da linha
            if(status != Status.Q0 && status != Status.Q12) { // Caso a linha tenha terminado em um estado válido para um símbolo
                if(status == Status.Q1 && keywords.containsKey(token)) {
                    table.add(new Token(token, keywords.get(token), currentLine));
                } else {
                    table.add(new Token(token, status.statusType, currentLine));
                }
            }
            token = ""; // Limpar o token lido
            
            if(status != Status.Q12) { // Checa se não se está dentro de um comentário ao final da linha
            	status = Status.Q0; // Resetar o estado            	
            }
        }
        
        currentLine = 0;
        if(status == Status.Q12) { // Chegou ao fim da leitura e o comentário não acabou
            throw new LexicalException("(Linha " + errorLine + ") " + new UnfinishedCommentException().getMessage());
        }
    }

    /** Método que retorna a tabela de símbolos
     *
     * @return A tabela de símbolos
     */
    public List<Token> getTable() {
        return table;
    }
}