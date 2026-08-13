# Interfaz gráfica del analizador de Compiscript

## Propósito y tecnología

La interfaz permite seleccionar, visualizar y analizar archivos `.cps` sin depender de la consola. Usa Swing, incluido en JDK 17, para evitar dependencias nativas y mantener una ejecución sencilla en Windows, macOS y Linux. El diseño es redimensionable y el análisis se ejecuta fuera del hilo gráfico para que la ventana permanezca receptiva.

## Requisitos y comandos

- JDK 17 o superior.
- Maven 3.x, o el Maven Wrapper incluido (recomendado).

Desde la raíz del repositorio:

```bash
.\mvnw.cmd clean package
.\mvnw.cmd compile exec:java@gui
```

El analizador de consola existente se conserva:

```bash
.\mvnw.cmd exec:java -Dexec.args="examples/correcto.cps"
```

## Componentes de la ventana

- **Abrir archivo .cps:** abre un selector filtrado por la extensión del lenguaje.
- **Analizar:** se habilita tras cargar un archivo válido e inicia ANTLR4 de forma explícita.
- **Archivo y estado:** muestran la ruta y uno de los estados `SIN ARCHIVO`, `LISTO`, `ANALIZANDO`, `CORRECTO` o `CON ERRORES`.
- **Código fuente:** presenta el texto UTF-8 con fuente monoespaciada, números de línea y desplazamiento horizontal y vertical.
- **Tabla de resultados:** incluye correlativo, tipo, línea, columna, símbolo/token y descripción. Las filas indican además el tipo por texto y por fondo diferenciado. Un clic en una fila selecciona la línea relacionada en el código.
- **Resumen:** presenta archivo y cantidades léxica, sintáctica y total. Cuando no hay errores muestra un mensaje explícito de éxito.

## Procedimiento de uso

1. Ejecute la interfaz con el comando anterior.
2. Pulse **Abrir archivo .cps** y seleccione un archivo. Cancelar el selector no altera la aplicación.
3. Verifique la ruta y el contenido cargado.
4. Pulse **Analizar**.
5. Consulte el resumen y, si existen errores, la tabla. Seleccione una fila para ir a su línea.
6. Abra otro archivo y repita; no es necesario reiniciar.

Una extensión distinta, un archivo inaccesible o un archivo mayor de 2 MB produce un aviso comprensible. Un archivo vacío se puede analizar y, de acuerdo con la regla raíz `statement* EOF`, es sintácticamente válido.

## Errores e integración con ANTLR4

`CompiscriptGui` invoca `CompiscriptAnalyzer`, la misma fachada usada por la consola. Esta crea el `CompiscriptLexer` y `CompiscriptParser` generados por ANTLR 4.13.2. Se retiran los listeners predeterminados de consola y se conectan `LexerErrorListener` y `ParserErrorListener` a un `ErrorCollector`. El flujo llena primero todos los tokens y usa `DefaultErrorStrategy` para continuar tras errores. `AnalysisResult` entrega objetos `AnalysisError` estructurados; no se reinterpretan impresiones de consola.

Los mensajes internos de ANTLR se convierten a español. El recolector conserva el orden y elimina duplicados exactos por tipo, ubicación y símbolo. Las líneas y columnas mostradas comienzan en **1**; internamente ANTLR entrega la columna desde cero y el listener suma uno.

## Casos de la rúbrica

Los archivos están en `examples/rubrica/` y fueron escritos exclusivamente con construcciones de `grammar/Compiscript.g4`.

| Archivo | Complejidad | Resultado previsto |
|---|---|---|
| `01_baja_correcto.cps` | Baja | Sin errores |
| `02_baja_lexicos.cps` | Baja | 3 o más léxicos |
| `03_baja_sintacticos.cps` | Baja | 3 o más sintácticos |
| `04_baja_mixtos.cps` | Baja | 2 o más de cada tipo |
| `05_media_correcto.cps` | Media | Sin errores |
| `06_media_lexicos.cps` | Media | 3 o más léxicos |
| `07_media_sintacticos.cps` | Media | 3 o más sintácticos |
| `08_media_mixtos.cps` | Media | 2 o más de cada tipo |

Para el video, abra los archivos en ese orden, pulse **Analizar** y señale el estado, los contadores, las columnas y la navegación al código. Los casos bajos contienen tres tipos, constante, dos operadores, condición, ciclo y `foreach`. Los medios agregan arreglo, dos clases, dos objetos, dos funciones y dos llamadas.

## Problemas comunes

- **`mvn` no se reconoce:** instale Maven y agréguelo a `PATH`.
- **Versión de Java incompatible:** compruebe `java -version` y `mvn -version`; ambos deben usar JDK 17 o posterior.
- **La ventana no abre en un servidor:** Swing requiere una sesión gráfica de escritorio.
- **No se habilita Analizar:** seleccione un archivo regular, legible y terminado en `.cps`.
- **Caracteres extraños:** guarde el archivo como UTF-8.
- **Dependencias no disponibles:** ejecute Maven con conexión la primera vez para descargar ANTLR y los complementos.

## Pruebas automatizadas

```bash
.\mvnw.cmd test
.\mvnw.cmd clean package
```

Las pruebas cubren archivo válido, clasificación de errores, múltiples errores, línea/columna/token, mensajes en español y deduplicación. No abren ventanas ni requieren interacción manual.
