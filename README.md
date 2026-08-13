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

## Interfaz gráfica

La aplicación de escritorio permite abrir archivos `.cps`, ver su código y consultar
errores léxicos y sintácticos en una tabla. Se ejecuta con:

```bash
.\mvnw.cmd compile exec:java@gui
```

En Windows se recomienda el Maven Wrapper incluido, por lo que no es necesario
instalar Maven globalmente. En macOS o Linux use `./mvnw`.

La guía completa, incluidos los ocho casos de demostración de la rúbrica, está en
[INTERFAZ_GRAFICA.md](INTERFAZ_GRAFICA.md).
