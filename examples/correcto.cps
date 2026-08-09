
let a: integer = 10;
let b: string = "hola";
let c: boolean = true;
let d = null;


let x = 5 + 3 * 2;
let y = !(x < 10 || x > 20);

let z = (1 + 2) * 3;

let nombre: string;
nombre = "Compiscript";

const PI: integer = 314;

function saludar(nombre: string): string {
return "Hola " + nombre;
}

let mensaje = saludar("Mundo");

print(dog.nombre);

let lista = [1, 2, 3];
print(lista[0]);

let notas: integer[] = [90, 85, 100];
let matriz: integer[][] = [[1, 2], [3, 4]];

function crearContador(): integer {
function siguiente(): integer {
return 1;
}
return siguiente();
}

class Animal {
let nombre: string;
function constructor(nombre: string) {
this.nombre = nombre;
}
function hablar(): string {
return this.nombre + " hace ruido.";
}
}

class Perro : Animal {
function hablar(): string {
return this.nombre + " ladra.";
}
}

this.nombre = "Firulais";


let perro: Perro = new Perro("Toby");

{
let x = 42;
print(x);
}

if (x > 10) {
print("Mayor a 10");
} else {
print("Menor o igual");
}

while (x < 5) {
x = x + 1;
}

do {
x = x - 1;
} while (x > 0);

for (let i: integer = 0; i < 3; i = i + 1) {
print(i);
}

foreach (item in lista) {
print(item);
}


switch (x) {
case 1:
print("uno");
case 2:
print("dos");
default:
print("otro");
}

try {
let peligro = lista[100];
} catch (err) {
print("Error atrapado: " + err);
}

function suma(a: integer, b: integer): integer {
return a + b;
}

