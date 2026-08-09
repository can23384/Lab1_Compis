import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            printUsage();
            System.exit(1);
        }

        Path file = Path.of(args[0]).toAbsolutePath().normalize();

        if (!Files.isRegularFile(file)) {
            System.err.println("Error: el archivo no existe o no es un archivo regular: " + file);
            System.exit(1);
        }

        if (!file.getFileName().toString().toLowerCase().endsWith(".cps")) {
            System.err.println("Error: se esperaba un archivo con extensión .cps");
            System.exit(1);
        }

        try {
            CompiscriptAnalyzer analyzer = new CompiscriptAnalyzer();
            AnalysisResult result = analyzer.analyze(file);
            printResult(result);
        } catch (IOException e) {
            System.err.println("No fue posible leer el archivo: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void printResult(AnalysisResult result) {
        System.out.println("============================================================");
        System.out.println(" ANALIZADOR LÉXICO Y SINTÁCTICO DE COMPISCRIPT");
        System.out.println("============================================================");
        System.out.println("Archivo: " + result.file());
        System.out.println();

        if (result.success()) {
            System.out.println("✓ Archivo analizado correctamente.");
            System.out.println("  No se encontraron errores léxicos ni sintácticos.");
            return;
        }

        System.out.printf("Se encontraron %d error(es): %d léxico(s) y %d sintáctico(s).%n%n",
                result.errors().size(),
                result.lexicalErrorCount(),
                result.syntacticErrorCount());

        int number = 1;
        for (AnalysisError error : result.errors()) {
            System.out.printf("%d. Tipo: %s%n", number++, error.type().displayName());
            System.out.printf("   Línea: %d%n", error.line());
            System.out.printf("   Columna: %d%n", error.column());
            System.out.printf("   Símbolo/lexema: %s%n", error.symbol());
            System.out.printf("   Descripción: %s%n%n", error.description());
        }
    }

    private static void printUsage() {
        System.out.println("Uso:");
        System.out.println("  java Main <archivo.cps>");
        System.out.println();
        System.out.println("Con Maven:");
        System.out.println("  mvn exec:java -Dexec.args=\"ruta/al/archivo.cps\"");
    }
}
