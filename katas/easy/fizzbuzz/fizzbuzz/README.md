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

- **Constantes** ([`FizzBuzz.java:6-10`](src/main/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzz.java)):
  - `MIN_VALUE = 1`: Valor mínimo permitido
  - `MAX_VALUE = 100`: Valor máximo permitido
  - `FIZZ_NUMBER = 3`: Divisor para "Fizz"
  - `BUZZ_NUMBER = 5`: Divisor para "Buzz"
  - `FIZZ = "Fizz"`: Literal para Fizz

- **Método `calculate(int number)`** ([`FizzBuzz.java:12-29`](src/main/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzz.java)):
  - Valida que el número esté dentro del rango `[MIN_VALUE, MAX_VALUE]`
  - Retorna el número como string si no es divisible por 3 ni 5
  - Concatena "Fizz" si es divisible por 3
  - Concatena "Buzz" si es divisible por 5

- **Método `print()`** ([`FizzBuzz.java:31-35`](src/main/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzz.java)):
  - Itera e imprime el resultado de `calculate()` para todos los números en el rango

### Tests: `FizzBuzzTest.java`

Los tests cubren los siguientes casos (ver [`FizzBuzzTest.java`](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java)):

| Test | Línea | Propósito |
|------|-------|-----------|
| `testMinDomainRange()` | [12-18](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Verifica excepción para número < 1 |
| `testMaxDomainRange()` | [21-28](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Verifica excepción para número > 100 |
| `testFizz()` | [30-40](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Valida "Fizz" para múltiplos de 3 |
| `testBuzz()` | [42-52](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Valida "Buzz" para múltiplos de 5 |
| `testFizzBuzz()` | [54-64](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Valida "FizzBuzz" para múltiplos de 15 |
| `testNoFizzBuzz()` | [66-76](src/test/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzzTest.java) | Valida número como string |

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
- La lógica usa constantes declaradas en la clase para mejorar mantenibilidad (ver [`FizzBuzz.java:6-10`](src/main/java/co/edu/javeriana/ingsoft/kata/fizzbuzz/FizzBuzz.java)).
- El patrón de concatenación en `calculate()` permite agregar fácilmente más divisores en el futuro.

## Commits sugeridos

1. Implementación inicial con tests unitarios
2. Refactor de constantes para mejor legibilidad
3. Extensión con nuevos casos de prueba si es necesario

## Dependencias

- **Java 21** (compilación)
- **JUnit 5.14.2** (testing) - consultar [`pom.xml:21-26`](pom.xml)
