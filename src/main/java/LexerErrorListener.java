import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LexerErrorListener extends BaseErrorListener {
    private static final Pattern TOKEN_RECOGNITION =
            Pattern.compile("token recognition error at: '(.*)'", Pattern.DOTALL);

    private final ErrorCollector collector;

    public LexerErrorListener(ErrorCollector collector) {
        this.collector = collector;
    }

    @Override
    public void syntaxError(
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e) {

        String lexeme = extractLexeme(msg);
        String description = describe(lexeme);

        collector.add(new AnalysisError(
                AnalysisError.Type.LEXICO,
                line,
                charPositionInLine + 1,
                printable(lexeme),
                description
        ));
    }

    private String extractLexeme(String message) {
        if (message == null) {
            return "<desconocido>";
        }

        Matcher matcher = TOKEN_RECOGNITION.matcher(message);
        if (matcher.find()) {
            String text = matcher.group(1);
            return text.isEmpty() ? "<vacío>" : text;
        }

        return "<desconocido>";
    }

    private String describe(String lexeme) {
        if (lexeme.startsWith("\"")) {
            return "La cadena de texto no es válida o no fue cerrada correctamente.";
        }
        return "El símbolo o lexema no es reconocido por el lenguaje Compiscript.";
    }

    private String printable(String text) {
        String sanitized = text
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");

        if (sanitized.length() > 40) {
            return sanitized.substring(0, 37) + "...";
        }
        return sanitized;
    }
}
