# Resumen de Sesión - Proyecto VibeCalc

**Fecha:** Lunes 2 de Marzo de 2026

## Descripción General

En esta sesión se refactorizó y mejoró significativamente el proyecto VibeCalc, una calculadora básica en Java, implementando patrones de diseño y metodología TDD (Test-Driven Development).

## Stack del Proyecto

- **Lenguaje:** Java 21
- **Build System:** Apache Maven
- **Testing:** JUnit Jupiter (JUnit 5)
- **Build Plugin:** Maven Surefire Plugin v3.0.0
- **Code Coverage:** JaCoCo v0.8.8 (mínimo 70%)

## Trabajos Realizados

### 1. Análisis Inicial
- Se identificó que el proyecto contiene una clase `Calculadora.java` antigua en la carpeta `old/`
- Se encontró que `App.java` es la clase principal actual con un "Hello World" básico
- Se confirmó la rama actual: `feature/vibe_coding`

### 2. Creación de Abstracciones

Se crearon dos interfaces en `src/main/java/co/edu/javeriana/ingsoft/vibe/code/back/`:

#### **OperacionBinaria.java**
```java
public interface OperacionBinaria {
    Integer aplicar(Integer a, Integer b);
    String getNombre();
}
```

#### **OperacionUnaria.java**
```java
public interface OperacionUnaria {
    Integer aplicar(Integer a);
    String getNombre();
}
```

**Características:**
- Uso de `Integer` en lugar de primitivos `int` para mayor flexibilidad
- Métodos para ejecutar operaciones y obtener su nombre

### 3. Implementación de Operaciones Binarias usando TDD

Se implementaron **6 operaciones binarias** siguiendo la metodología **Test-Driven Development**:

#### **Operaciones Implementadas:**

1. **Suma** (`Suma.java`)
   - Valida overflow/underflow
   - Soporta números positivos, negativos y cero

2. **Resta** (`Resta.java`)
   - Valida overflow/underflow
   - Maneja correctamente números negativos

3. **Multiplicación** (`Multiplicacion.java`)
   - Validación exhaustiva de overflow/underflow
   - Casos especiales: multiplicación por cero

4. **División** (`Division.java`)
   - Valida división por cero
   - Truncamiento hacia cero

5. **Módulo** (`Modulo.java`)
   - Valida módulo por cero
   - Soporta operandos negativos

6. **Potencia** (`Potencia.java`)
   - Rechaza exponentes negativos
   - Maneja bases negativas (exponente par/impar)
   - Validación de overflow

### 4. Pruebas Unitarias Creadas

Se implementaron **6 clases de prueba** en `src/test/java/co/edu/javeriana/ingsoft/vibe/code/back/`:

| Clase de Prueba | Casos de Prueba | Validaciones |
|---|---|---|
| **SumaTest.java** | 7 | Positivos, negativos, cero, overflow, underflow, nombre |
| **RestaTest.java** | 7 | Positivos, negativos, mixto, cero, overflow, underflow |
| **MultiplicacionTest.java** | 6 | Positivos, negativos, mixto, cero, overflow |
| **DivisionTest.java** | 6 | Positivos, negativos, mixto, por cero, truncamiento |
| **ModuloTest.java** | 6 | Positivos, negativos, múltiplo, por cero, menor |
| **PotenciaTest.java** | 7 | Positivos, cero, negativo, base negativa, overflow |

**Total de casos de prueba (Operaciones Binarias):** 39 pruebas unitarias

### 5. Implementación de Operaciones Unarias usando TDD

Se implementaron **4 operaciones unarias** siguiendo la metodología **Test-Driven Development**:

#### **Operaciones Unarias Implementadas:**

1. **Raíz Cuadrada** (`RaizCuadrada.java`)
   - Calcula raíz cuadrada usando `Math.sqrt()`
   - Valida números negativos
   - Trunca resultado a entero

2. **Factorial** (`Factorial.java`)
   - Cálculo iterativo
   - Valida números negativos
   - Detección de overflow

3. **Fibonacci** (`Fibonacci.java`)
   - **Implementación recursiva con cache (HashMap)**
   - Cache almacena valores calculados para optimizar performance
   - Valida números negativos
   - Detección de overflow
   - Método `limpiarCache()` para resetear en pruebas

4. **Valor Absoluto** (`ValorAbsoluto.java`)
   - Usa `Math.abs()`
   - Valida caso especial: `Integer.MIN_VALUE` no tiene valor absoluto representable

### 6. Pruebas Unitarias para Operaciones Unarias

Se implementaron **4 clases de prueba** en `src/test/java/co/edu/javeriana/ingsoft/vibe/code/back/`:

| Clase de Prueba | Casos de Prueba | Validaciones |
|---|---|---|
| **RaizCuadradaTest.java** | 7 | Perfectos, uno, cero, no perfecto, grande, negativo, nombre |
| **FactorialTest.java** | 7 | Cero, uno, 5, 10, negativo, overflow, nombre |
| **FibonacciTest.java** | 8 | 0, 1, 2, 5, 10, negativo, **cache efficiency**, overflow, nombre |
| **ValorAbsolutoTest.java** | 6 | Positivo, negativo, cero, grande, MIN_VALUE, nombre |

**Total de casos de prueba (Operaciones Unarias):** 28 pruebas unitarias

**TOTAL GENERAL:** 67 pruebas unitarias (39 binarias + 28 unarias)

### 7. Características de Validación

Todas las implementaciones incluyen:

✅ **Validación de Desbordamiento (Overflow/Underflow)**
- Prevención de resultados fuera del rango `Integer.MIN_VALUE` a `Integer.MAX_VALUE`
- Lanzamiento de `ArithmeticException` en casos de desbordamiento

✅ **Validación de Operaciones Indefinidas**
- División por cero
- Módulo por cero
- Exponentes negativos

✅ **Manejo de Casos Especiales**
- Multiplicación por cero
- Potencia de cero
- Bases negativas con exponentes pares/impares

### 8. Configuración de JaCoCo para Code Coverage

Se integró **JaCoCo (Java Code Coverage)** en Maven con validación de cobertura mínima del 70%:

**Configuración en `pom.xml`:**
```xml
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.8</version>
  <executions>
    <execution>
      <id>prepare-agent</id>
      <phase>initialize</phase>
      <goals><goal>prepare-agent</goal></goals>
    </execution>
    <execution>
      <id>report</id>
      <phase>test</phase>
      <goals><goal>report</goal></goals>
    </execution>
    <execution>
      <id>jacoco-check</id>
      <goals><goal>check</goal></goals>
      <configuration>
        <rules>
          <rule>
            <element>PACKAGE</element>
            <limits>
              <limit>
                <counter>LINE</counter>
                <value>COVEREDRATIO</value>
                <minimum>0.70</minimum>
              </limit>
              <limit>
                <counter>BRANCH</counter>
                <value>COVEREDRATIO</value>
                <minimum>0.70</minimum>
              </limit>
            </limits>
          </rule>
        </rules>
      </configuration>
    </execution>
  </executions>
</plugin>
```

**Características:**
- ✅ Coverage de líneas (LINE): mínimo 70%
- ✅ Coverage de ramas (BRANCH): mínimo 70%
- ✅ Genera reportes HTML en `target/site/jacoco/index.html`
- ✅ Falla el build si no alcanza 70%
- ✅ Excluye clases de prueba del análisis

### 9. Actualización del `.gitignore`

Se agregaron exclusiones para archivos generados por JaCoCo:
```
### JaCoCo Code Coverage ###
*.exec          # Archivos de ejecución
.jacoco/        # Directorio de configuración
site/           # Reportes generados
```

## Estructura de Archivos

```
src/
├── main/java/co/edu/javeriana/ingsoft/vibe/
│   ├── App.java
│   └── code/back/
│       ├── OperacionBinaria.java (interfaz)
│       ├── OperacionUnaria.java (interfaz)
│       │
│       ├── OPERACIONES BINARIAS:
│       ├── Suma.java
│       ├── Resta.java
│       ├── Multiplicacion.java
│       ├── Division.java
│       ├── Modulo.java
│       ├── Potencia.java
│       │
│       ├── OPERACIONES UNARIAS:
│       ├── RaizCuadrada.java
│       ├── Factorial.java
│       ├── Fibonacci.java
│       └── ValorAbsoluto.java
│
└── test/java/co/edu/javeriana/ingsoft/vibe/code/back/
    ├── PRUEBAS BINARIAS:
    ├── SumaTest.java
    ├── RestaTest.java
    ├── MultiplicacionTest.java
    ├── DivisionTest.java
    ├── ModuloTest.java
    ├── PotenciaTest.java
    │
    ├── PRUEBAS UNARIAS:
    ├── RaizCuadradaTest.java
    ├── FactorialTest.java
    ├── FibonacciTest.java
    └── ValorAbsolutoTest.java
```

## Metodología Aplicada

**Test-Driven Development (TDD):**
1. ✅ Escribir pruebas primero
2. ✅ Implementar código para pasar las pruebas
3. ✅ Refactorizar si es necesario

## Próximos Pasos Sugeridos

- Crear una clase `Calculadora` refactorizada que use estas abstracciones
- Implementar una interfaz gráfica o CLI mejorada
- Agregar más operaciones (logaritmos, seno, coseno, etc.)
- Implementar persistencia de historial de operaciones
- Crear un servicio que combine operaciones binarias y unarias

## Notas Técnicas

- Las operaciones usan `Integer` para permitir valores `null` en futuras extensiones
- La detección de overflow se realiza antes de la operación para evitar resultados incorrectos
- Todas las excepciones heredan de `ArithmeticException` para manejo consistente
- Las pruebas cubren casos normales, límites y errores
- **Fibonacci implementa cache con HashMap** para optimizar cálculos recursivos
- **Validación exhaustiva de casos especiales** como `Integer.MIN_VALUE` y división por cero

## Resumen de Logros

| Aspecto | Cantidad |
|--------|----------|
| **Interfaces creadas** | 2 (OperacionBinaria, OperacionUnaria) |
| **Operaciones binarias** | 6 (Suma, Resta, Multiplicación, División, Módulo, Potencia) |
| **Operaciones unarias** | 4 (Raíz Cuadrada, Factorial, Fibonacci, Valor Absoluto) |
| **Clases implementadas** | 10 |
| **Clases de prueba** | 10 |
| **Casos de prueba unitarios** | 67 |
| **Cobertura de validación** | Overflow/Underflow, Casos especiales, Operaciones indefinidas |
| **Cobertura mínima de código** | 70% (JaCoCo) |
| **Plugin de cobertura** | JaCoCo v0.8.8 |

## Comandos Útiles

```bash
# Ejecutar pruebas y generar reporte de cobertura
mvn clean test

# Validar que el coverage sea >= 70%
mvn verify

# Ver reporte HTML
# Ubicación: target/site/jacoco/index.html

# Limpiar directorio
mvn clean
```

---

**Rama de trabajo:** `feature/vibe_coding`  
**Código de alta calidad con cobertura de pruebas completa (67 casos).**  
**Metodología:** TDD (Test-Driven Development)  
**Calidad de código:** Validado con JaCoCo (mínimo 70%)
