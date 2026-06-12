# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [09]: TDD

### Estudiante
- **Nombre completo**: Juan Pablo Suarez Moreno

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Claude Opus 4.5 |
| **¿Por qué elegiste este LLM?** | Es excelente para el desarrollo de codigo razonamiento exteso. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
[Pega aquí el prompt exacto que enviaste al LLM. 
Incluye TODO el texto, sin editar ni resumir.

Un buen prompt incluye:
- Contexto del proyecto OpenLib Market
- El código o situación específica
- Lo que esperas que el LLM haga
- Restricciones (ej: "usa Java 21", "aplica SOLID")
- Formato de salida esperado (ej: "respuesta en markdown con código Java")]

[ROL]
Eres un profesor de fundamentos de software

[CONTEXTO]
Proyecto: OpenLib Market (Plataforma transaccional E-commerce para libros Open Source).
Módulo actual: Seller / Core - Control de Inventario (Gestión de publicación y disponibilidad).
Stack Tecnológico: Java 25, Spring Boot 4.x, JUnit 5, Mockito.

[PROBLEMA]
Necesito implementar una funcionalidad crucial de control de inventario utilizando la metodología de Desarrollo Guiado por Pruebas (TDD). La lógica de negocio dicta lo siguiente:
1. Cuando el stock de un libro se reduce a cero (0), su estado debe cambiar automáticamente a "AGOTADO".
2. Al agotarse, el sistema debe disparar una notificación al vendedor (Seller) y el frontend deberá deshabilitar la opción de "Agregar al carrito" (bloqueo por estado).
3. Cuando el vendedor repone el stock (cantidad > 0), el estado del libro debe cambiar automáticamente a "DISPONIBLE".

Necesito ver reflejado explícitamente el ciclo completo de TDD (Red -> Green -> Refactor) para la creación de la clase `ControlInventarioService`.

[RESTRICCIONES]
El LLM debe estructurar la respuesta simulando rigurosamente las tres fases de TDD:
1. **Fase FASE 1: RED (Escribir pruebas que fallen):** Presentar primero el código de las pruebas unitarias en JUnit 5 y Mockito que validen los escenarios de "Agotado con notificación" y "Reposición de Stock". Este código NO debe compilar o debe fallar porque las clases de servicio o métodos aún no existen.
2. **Fase FASE 2: GREEN (Código mínimo para pasar):** Presentar la implementación más simple, básica y directa de `ControlInventarioService` (y entidades necesarias como `Libro`, `EstadoLibro`, `NotificacionService`) para que las pruebas anteriores pasen con éxito.
3. **Fase FASE 3: REFACTOR (Mejorar el diseño):** Realizar un refactoring limpio sobre el código de la Fase 2 para aplicar principios SOLID (ej. separar la lógica de notificación del cambio de estado si es necesario o mejorar la encapsulación), garantizando que las pruebas sigan pasando (Green).

[FORMATO DE SALIDA]
Solicito la respuesta dividida claramente por títulos correspondientes a cada ciclo:
1. **### 1. Fase RED:** Bloques de código de las pruebas unitarias iniciales.
2. **### 2. Fase GREEN:** Bloques de código de la implementación mínima que hace pasar el test.
3. **### 3. Fase REFACTOR:** Bloques de código con el diseño final optimizado y una breve explicación de qué mejoras de Clean Code o diseño se aplicaron sin alterar el comportamiento.

[VALIDACIÓN / CRITERIOS DE ÉXITO]
La solución se considerará correcta si demuestra metodológicamente cómo el desarrollo es guiado por los tests, si usa correctamente las aserciones de JUnit 5 (`assertEquals`, `assertTrue`) y las verificaciones de comportamiento de Mockito (`verify(...)` para la notificación al vendedor), adaptado al entorno de Java 25.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
[Pega aquí la respuesta COMPLETA del LLM, sin editar, sin resumir.
Incluye TODO el texto, código, explicaciones que generó el LLM.

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]

# TDD Cycle: `ControlInventarioService`
## OpenLib Market — Seller / Core · Control de Inventario

---

## 1. Fase RED — Escribir las pruebas que fallan

> En esta fase escribimos **únicamente los tests**. Las clases `ControlInventarioService`, `Libro`, `EstadoLibro` y `NotificacionService` **no existen aún**, por lo que el proyecto **no compilará**. Eso es exactamente lo esperado: el compilador es nuestro primer "test en rojo".

```java
// src/test/java/com/openlib/seller/inventario/ControlInventarioServiceTest.java
package com.openlib.seller.inventario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

// ❌ ERROR DE COMPILACIÓN: ninguna de estas clases existe todavía.
@ExtendWith(MockitoExtension.class)
class ControlInventarioServiceTest {

    @Mock
    private NotificacionService notificacionService;   // ❌ no existe

    @InjectMocks
    private ControlInventarioService service;           // ❌ no existe

    private Libro libro;                                // ❌ no existe

    @BeforeEach
    void setUp() {
        // Libro con stock inicial de 5 unidades
        libro = new Libro("LIB-001", "Clean Code", 5); // ❌ no existe
    }

    // ── Escenario 1 ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("Dado stock > 0, al reducirlo a 0 el estado debe ser AGOTADO")
    void cuandoStockLlegaACero_entoncesEstadoEsAgotado() {
        service.reducirStock(libro, 5); // ❌ método no existe

        assertEquals(EstadoLibro.AGOTADO, libro.getEstado()); // ❌ enum no existe
    }

    // ── Escenario 2 ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("Al agotarse el stock, se debe notificar al vendedor")
    void cuandoStockLlegaACero_entoncesSeNotificaAlVendedor() {
        service.reducirStock(libro, 5); // ❌ método no existe

        // Mockito verifica que el servicio de notificación fue invocado
        verify(notificacionService).notificarAgotamiento(libro); // ❌ no existe
    }

    // ── Escenario 3 ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("Si el stock NO llega a cero, NO se debe notificar")
    void cuandoStockNoLlegaACero_entoncesNoSeNotifica() {
        service.reducirStock(libro, 2); // stock queda en 3

        verify(notificacionService, never()).notificarAgotamiento(libro);
    }

    // ── Escenario 4 ─────────────────────────────────────────────────────────
    @Test
    @DisplayName("Al reponer stock > 0 desde AGOTADO, el estado debe ser DISPONIBLE")
    void cuandoSeReponelStock_entoncesEstadoEsDisponible() {
        service.reducirStock(libro, 5);       // primero lo agotamos
        service.reponerStock(libro, 10);      // ❌ método no existe

        assertEquals(EstadoLibro.DISPONIBLE, libro.getEstado());
    }

    // ── Escenario 5 (guardia de frontera) ───────────────────────────────────
    @Test
    @DisplayName("Reponer con cantidad 0 no debe cambiar el estado")
    void cuandoSeReponeCantidadCero_entoncesEstadoNoCambia() {
        service.reducirStock(libro, 5);  // AGOTADO
        service.reponerStock(libro, 0);  // cantidad inválida

        assertEquals(EstadoLibro.AGOTADO, libro.getEstado()); // sigue AGOTADO
    }
}
```

**¿Por qué está en ROJO?** El compilador lanza `cannot find symbol` para `ControlInventarioService`, `NotificacionService`, `Libro` y `EstadoLibro`. Los tests guían exactamente **qué** tenemos que construir.

---

## 2. Fase GREEN — Implementación mínima para pasar los tests

> El objetivo aquí es **solo hacer que los tests compilen y pasen**. No se busca elegancia, se busca verde en el reporte de JUnit.

### `EstadoLibro.java`
```java
// src/main/java/com/openlib/seller/inventario/EstadoLibro.java
package com.openlib.seller.inventario;

public enum EstadoLibro {
    DISPONIBLE,
    AGOTADO
}
```

### `Libro.java`
```java
// src/main/java/com/openlib/seller/inventario/Libro.java
package com.openlib.seller.inventario;

public class Libro {

    private String id;
    private String titulo;
    private int    stock;
    private EstadoLibro estado;

    public Libro(String id, String titulo, int stockInicial) {
        this.id     = id;
        this.titulo = titulo;
        this.stock  = stockInicial;
        this.estado = EstadoLibro.DISPONIBLE; // estado inicial siempre DISPONIBLE
    }

    // ── Getters y setters mínimos ────────────────────────────────────────────
    public String      getId()     { return id; }
    public String      getTitulo() { return titulo; }
    public int         getStock()  { return stock; }
    public EstadoLibro getEstado() { return estado; }

    public void setStock(int stock)   { this.stock  = stock; }
    public void setEstado(EstadoLibro estado) { this.estado = estado; }
}
```

### `NotificacionService.java`
```java
// src/main/java/com/openlib/seller/inventario/NotificacionService.java
package com.openlib.seller.inventario;

public interface NotificacionService {
    void notificarAgotamiento(Libro libro);
}
```

### `ControlInventarioService.java` — versión GREEN (mínima)
```java
// src/main/java/com/openlib/seller/inventario/ControlInventarioService.java
package com.openlib.seller.inventario;

public class ControlInventarioService {

    private final NotificacionService notificacionService;

    public ControlInventarioService(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    public void reducirStock(Libro libro, int cantidad) {
        int nuevoStock = libro.getStock() - cantidad;
        libro.setStock(nuevoStock);

        // Lógica inline: si llegó a cero → AGOTADO + notificar
        if (libro.getStock() == 0) {
            libro.setEstado(EstadoLibro.AGOTADO);
            notificacionService.notificarAgotamiento(libro);
        }
    }

    public void reponerStock(Libro libro, int cantidad) {
        if (cantidad > 0) {
            libro.setStock(libro.getStock() + cantidad);
            libro.setEstado(EstadoLibro.DISPONIBLE);
        }
    }
}
```

✅ **Resultado:** Los 5 tests pasan. El reporte de JUnit muestra todo en verde.

---

## 3. Fase REFACTOR — Diseño limpio sin romper los tests

> Los tests siguen siendo exactamente los mismos. Solo cambia el **cómo** está organizado el código de producción.

### Mejoras aplicadas

| Principio | Problema en GREEN | Solución en REFACTOR |
|---|---|---|
| **SRP** (Single Responsibility) | `ControlInventarioService` decide el estado *y* notifica | Se extrae la transición de estado a `Libro` (el objeto que *conoce* su propio estado) |
| **Encapsulación** | `Libro` expone `setStock` y `setEstado` públicos; cualquiera puede corromper el estado | Se eliminan los setters públicos; el estado solo cambia mediante métodos de dominio |
| **Tell, Don't Ask** | El servicio pregunta `getStock() == 0` y luego muta el objeto | El servicio le *dice* al libro qué ocurrió; el libro reacciona internamente |
| **Inmutabilidad parcial** | `id` y `titulo` son mutables sin necesidad | Se eliminan sus setters (nunca se necesitaban) |

---

### `Libro.java` — versión REFACTOR (con lógica de dominio encapsulada)

```java
// src/main/java/com/openlib/seller/inventario/Libro.java
package com.openlib.seller.inventario;

public class Libro {

    private final String id;
    private final String titulo;
    private int         stock;
    private EstadoLibro estado;

    public Libro(String id, String titulo, int stockInicial) {
        this.id     = id;
        this.titulo = titulo;
        this.stock  = stockInicial;
        this.estado = EstadoLibro.DISPONIBLE;
    }

    // ── Consultas (solo lectura) ─────────────────────────────────────────────
    public String      getId()        { return id; }
    public String      getTitulo()    { return titulo; }
    public int         getStock()     { return stock; }
    public EstadoLibro getEstado()    { return estado; }
    public boolean     estaAgotado()  { return estado == EstadoLibro.AGOTADO; }

    // ── Comandos de dominio (el libro gestiona su propio estado) ─────────────

    /**
     * Descuenta unidades. Devuelve {@code true} si la operación
     * provocó el agotamiento, permitiendo al servicio reaccionar.
     */
    public boolean descontarStock(int cantidad) {
        this.stock = this.stock - cantidad;
        if (this.stock == 0) {
            this.estado = EstadoLibro.AGOTADO;
            return true; // evento: se agotó en esta operación
        }
        return false;
    }

    /**
     * Repone unidades. Solo actúa si la cantidad es positiva.
     */
    public void reponerStock(int cantidad) {
        if (cantidad > 0) {
            this.stock += cantidad;
            this.estado = EstadoLibro.DISPONIBLE;
        }
    }
}
```

### `ControlInventarioService.java` — versión REFACTOR (orquestador delgado)

```java
// src/main/java/com/openlib/seller/inventario/ControlInventarioService.java
package com.openlib.seller.inventario;

public class ControlInventarioService {

    private final NotificacionService notificacionService;

    public ControlInventarioService(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    /**
     * Reduce el stock del libro y, si se agota, dispara la notificación.
     * El servicio orquesta; el libro gestiona su propio estado.
     */
    public void reducirStock(Libro libro, int cantidad) {
        boolean seAgoto = libro.descontarStock(cantidad);
        if (seAgoto) {
            notificacionService.notificarAgotamiento(libro);
        }
    }

    /**
     * Repone stock delegando completamente en la lógica del dominio.
     */
    public void reponerStock(Libro libro, int cantidad) {
        libro.reponerStock(cantidad);
    }
}
```

### `NotificacionService.java` — sin cambios (ya era correcto)

```java
// src/main/java/com/openlib/seller/inventario/NotificacionService.java
package com.openlib.seller.inventario;

public interface NotificacionService {
    void notificarAgotamiento(Libro libro);
}
```

✅ **Los 5 tests siguen en verde.** El comportamiento es idéntico; la estructura es más mantenible.

---

## Resumen del ciclo

```
RED  ──► Pruebas escritas, proyecto no compila.
          Los tests definen el contrato del sistema.

GREEN ──► Implementación mínima (setters públicos,
          lógica inline en el servicio). Todo pasa.

REFACTOR ► Encapsulación en Libro (Tell, Don't Ask).
           Servicio delgado como orquestador (SRP).
           Setters eliminados. Tests: sin tocar, verde.
```

El punto clave de TDD no es escribir tests después del código, sino dejar que **los tests dicten el diseño**: cada fallo en RED es una especificación ejecutable de lo que el sistema debe hacer.


---

#### Análisis crítico de la respuesta

#### 1. ¿Realmente hizo TDD o me esta tomando el pelo?
No, no hizo TDD real. Un LLM genera todo de un solo viaje mental, asi que vio la implementacion antes de escribir los tests. Lo que hizo fue simular el proceso narrativamente, que tiene valor didactico, pero no es lo mismo que correr los tests, ver el rojo y ahi si escribir codigo.

#### 2. ¿Los tests cubren todo lo que deberian?
Mas o menos. Los casos felices estan bien pero faltan edge cases obvios como stock negativo reducir en un libro ya agotado, esos huecos son exactamente los que TDD real descubre solo.

#### 3. ¿El refactor sirvio de algo o fue puro cosmetico?
Sirvio. Mover la logica al propio Libro, los setters publicos desaparecieron y el servicio quedo delgado. Lo clave es que los 5 tests originales no se tocaron, que es exactamente la promesa del refactor.

#### 4. ¿Como cacharia que el LLM esta simulando y no aplicando TDD?
Solo tocaria pegar los tests en un proyecto vacio y confirma que no compila. Luego agrega la Fase GREEN y verifica verde. Si en algun paso los tests referencian clases que "magicamente" ya existen, hay esta. Otra senal es que los tests esten escritos a la medida exacta de la implementacion, sin ningun caso que esta no maneje.

#### 5. ¿Que estuvo bien en el prompt?
Definio el stack concreto, dio reglas de negocio claras, exigi el ciclo RED/GREEN/REFACTOR explicito y especifico las herramientas exactas, eso elimino casi todo el espacio para respuestas genericas.

#### 6. ¿Que le falta al prompt?
Tres cosas, pedir edge cases explicitamente, pedir que liste los errores de compilacion esperados en RED para forzar honestidad, y pedir que el refactor incluya una comparacion de dependencias antes despues para razonar el diseno de verdad.
