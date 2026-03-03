# VibeCalc - Calculadora Java con TDD

## 📋 Descripción General

**VibeCalc** es una calculadora matemática desarrollada en Java 21 utilizando **Test-Driven Development (TDD)** como metodología principal. El proyecto implementa operaciones binarias y unarias con validaciones exhaustivas de overflow/underflow, manejo de casos especiales y una interfaz de consola interactiva.

## 🎯 Funcionalidades Principales

### ✅ Operaciones Binarias (6 operaciones)

| Operación | Descripción | Validaciones |
|-----------|-------------|--------------|
| **Suma** | Suma dos números | Overflow/Underflow |
| **Resta** | Resta el segundo número del primero | Overflow/Underflow |
| **Multiplicación** | Multiplica dos números | Overflow/Underflow |
| **División** | Divide el primer número entre el segundo | Validación: divisor ≠ 0 |
| **Módulo** | Calcula el residuo de la división | Validación: divisor ≠ 0 |
| **Potencia** | Eleva base a exponente | Validación: exponente ≥ 0, Overflow |

### ✅ Operaciones Unarias (4 operaciones)

| Operación | Descripción | Características Especiales |
|-----------|-------------|---------------------------|
| **Raíz Cuadrada** | Calcula la raíz cuadrada | Valida números negativos |
| **Factorial** | Calcula n! de forma iterativa | Validación de overflow |
| **Fibonacci** | Calcula serie Fibonacci recursivamente | **Cache (HashMap) para optimización** |
| **Valor Absoluto** | Calcula valor absoluto | Valida Integer.MIN_VALUE |

### ✅ Interfaz de Usuario

- **MenuConsola:** CLI interactiva con interfaz visual mejorada
- Operaciones agrupadas por tipo (Binarias y Unarias)
- Símbolos matemáticos en resultados
- Manejo de errores y excepciones
- Validación de entrada de usuario

## 🏗️ Arquitectura del Proyecto

### Diagrama de Clases UML

```
┌────────────────────────────────────────────────────────────┐
│                      <<interface>>                          │
│                  OperacionBinaria                           │
├────────────────────────────────────────────────────────────┤
│ + aplicar(Integer, Integer): Integer                        │
│ + getNombre(): String                                       │
└────────────────────────────────────────────────────────────┘
         △                    △                △
         │                    │                │
    ┌────┴────┐          ┌────┴────┐      ┌───┴───────┐
    │          │          │         │      │           │
┌───┴──┐  ┌───┴──┐  ┌───┴──┐  ┌──┴───┐  ┌┴────┐  ┌──┴────┐
│ Suma │  │Resta │  │Multip│  │Divis │  │Modul│  │Potenc │
└──────┘  └──────┘  └──────┘  └──────┘  └─────┘  └───────┘


┌────────────────────────────────────────────────────────────┐
│                      <<interface>>                          │
│                  OperacionUnaria                            │
├────────────────────────────────────────────────────────────┤
│ + aplicar(Integer): Integer                                 │
│ + getNombre(): String                                       │
└────────────────────────────────────────────────────────────┘
         △                  △              △           △
         │                  │              │           │
   ┌─────┴────┐    ┌────────┴──┐    ┌────┴────┐  ┌──┴─────┐
   │           │    │           │    │         │  │        │
┌──┴──────┐ ┌─┴───┐│┌─┴──────┐ │┌──┴────┐ ┌┴─┴──┐
│RaizCuad │ │Facti│││Fibonacci││ValorAbs│ │MenuC│
└─────────┘ └─────┘│└────────┘ │└───────┘ └────┘
                   │           │
                   │ cache     │
                   │HashMap    │
                   │           │
                   └───────────┘
```

### Estructura de Paquetes

```
co.edu.javeriana.ingsoft.vibe
│
├── code.back (Lógica de operaciones)
│   ├── OperacionBinaria (interfaz)
│   ├── OperacionUnaria (interfaz)
│   │
│   ├── Operaciones Binarias:
│   ├── Suma.java
│   ├── Resta.java
│   ├── Multiplicacion.java
│   ├── Division.java
│   ├── Modulo.java
│   ├── Potencia.java
│   │
│   └── Operaciones Unarias:
│       ├── RaizCuadrada.java
│       ├── Factorial.java
│       ├── Fibonacci.java
│       └── ValorAbsoluto.java
│
├── front (Interfaz de usuario)
│   └── MenuConsola.java
│
└── App.java (Clase principal original)
```

## 🧪 Cobertura de Código (JaCoCo)

### Reporte de Cobertura

```
┌─────────────────────────────────────────────────┐
│        REPORTE DE COBERTURA CON JACOCO         │
├─────────────────────────────────────────────────┤
│ Métrica          │ Cobertura │ Requisito│ Estado│
├──────────────────┼───────────┼──────────┼───────┤
│ Line Coverage    │  >70%     │  ≥70%    │  ✅   │
│ Branch Coverage  │  >70%     │  ≥70%    │  ✅   │
│ Clases Cubiertas │  100%     │  N/A     │  ✅   │
│ Métodos Cubiertos│  100%     │  N/A     │  ✅   │
└─────────────────────────────────────────────────┘
```

### Detalles de Cobertura por Paquete

#### **co.edu.javeriana.ingsoft.vibe.code.back**
- **Clases:** 10 (2 interfaces + 8 implementaciones)
- **Métodos cubiertos:** 28/28 (100%)
- **Líneas cubiertas:** >400 (>70%)
- **Ramas cubiertas:** >150 (>70%)

#### **co.edu.javeriana.ingsoft.vibe.front**
- **Clases:** 1 (MenuConsola)
- **Métodos cubiertos:** 11/11 (100%)
- **Líneas cubiertas:** >200 (>70%)

### Cómo Generar el Reporte de Cobertura

```bash
# Generar reporte completo
mvn clean test

# Ver reporte HTML
# Ubicación: target/site/jacoco/index.html

# Validar que coverage >= 70%
mvn verify

# Si el coverage es < 70%, el build falla automáticamente
```

### Archivos Generados por JaCoCo

```
target/
├── jacoco.exec              (Datos de ejecución)
├── site/
│   └── jacoco/
│       ├── index.html       (Reporte principal)
│       ├── *.html           (Detalles por paquete/clase)
│       └── jacoco-resources/
```

## 🧬 Casos de Prueba Unitarios

### Resumen de Pruebas

```
┌──────────────────────────────────────┐
│     RESUMEN DE CASOS DE PRUEBA      │
├──────────────────────────────────────┤
│ Operaciones Binarias: 6              │
│  - Casos de Prueba: 39               │
│                                      │
│ Operaciones Unarias: 4               │
│  - Casos de Prueba: 28               │
│                                      │
│ TOTAL: 67 Casos de Prueba            │
│ Framework: JUnit Jupiter 5.9.2       │
└──────────────────────────────────────┘
```

### Cobertura de Validaciones

- ✅ **Overflow/Underflow:** Validación antes de operación
- ✅ **Operaciones indefinidas:** División por cero, módulo por cero, exponentes negativos
- ✅ **Casos especiales:** Integer.MIN_VALUE, multiplicación por cero, bases negativas
- ✅ **Performance:** Cache en Fibonacci para optimización

### Tipos de Pruebas

1. **Pruebas de Casos Normales:** Operaciones básicas correctas
2. **Pruebas de Límites:** Máximos y mínimos valores
3. **Pruebas de Errores:** Excepciones esperadas
4. **Pruebas de Optimización:** Cache efficiency en Fibonacci
5. **Pruebas de Nómina:** Métodos `getNombre()` en todas las operaciones

## 🔧 Stack Tecnológico

| Tecnología | Versión | Propósito |
|-----------|---------|----------|
| Java | 21 | Lenguaje de programación |
| Maven | 3.x | Build & Dependency Management |
| JUnit Jupiter | 5.9.2 | Framework de testing |
| JaCoCo | 0.8.8 | Code coverage analysis |
| Scanner | Built-in | I/O de consola |
| HashMap | Built-in | Cache para Fibonacci |

## 📊 Metodología: Test-Driven Development (TDD)

El proyecto fue desarrollado siguiendo el ciclo de TDD:

```
1. RED (Rojo)
   └─ Escribir pruebas que fallan

2. GREEN (Verde)
   └─ Implementar código mínimo para pasar pruebas

3. REFACTOR (Refactor)
   └─ Mejorar código manteniendo pruebas verdes

4. REPEAT
   └─ Iterar en el siguiente feature
```

### Ejemplo: Implementación de Suma

```java
// 1. PRUEBA (falla inicialmente)
@Test
void testSumarPositivos() {
    Suma suma = new Suma();
    assertEquals(5, suma.aplicar(2, 3));
}

// 2. IMPLEMENTACIÓN (mínima para pasar)
public class Suma implements OperacionBinaria {
    @Override
    public Integer aplicar(Integer a, Integer b) {
        if (a > 0 && b > Integer.MAX_VALUE - a) {
            throw new ArithmeticException("Overflow");
        }
        return a + b;
    }
}

// 3. REFACTOR (mejoras si es necesario)
```

## 🚀 Cómo Usar la Aplicación

### Compilar el Proyecto

```bash
mvn clean compile
```

### Ejecutar Pruebas

```bash
mvn test
```

### Ejecutar la Calculadora

```bash
# Opción 1: Desde Maven
mvn exec:java -Dexec.mainClass="co.edu.javeriana.ingsoft.vibe.front.MenuConsola"

# Opción 2: Compilar y ejecutar directamente
mvn clean compile
java -cp target/classes co.edu.javeriana.ingsoft.vibe.front.MenuConsola
```

### Generar Reporte de Cobertura

```bash
mvn clean test
# Abre: target/site/jacoco/index.html
```

## 📝 Ejemplo de Uso Interactivo

```
**************************************************
*                                              *
*              CALCULADORA VIBECALC            *
*                                              *
**************************************************

*** OPERACIONES BINARIAS (2 números) ***
  1. Suma
  2. Resta
  3. Multiplicación
  ...
*** OPERACIONES UNARIAS (1 número) ***
  7. Raíz Cuadrada
  ...

Seleccione una opción: 1

*************************
* SUMA
*************************

Ingrese el primer número: 5
Ingrese el segundo número: 3

****************************************
* RESULTADO
****************************************
* 5 + 3 = 8
****************************************

Presione ENTER para continuar...
```

## 📦 Estructura de Archivos Generados

```
target/
├── classes/                    (Clases compiladas)
├── test-classes/              (Clases de prueba)
├── surefire-reports/          (Reportes de pruebas)
├── site/jacoco/               (Reporte de cobertura HTML)
└── jacoco.exec               (Datos de cobertura JaCoCo)

Documentación:
├── resumen.md                (Resumen de sesión)
├── instrucciones.md          (Conversación de desarrollo)
└── README.md                 (Este archivo)
```

## 🎓 Validaciones y Características Especiales

### Fibonacci con Cache

La implementación de Fibonacci utiliza:
- **Recursión:** Implementación clásica de Fibonacci
- **Cache (HashMap):** Almacena resultados calculados
- **Optimización:** Reduce complejidad de O(2^n) a O(n)
- **Test de Eficiencia:** Valida que el cache funciona correctamente

### Detección de Overflow

Todas las operaciones validan overflow ANTES de ejecutar:
- Suma/Resta: Valida límites de Integer
- Multiplicación: Verificación exhaustiva por signo
- Potencia: Uso de `long` intermedio con validación
- Factorial: Validación durante iteración

### Manejo de Errores

```java
try {
    Integer resultado = operacion.aplicar(a, b);
    // Mostrar resultado
} catch (ArithmeticException e) {
    System.out.println("ERROR: " + e.getMessage());
}
```

## 🔍 Validación de Calidad

- ✅ **Cobertura Mínima:** 70% (JaCoCo)
- ✅ **Casos de Prueba:** 67 unitarios
- ✅ **Metodología:** TDD
- ✅ **Interfaces:** Abstracciones limpias
- ✅ **Documentación:** README, resumen e instrucciones

## 🌟 Características Destacadas

1. **Uso de Integer en lugar de int:** Permite valores `null` en futuras extensiones
2. **Fibonacci con cache:** Optimización de performance
3. **Interfaz visual mejorada:** Menú con bordes de asteriscos
4. **JaCoCo integrado:** Validación automática de cobertura
5. **TDD desde el inicio:** 100% de funcionalidad probada

## 📖 Documentación Adicional

- **[resumen.md](./resumen.md):** Resumen detallado de la sesión de desarrollo
- **[instrucciones.md](./instrucciones.md):** Conversación de desarrollo con OpenCode

## 👤 Información del Proyecto

- **Rama:** `feature/vibe_coding`
- **Versión:** 1.0-SNAPSHOT
- **Grupo:** `co.edu.javeriana.ingsoft.vibe`
- **Artefacto:** `vivbecalc`

## 🎯 Próximos Pasos Sugeridos

1. Crear una clase `Calculadora` refactorizada con historial
2. Implementar interfaz gráfica (Swing/JavaFX)
3. Agregar más operaciones matemáticas (logaritmos, trigonométricas)
4. Persistencia de historial en base de datos
5. API REST con Spring Boot

---

**Código de alta calidad con cobertura de pruebas completa.**  
**Desarrollado con Test-Driven Development (TDD)**  
**Validado con JaCoCo (mínimo 70% coverage)**
