@ # $
let edad: integer = 20; let nombre: string = "Ana"; let activo: boolean = true;
const LIMITE: integer = 5; let total = edad + LIMITE; let mitad = total / 2;
if (activo) { print(nombre); } while (edad > 18) { edad = edad - 1; }
let datos: integer[] = [1, 2, 3]; foreach (valor in datos) { print(valor); }
class Persona { let nombre: string; function hablar() { print(this.nombre); } }
class Curso { let titulo: string; function mostrar() { print(this.titulo); } }
function sumar(a: integer, b: integer): integer { return a + b; }
function saludar(texto: string): string { return "Hola " + texto; }
let persona: Persona = new Persona(); let curso: Curso = new Curso();
let resultado = sumar(2, 3); let saludo = saludar(nombre);
