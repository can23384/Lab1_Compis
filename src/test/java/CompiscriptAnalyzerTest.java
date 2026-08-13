import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CompiscriptAnalyzerTest {
    @TempDir Path tempDir;

    private AnalysisResult analyze(String source) throws Exception {
        Path file = tempDir.resolve("prueba.cps");
        Files.writeString(file, source, StandardCharsets.UTF_8);
        return new CompiscriptAnalyzer().analyze(file);
    }

    @Test void acceptsValidFile() throws Exception {
        AnalysisResult result = analyze("let edad: integer = 20;\nprint(edad);\n");
        assertTrue(result.success());
        assertEquals(0, result.totalErrorCount());
    }

    @Test void classifiesAndPreservesMultipleErrors() throws Exception {
        AnalysisResult result = analyze("@ let x = 1\n# let y = 2;\n");
        assertTrue(result.lexicalErrorCount() >= 2);
        assertTrue(result.syntacticErrorCount() >= 1);
        AnalysisError lexical = result.errors().stream().filter(e -> e.type() == AnalysisError.Type.LEXICO).findFirst().orElseThrow();
        assertEquals(1, lexical.line());
        assertEquals(1, lexical.column());
        assertEquals("@", lexical.symbol());
        assertFalse(lexical.description().toLowerCase().contains("token recognition error"));
    }

    @Test void filtersExactDuplicates() {
        ErrorCollector collector = new ErrorCollector();
        AnalysisError error = new AnalysisError(AnalysisError.Type.LEXICO, 2, 4, "@", "Símbolo no reconocido.");
        collector.add(error);
        collector.add(error);
        assertEquals(1, collector.getErrors().size());
    }

    @Test void messagesAreReadableSpanishAndColumnsAreOneBased() throws Exception {
        AnalysisResult result = analyze("let x = 1\n");
        AnalysisError error = result.errors().get(0);
        assertTrue(error.line() >= 1);
        assertTrue(error.column() >= 1);
        assertTrue(error.description().startsWith("Se encontró") || error.description().startsWith("El archivo terminó"));
        assertFalse(error.description().contains("mismatched input"));
    }
}
