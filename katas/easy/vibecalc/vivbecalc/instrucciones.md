# Instrucciones de Sesión - Conversación con OpenCode

## Resumen de la Sesión de Desarrollo del Proyecto VibeCalc

---

### **Tú:** ¿Cuál es el stack de este proyecto?

**OpenCode:**
El proyecto usa:
- **Lenguaje:** Java 21
- **Build:** Apache Maven
- **Testing:** JUnit Jupiter (JUnit 5)
- **Plugin de build:** Maven Surefire Plugin v3.0.0

Es un proyecto Maven básico con una clase `Calculadora.java` antigua en la carpeta `old/` y una clase `App.java` con un "Hello World".

---

### **Tú:** Explicame qué hace el código

**OpenCode:**
`App.java` es la clase principal que solo imprime "Hello World!". 

La clase `Calculadora.java` antigua (en carpeta `old/`) es una calculadora interactiva con menú textual que ofrece operaciones binarias (suma, resta, multiplicación, división) y operaciones unarias (negar, valor absoluto, incrementar, decrementar).

---

### **Tú:** ¿En qué rama estoy?

**OpenCode:**
Estás en la rama `feature/vibe_coding`.

---

### **Tú:** ¿Y no hay una clase llamada calculadora?

**OpenCode:**
Sí, existe `Calculadora.java` en `src/main/java/co/edu/javeriana/ingsoft/vibe/old/`. Es código antiguo que no está siendo usado actualmente. La clase `App.java` actual solo tiene un "Hello World!".

---

### **Tú:** Crea abstracciones para operaciones binarias y operaciones unarias en la carpeta backend

**OpenCode:**
Creé dos interfaces en `src/main/java/co/edu/javeriana/ingsoft/vibe/code/back/`:

1. **OperacionBinaria.java:**
   ```java
   public interface OperacionBinaria {
       Integer aplicar(Integer a, Integer b);
       String getNombre();
   }
   ```

2. **OperacionUnaria.java:**
   ```java
   public interface OperacionUnaria {
       Integer aplicar(Integer a);
       String getNombre();
   }
   ```

Ambas usan `Integer` en lugar de primitivos `int` para mayor flexibilidad.

---

### **Tú:** Primero cambia los primitivos de las abstracciones por Integers

**OpenCode:**
Ya hecho. Cambié ambas interfaces para usar `Integer` en los parámetros y valores de retorno en lugar de primitivos `int`.

---

### **Tú:** Actualiza el contexto

**OpenCode:**
El contexto está actualizado. Las abstracciones están en el paquete correcto `code/back/` con tipos `Integer`.

---

### **Tú:** Usa el método TDD para soportar la suma, resta, multiplicación, división, módulo y potencia. Ten en cuenta que no se desborden los resultados y valores para los cuales no esté definida la operación. Usa las abstracciones recién creadas.

**OpenCode:**
Implementé **6 operaciones binarias** usando TDD:

1. **Suma** - Valida overflow/underflow
2. **Resta** - Valida overflow/underflow
3. **Multiplicación** - Validación exhaustiva de overflow/underflow
4. **División** - Valida división por cero
5. **Módulo** - Valida módulo por cero
6. **Potencia** - Rechaza exponentes negativos, valida overflow

Cada operación tiene:
- ✅ Clase de prueba (39 casos totales)
- ✅ Implementación concreta
- ✅ Validación de desbordamiento
- ✅ Manejo de operaciones indefinidas

---

### **Tú:** Crea un archivo en la raíz del proyecto llamado resumen.md donde muestres el resumen de esta sesión

**OpenCode:**
Creé `resumen.md` en la raíz con:
- Stack del proyecto
- Descripción de trabajos realizados
- Abstracciones creadas
- Operaciones binarias implementadas
- Casos de prueba
- Estructura de archivos
- Próximos pasos

---

### **Tú:** Crea las siguientes operaciones unarias siguiendo la definición anterior: raíz cuadrada, factorial, fibonacci (usando cache e implementación recursiva), valor absoluto

**OpenCode:**
Implementé **4 operaciones unarias** usando TDD:

1. **RaizCuadrada** - Usa `Math.sqrt()`, valida negativos
2. **Factorial** - Cálculo iterativo, detección de overflow
3. **Fibonacci** - **Implementación recursiva con HashMap cache**, valida negativos
4. **ValorAbsoluto** - Usa `Math.abs()`, valida `Integer.MIN_VALUE`

Total de 28 nuevos casos de prueba unitarios para operaciones unarias.

---

### **Tú:** Actualiza el resumen

**OpenCode:**
Actualicé `resumen.md` agregando:
- Secciones 5 y 6 con detalle completo de operaciones unarias
- Tabla con 28 casos de prueba para operaciones unarias
- **67 casos de prueba totales** (39 binarias + 28 unarias)
- Tabla resumen de logros con todos los detalles

---

### **Tú:** Adiciona JaCoCo en Maven con un coverage de mínimo 70%, ajusta el git ignore

**OpenCode:**
Configuré JaCoCo en `pom.xml`:
- Plugin jacoco-maven-plugin v0.8.8
- Validación de coverage mínimo 70% para líneas y ramas
- Genera reportes HTML en `target/site/jacoco/index.html`
- Falla el build si no alcanza 70%

Actualicé `.gitignore` con:
```
### JaCoCo Code Coverage ###
*.exec
.jacoco/
site/
```

Ahora `mvn verify` valida automáticamente la cobertura.

---

### **Tú:** Actualiza el resumen

**OpenCode:**
Actualicé `resumen.md` agregando:
- JaCoCo en el stack
- Sección 8: Configuración completa de JaCoCo con el XML del pom.xml
- Sección 9: Cambios en `.gitignore`
- Tabla de logros con JaCoCo y cobertura 70%
- Comandos útiles para ejecutar las pruebas
- Notas finales con validación de cobertura

---

## Resumen Ejecutivo

En esta sesión completamos un **refactor completo del proyecto VibeCalc** implementando:

### ✅ Lo Logrado

| Item | Detalles |
|------|----------|
| **Interfaces** | 2 (OperacionBinaria, OperacionUnaria) |
| **Operaciones** | 10 (6 binarias + 4 unarias) |
| **Clases** | 20 (10 implementaciones + 10 pruebas) |
| **Casos de Prueba** | 67 unitarios con TDD |
| **Cobertura** | JaCoCo con mínimo 70% |
| **Validaciones** | Overflow/Underflow, casos especiales, operaciones indefinidas |
| **Documentación** | resumen.md e instrucciones.md |

### 📚 Metodología

**Test-Driven Development (TDD):**
1. Escribir pruebas primero
2. Implementar código para pasar pruebas
3. Refactorizar si es necesario

### 🎯 Rama de Trabajo

`feature/vibe_coding`

---

**Próximos pasos sugeridos:**
- Crear clase `Calculadora` refactorizada
- Implementar CLI mejorada
- Agregar más operaciones matemáticas
- Persistencia de historial
