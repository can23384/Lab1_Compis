# Analizador de Compiscript por consola

Versión inicial del laboratorio usando Java + ANTLR 4.13.2

## Requisitos

- JDK 17 o superior
- Maven 3.x

## Ejecutar

```bash
mvn compile
mvn exec:java -Dexec.args="examples/correcto.cps"
mvn exec:java -Dexec.args="examples/errores.cps"
```

Los archivos `CompiscriptLexer.java`, `CompiscriptParser.java`, `CompiscriptListener.java`
y `CompiscriptBaseListener.java` fueron generados por ANTLR y no deben editarse manualmente.
