# FizzBuzz — Guía TDD (sin IA)

## Objetivo

Implementar la función `calculate(int number)` que devuelve:
- `"FizzBuzz"` si el número es divisible por 3 y por 5
- `"Fizz"` si el número es divisible por 3
- `"Buzz"` si el número es divisible por 5
- el número como string en cualquier otro caso

**Rango válido:** 1 a 100 (consultar constantes en [`FizzBuzz.java:6-9`](src/main/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzz.java))

## Regla: sin IA

- No usar generadores de código ni asistencia automática de IA para escribir tests o implementación.
- Seguir el ciclo TDD manualmente.

## Flujo TDD (resumido)

1. Escribir una prueba pequeña y específica que falle.
2. Ejecutar los tests y confirmar que falla.
3. Implementar la mínima lógica necesaria para pasar esa prueba.
4. Ejecutar tests; si pasan, refactorizar manteniendo tests verdes.
5. Repetir añadiendo nuevas pruebas hasta cubrir los casos requeridos.

## Criterios de aceptación / casos básicos

| Entrada | Salida |
|---------|--------|
| 1 | "1" |
| 2 | "2" |
| 3 | "Fizz" |
| 4 | "4" |
| 5 | "Buzz" |
| 6 | "Fizz" |
| 9 | "Fizz" |
| 10 | "Buzz" |
| 15 | "FizzBuzz" |
| 30 | "FizzBuzz" |

## Estructura del proyecto

### Implementación: `FizzBuzz.java`

La clase principal contiene:

- **Constantes** ([`FizzBuzz.java:8-13`](src/main/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzz.java)):
  - `MIN_VALUE = 1`: Valor mínimo permitido
  - `MAX_VALUE = 100`: Valor máximo permitido
  - `FIZZ_NUMBER = 3`: Divisor para "Fizz"
  - `BUZZ_NUMBER = 5`: Divisor para "Buzz"
  - `FIZZ = "Fizz"`: Literal para Fizz
  - `BUZZ = "Buzz"`: Literal para Buzz (extraído como constante)

- **Método `calculate(int number)`** ([`FizzBuzz.java:15-31`](src/main/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzz.java)):
  - Valida que el número esté dentro del rango `[MIN_VALUE, MAX_VALUE]`
  - Retorna el número como string si no es divisible por 3 ni 5
  - Concatena "Fizz" si es divisible por 3
  - Concatena "Buzz" si es divisible por 5

- **Métodos `print()`** ([`FizzBuzz.java:33-41`](src/main/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzz.java)):
  - `print()`: Imprime en `System.out` (sin parámetros)
  - `print(PrintStream output)`: Acepta un `PrintStream` inyectado para testabilidad
  - Itera e imprime el resultado de `calculate()` para todos los números en el rango

### Tests: `FizzBuzzTest.java`

Los tests cubren los siguientes casos (ver [`FizzBuzzTest.java`](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java)):

| Test | Línea | Propósito |
|------|-------|-----------|
| `testMinDomainRange()` | [20-25](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Verifica excepción para número < 1 |
| `testMaxDomainRange()` | [27-33](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Verifica excepción para número > 100 |
| `testOne()` | [79-88](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Valida "1" para entrada 1 |
| `testTwo()` | [90-99](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Valida "2" para entrada 2 |
| `testThree()` | [101-110](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Valida "Fizz" para entrada 3 |
| `testFizz()` | [35-44](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Valida "Fizz" para múltiplos de 3 (27) |
| `testFive()` | [112-121](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Valida "Buzz" para entrada 5 |
| `testSix()` | [123-132](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Valida "Fizz" para entrada 6 |
| `testBuzz()` | [46-55](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Valida "Buzz" para múltiplos de 5 (10) |
| `testNine()` | [134-143](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Valida "Fizz" para entrada 9 |
| `testTen()` | [145-154](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Valida "Buzz" para entrada 10 |
| `testFizzBuzz()` | [57-66](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Valida "FizzBuzz" para múltiplos de 15 (15) |
| `testThirty()` | [156-165](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Valida "FizzBuzz" para entrada 30 |
| `testNoFizzBuzz()` | [68-77](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Valida número como string (4)

## Ejecución de tests

```bash
mvn test
```

## Ejecución de la aplicación

```bash
mvn exec:java -Dexec.mainClass="co.edu.javeriana.ingsoft.kata.App"
```

## Notas prácticas

- Mantén las pruebas pequeñas y enfocadas.
- Evita implementar todos los casos antes de tener tests.
- Refactoriza solo cuando los tests estén verdes.
- La lógica usa constantes declaradas en la clase para mejorar mantenibilidad (ver [`FizzBuzz.java:8-13`](src/main/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzz.java)).
- El patrón de concatenación en `calculate()` permite agregar fácilmente más divisores en el futuro.
- El método `print(PrintStream)` permite inyectar salida para testing sin depender de `System.out`.

## Historial de mejoras (Refactoring)

Se aplicaron mejoras progresivas en dos tiers:

### TIER 1 - Mejoras críticas de calidad
1. **Refactorizar tests con `@BeforeEach`** - Elimina 6 instanciaciones repetidas de FizzBuzz
2. **Cambiar `Integer` a `int`** en constantes - Mejor eficiencia y sigue best practices
3. **Corregir mensaje de error** - Ahora usa constantes `MIN_VALUE` y `MAX_VALUE` en lugar de valores hardcodeados

### TIER 2 - Mejoras de mantenibilidad y testabilidad
1. **Extraer constante `BUZZ`** - Por consistencia con `FIZZ`
2. **Hacer `print()` testeable** - Crear sobrecarga `print(PrintStream)` para inyectar salida
3. **Ampliar cobertura de tests** - Agregar test cases para casos base: 1, 2, 3, 5, 6, 9, 10, 30

## Commits sugeridos / Realizados

**Commits de mejoras aplicadas:**

1. `08fb908` - Refactor: eliminar instanciación repetida con @BeforeEach
2. `969e19f` - Refactor: cambiar Integer→int, corregir mensaje error, eliminar comentarios
3. `2003640` - Refactor: extraer constante BUZZ y hacer print() testeable con 8 tests adicionales

## Dependencias

- **Java 21** (compilación)
- **JUnit 5.14.2** (testing) - consultar [`pom.xml:21-26`](pom.xml)
