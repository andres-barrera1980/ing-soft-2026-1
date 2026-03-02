```markdown
# Calculadora de Consola

Una herramienta interactiva de línea de comandos para realizar cálculos matemáticos básicos y avanzados. Esta aplicación permite procesar dos tipos de operaciones:

### 1. Operaciones Binarias
Requieren dos valores de entrada para devolver un resultado:
*   **Suma (+):** `a + b`
*   **Resta (-):** `a - b`
*   **Multiplicación (*):** `a * b`
*   **División (/):** `a / b`
*   **Potencia (^):** `a ^ b`
*   **Modulo (%):** `a % b`

### 2. Operaciones Unarias
Requieren un único valor de entrada:
*   **Raíz Cuadrada (sqrt):** `√a`
*   **Valor Absoluto (abs):** `|a|`
*   **Factorial (!):** `a!`
*   **Inversión de signo (-):** `-a`
*   **Logaritmo (log):** `log(a)`
*   **Fibonacci (fib):** `fib(a)`

```markdown
### Funcionamiento y Arquitectura
Este proyecto está desarrollado en **Java 21**, aplicando principios fundamentales de **Programación Orientada a Objetos (OOP)**. La lógica de negocio se encuentra desacoplada de la interfaz de consola, utilizando polimorfismo para gestionar las distintas operaciones matemáticas de manera extensible y mantenible.
```

La calculadora solicita al usuario el tipo de operación deseada, pide los operandos necesarios y muestra el resultado final en la terminal, manejando errores comunes como la división por cero o raíces de números negativos.

```
