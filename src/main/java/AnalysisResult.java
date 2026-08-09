import java.nio.file.Path;
import java.util.List;

public record AnalysisResult(Path file, List<AnalysisError> errors) {
    public boolean success() {
        return errors.isEmpty();
    }

    public long lexicalErrorCount() {
        return errors.stream().filter(e -> e.type() == AnalysisError.Type.LEXICO).count();
    }

    public long syntacticErrorCount() {
        return errors.stream().filter(e -> e.type() == AnalysisError.Type.SINTACTICO).count();
    }
}
