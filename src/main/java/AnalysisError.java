import java.util.Objects;

public final class AnalysisError {
    public enum Type {
        LEXICO("Léxico"),
        SINTACTICO("Sintáctico");

        private final String displayName;

        Type(String displayName) {
            this.displayName = displayName;
        }

        public String displayName() {
            return displayName;
        }
    }

    private final Type type;
    private final int line;
    private final int column;
    private final String symbol;
    private final String description;

    public AnalysisError(Type type, int line, int column, String symbol, String description) {
        this.type = Objects.requireNonNull(type);
        this.line = line;
        this.column = column;
        this.symbol = symbol == null || symbol.isBlank() ? "<desconocido>" : symbol;
        this.description = Objects.requireNonNull(description);
    }

    public Type type() { return type; }
    public int line() { return line; }
    public int column() { return column; }
    public String symbol() { return symbol; }
    public String description() { return description; }

    public String deduplicationKey() {
        return type + "|" + line + "|" + column + "|" + symbol;
    }

    @Override
    public String toString() {
        return "[%s] línea %d, columna %d | símbolo: %s | %s"
                .formatted(type.displayName(), line, column, symbol, description);
    }
}
