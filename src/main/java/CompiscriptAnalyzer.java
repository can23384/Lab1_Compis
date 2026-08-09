import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public final class CompiscriptAnalyzer {

    public AnalysisResult analyze(Path file) throws IOException {
        ErrorCollector collector = new ErrorCollector();

        CharStream input = CharStreams.fromPath(file, StandardCharsets.UTF_8);

        CompiscriptLexer lexer = new CompiscriptLexer(input);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new LexerErrorListener(collector));

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Fuerza el análisis léxico de TODO el archivo. Así un error léxico
        // no evita que se busquen errores posteriores.
        tokens.fill();
        tokens.seek(0);

        CompiscriptParser parser = new CompiscriptParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new ParserErrorListener(collector));

        // Estrategia estándar de ANTLR: intenta insertar/eliminar tokens y
        // sincronizarse para continuar después de un error sintáctico.
        parser.setErrorHandler(new DefaultErrorStrategy());

        // Regla raíz de Compiscript: statement* EOF
        parser.program();

        return new AnalysisResult(file, collector.getErrors());
    }
}
