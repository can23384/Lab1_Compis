import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.ArrayList;
import java.util.List;

public final class ParserErrorListener extends BaseErrorListener {
    private final ErrorCollector collector;

    public ParserErrorListener(ErrorCollector collector) {
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

        if (!(recognizer instanceof Parser parser)) {
            return;
        }

        Token token = offendingSymbol instanceof Token ? (Token) offendingSymbol : null;
        String symbol = token == null ? "<desconocido>" : printableToken(token);
        String expected = expectedTokens(parser);
        String description = buildDescription(token, expected);

        collector.add(new AnalysisError(
                AnalysisError.Type.SINTACTICO,
                line,
                charPositionInLine + 1,
                symbol,
                description
        ));
    }

    private String buildDescription(Token token, String expected) {
        if (token != null && token.getType() == Token.EOF) {
            return "El archivo terminó antes de completar la estructura. Se esperaba: " + expected + ".";
        }

        if (expected.equals("una construcción válida")) {
            return "Se encontró una construcción que no es válida en este punto del programa.";
        }

        return "Se encontró " + quote(token == null ? "<desconocido>" : token.getText())
                + " donde se esperaba: " + expected + ".";
    }

    private String expectedTokens(Parser parser) {
        IntervalSet set;
        try {
            set = parser.getExpectedTokens();
        } catch (Exception ignored) {
            return "una construcción válida";
        }

        if (set == null || set.isNil()) {
            return "una construcción válida";
        }

        Vocabulary vocabulary = parser.getVocabulary();
        List<String> names = new ArrayList<>();

        for (int tokenType : set.toList()) {
            names.add(humanTokenName(tokenType, vocabulary));
            if (names.size() == 8) {
                break;
            }
        }

        if (names.isEmpty()) {
            return "una construcción válida";
        }

        return String.join(", ", names);
    }

    private String humanTokenName(int tokenType, Vocabulary vocabulary) {
        if (tokenType == Token.EOF) {
            return "fin del archivo";
        }

        String literal = vocabulary.getLiteralName(tokenType);
        if (literal != null) {
            return literal;
        }

        String symbolic = vocabulary.getSymbolicName(tokenType);
        if (symbolic == null) {
            return "token " + tokenType;
        }

        return switch (symbolic) {
            case "Identifier" -> "identificador";
            case "Literal" -> "literal";
            case "IntegerLiteral" -> "entero";
            case "StringLiteral" -> "cadena de texto";
            default -> symbolic;
        };
    }

    private String printableToken(Token token) {
        if (token.getType() == Token.EOF) {
            return "<EOF>";
        }
        return quote(token.getText());
    }

    private String quote(String text) {
        if (text == null) {
            return "<desconocido>";
        }
        return "'" + text
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t") + "'";
    }
}
