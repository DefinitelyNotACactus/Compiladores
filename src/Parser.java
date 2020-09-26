import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
        Lexical lexical = new Lexical();
        try {
            List<String> lines = new ArrayList<>();
            String line;
            while((line = reader.readLine()) != null) {
                lines.add(line);
            }
            lexical.buildTokenTable(lines);
        } catch(IOException ex) {
            System.out.println("Erro durante a leitura do arquivo:\n" + ex.getMessage());
        } catch (UnfinishedCommentException | InvalidSymbolException ex) {
            System.out.println(ex.getMessage());
        }
        lexical.showTable();
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
