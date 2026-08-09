let ano = 2026;

let nombre = 'Carlos';

let mensaje = "cadena sin cerrar;

let multilinea = "primera linea
segunda linea";

let frase = "Ella dijo \"hola\"";

let pi = 3.14159;

let hexadecimal = 0xFF;

let numeroGrande = 1_000_000;

let 123variable = 50;

let resultado = 10 @ 2;

if (edad >= 18)
    print("mayor");

if (x > 10) {
    print("A");
} else if (x > 5) {
    print("B");
}

do {
    x = x + 1;
} while (x < 10);

foreach (elemento in elementos) {
    print(elemento);
}

let datos = [
    10,
    20,
    30,
];

integer[] = [90, 85, 100];

procesar(
    nombre,
    edad,
    direccion,
);

function calcular(
    izquierda: integer,
    derecha: integer,
) {
    return izquierda + derecha;
}

function conectar(
    host: string,
    puerto: integer = 8080
) {
    print(host);
}

try {
    ejecutar();
} catch (error: Error) {
    print(error);
}

try {
    abrir();
} catch (error) {
    print(error);
} finally {
    cerrar();
}

let primero = [10, 20, 30][0];

let nombreUsuario = (usuario).nombre;

let resultadoFuncion = (funcion)(10);

let longitud = "hola".length;

let propiedad = null.propiedad;

switch (opcion) {
    case 1:
        print("uno");
        break;

    default:
        print("otro");
        break;

    case 2:
        print("dos");
        break;
}

switch (x) {
    case 1:
        print("A");
        break;

    default:
        print("B");
        break;

    default:
        print("C");
        break;
}

for (
    let i = 0;
    i < 10;
    let j = i;
) {
    print(i);
}

for (let i = 0; i < 10; i++) {
    print(i);
}

class Perro extends Animal {
    function ladrar() {
        print("guau");
    }
}

function casoMuyEscondido() {
    let calculadora = new Calculadora();
    let numeros = [10, 20, 30, 40];

    let resultado =
        calculadora.sumar(
            numeros[0],
            [100, 200, 300][1]
        );

    print(resultado);
}