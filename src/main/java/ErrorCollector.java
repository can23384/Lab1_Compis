import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ErrorCollector {
    private final Map<String, AnalysisError> errors = new LinkedHashMap<>();

    public void add(AnalysisError error) {
        errors.putIfAbsent(error.deduplicationKey(), error);
    }

    public List<AnalysisError> getErrors() {
        return List.copyOf(new ArrayList<>(errors.values()));
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }
}
