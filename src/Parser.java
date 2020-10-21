import util.LexicalException;
import util.SyntaxException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private File input;
    private BufferedReader reader;

    public Parser(String filename) throws IOException {
        input = new File(filename);
        reader = new BufferedReader(new FileReader(input));
    }

    public void doParse() {
        long endLexical, startLexical, endSyntax, startSyntax;
        Lexical lexical = new Lexical();
        try {
            List<String> lines = new ArrayList<>();
            String line;
            while((line = reader.readLine()) != null) {
                lines.add(line);
            }

            startLexical = System.currentTimeMillis();
            lexical.buildTokenTable(lines);
            endLexical = System.currentTimeMillis();

            System.out.println("Análise léxica realizada em: " + (endLexical - startLexical) + " ms");

            startSyntax = System.currentTimeMillis();
            Syntax syntax = new Syntax(lexical.getTable());
            syntax.program();
            endSyntax = System.currentTimeMillis();

            System.out.println("Análise sintática realizada em: " + (endSyntax - startSyntax) + " ms");
        } catch(IOException ex) {
            System.out.println("Erro durante a leitura do arquivo:\n" + ex.getMessage());
        } catch (LexicalException | SyntaxException ex) {
            System.out.println(ex.getMessage());
        }
        printTokenTable(lexical.getTable());
    }


    /** Método para mostrar a tabela de símbolos
     *
     */
    private void printTokenTable(List<Token> table) {
        BufferedWriter writer;
        String outputFile = input.getName().split("\\.", 2)[0] + ".csv";
        try {
            writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write("Token;Classificacao;Linha\n");
            for(Token token : table) {
                writer.write(String.format("\"%s\";%s;%d\n", token.getValue(), token.getType().name, token.getLine()));
            }
            writer.close();
            System.out.println("\nSaída do analisador léxico salva em " + outputFile);
        } catch (IOException ex) {
            System.out.println("Erro ao escrever o arquivo de saída:\n" + ex.getMessage());
        }
    }

    public static void main(String... args) {
        if(args.length == 0) {
            System.out.println("É necessário informar o arquivo de entrada como argumento!");
            System.exit(-1);
        }
        try {
            Parser parser = new Parser(args[0]);
            parser.doParse();
        } catch (IOException ex) {
            System.out.println("Erro ao abrir o arquivo de entrada:\n" + ex.getMessage());
        }
    }
}
