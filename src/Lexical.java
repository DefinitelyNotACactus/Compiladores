import java.util.*;

public class Lexical {
    enum Status {
        Q0(null),
        Q1(Type.IDENTIFICADOR),
        Q20(Type.INTEIRO),
        Q21(Type.REAL),
        Q30(Type.DELIMITADOR),
        Q40(Type.DELIMITADOR),
        Q41(Type.ATRIBUICAO),
        Q50(Type.OP_RELACIONAL),
        Q60(Type.OP_RELACIONAL),
        Q61(Type.OP_RELACIONAL),
        Q62(Type.OP_RELACIONAL),
        Q70(Type.OP_RELACIONAL),
        Q71(Type.OP_RELACIONAL),
        Q80(Type.OP_ADITIVO),
        Q90(Type.OP_MULTIPLICATIVO),
        Q99(null);

        public final Type statusType;

        Status(Type statusType) {
            this.statusType = statusType;
        }
    }

    private List<Token> table;
    private Map<String, Type> keywords;
    private Set<Character> delimiters;
    private Status status;

    public Lexical() {
        table = new ArrayList<>();
        keywords = createKeywordsMap();
        delimiters = new HashSet<>(Arrays.asList(';', '.', '(', ')', ','));
        status = Status.Q0;
    }

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
        keywords.put("and", Type.OP_MULTIPLICATIVO);
        keywords.put("or", Type.OP_ADITIVO);

        return keywords;
    }

    private Status getNextStatus(Status current, Character c) throws InvalidSymbolException {
        switch(current) {
            case Q0: // Estado inicial
                switch (c) {
                    case ':': // c pode ser um delimitador ou atribuição
                        return Status.Q40;
                    case '=': // operador relacional
                        return Status.Q50;
                    case '<': // mesma coisa para < e > porém necessita-se ser checado se é acompanhado por um = ou um > no caso de <
                        return Status.Q60;
                    case '>':
                        return Status.Q70;
                    case '+': // Operadores aditivos
                    case '-':
                        return Status.Q80;
                    case '*': // Operadores multiplicativos
                    case '/':
                        return Status.Q90;
                    case '{': // Início de um comentário
                        return Status.Q99;
                    case ' ': // Caracteres para serem ignorados
                    case '\t':
                    case '\r':
                    case '\n':
                        return Status.Q0;
                    default: // Outros caracteres
                        if(delimiters.contains(c)) { // c é um delimitador
                            return Status.Q30;
                        } else if(Validator.isLetter(c)) { // c é uma letra
                            return Status.Q1;
                        } else if(Validator.isNumber(c)) { // c é um número
                            return Status.Q20;
                        }
                        throw new InvalidSymbolException(c); // Se chegar aqui, é pelo fato de c ser um símbolo não reconhecido
                }
            case Q1: // Nesse estado, se supõe que o token é um identificador
                if(Validator.isNumberOrCharacter(c) || c == '_') {
                    return Status.Q1;
                }
                return Status.Q0;
            case Q20:
                if(Validator.isNumber(c)) {
                    return Status.Q20; // Continua a ler o número
                } else if (c == '.') {
                    return Status.Q21; // O token passa a ser um número real
                }
                return Status.Q0;
            case Q21:
                if(Validator.isNumber(c)) { // Continua a ler o número real
                    return Status.Q21;
                }
                return Status.Q0;
            case Q30:
            case Q50:
            case Q80:
            case Q90:
                return Status.Q0; // Não deveria entrar aqui
            case Q40:
                if(c == '=') { // O token passa a ser uma atribuição
                    return Status.Q41;
                }
                return Status.Q0;
            case Q60:
                if(c == '>' || c == '=') {
                    return Status.Q60;
                }
                return Status.Q0;
            case Q70:
                if(c == '=') {
                    return Status.Q70;
                }
                return Status.Q0;
            case Q99:
                if(c == '}') { // Fim do comentário
                    return Status.Q0;
                }
                return Status.Q99; // o comentário continua
        }
        throw new InvalidSymbolException(c);
    }

    public void buildTokenTable(List<String> input) throws InvalidSymbolException, UnfinishedCommentException {
        table.clear();
        int currentLine = 0;
        Status previous;
        String token = "";
        // Laço de leitura
        for(String line : input) { // Para cada linha
            currentLine++;
            for(Character character : line.toCharArray()) { // Para cada caractere da linha
                // Obter o próximo e o estado atual
                previous = status;
                status = getNextStatus(previous, character);

                if(status == Status.Q0) { // O token foi lido
                    switch(previous) {
                        case Q0:
                        case Q99:
                            token = "";
                            break;
                        case Q1:
                            if(keywords.containsKey(token)) {
                                table.add(new Token(token, keywords.get(token), currentLine));
                            } else {
                                table.add(new Token(token, previous.statusType, currentLine));
                            }
                            token = "";
                            status = getNextStatus(status, character);
                            if(status != Status.Q0 && status != Status.Q99) {
                                token += character;
                            }
                            break;
                        default:
                            table.add(new Token(token, previous.statusType, currentLine));
                            token = "";
                    }
                } else if(status != Status.Q99){ // Token ainda está sendo lido passar para o próximo caractere
                    token += character;
                }
            }
            // Fim da linha
            if(status != Status.Q0 && status != Status.Q99) { // Caso a linha tenha terminado em um estado válido para um símbolo
                if(status == Status.Q1 && keywords.containsKey(token)) {
                    table.add(new Token(token, keywords.get(token), currentLine));
                } else {
                    table.add(new Token(token, status.statusType, currentLine));
                }
            }
            token = ""; // Limpar o token lido
            status = Status.Q0; // Resetar o estado
        }
        if(status == Status.Q99) { // Chegou ao fim da leitura e o comentário não acabou
            throw new UnfinishedCommentException();
        }
    }

    public void showTable() {
        System.out.println("Token \t Classificação \t Linha");
        for(Token entry : table) {
            System.out.println(entry.getValue() + " " + entry.getType().name + " " + entry.getLine());
        }
    }
}