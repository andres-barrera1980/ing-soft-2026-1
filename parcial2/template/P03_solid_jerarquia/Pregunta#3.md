# Plantilla de entrega — Parcial 2
---

## Pregunta [03]: P03_solid_jerarquia

### Estudiante
- **Nombre completo**: Samuel López Gómez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** |Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | Para esta pregunta la Ia dijo: la jerarquía de clases es un desastre clásico de diseño orientado a objetos. Te sugiero usar GPT-4o o Claude 3.5 Sonnet, ambos destrozarán este mal diseño fácilmente. |

---

### Prompt utilizado

Actúa como un arquitecto de software senior revisando el modelo de usuarios del proyecto OpenLib Market (entorno Java 21). Adjunto una jerarquía de clases diseñada por el equipo para representar los roles del sistema.

Necesito que:
1. Identifiques y expliques detalladamente qué principios SOLID se están violando en esta jerarquía (debes detectar al menos dos principios clave).
2. Expliques cuáles son las consecuencias de estas violaciones en tiempo de ejecución (ej. qué pasa si intentamos usar polimorfismo).
3. Propongas un rediseño completo de esta estructura que corrija los problemas encontrados.
4. Escribas el código Java resultante del rediseño.

Restricciones:
- Soluciona el problema del "fat interface" (interfaz gorda).
- Evita el abuso de herencia; el diseño debe permitir de forma natural que los roles estén separados o se puedan combinar.
- Usa POO pura y características modernas de Java 21.

Formato de salida esperado: Markdown con las secciones: "Diagnóstico de Violaciones SOLID", "Consecuencias en Tiempo de Ejecución", "Estrategia de Rediseño", y "Código Refactorizado".

Código a analizar:
public interface Usuario {
    void comprar(Libro libro);
    void vender(Libro libro);
    void moderarComentario(Comentario comentario);
    void generarReporteVentas();
    void gestionarUsuarios();
}

public class Comprador implements Usuario {
    public void comprar(Libro libro) { /* implementación */ }
    public void vender(Libro libro) { throw new UnsupportedOperationException("Un comprador no puede vender"); }
    public void moderarComentario(Comentario c) { throw new UnsupportedOperationException("No autorizado"); }
    public void generarReporteVentas() { throw new UnsupportedOperationException("No aplica"); }
    public void gestionarUsuarios() { throw new UnsupportedOperationException("No autorizado"); }
}

public class Vendedor extends Comprador {
    @Override
    public void comprar(Libro libro) { throw new UnsupportedOperationException("Un vendedor no compra"); }
    @Override
    public void vender(Libro libro) { /* implementación */ }
}

public class Administrador extends Vendedor {
    @Override
    public void comprar(Libro libro) { /* un admin sí puede comprar */ }
    // hereda vender() de Vendedor, implementa el resto
}

```

---

### Respuesta del LLM
# Análisis SOLID — Jerarquía de Usuarios
> OpenLib Market · Modelo de Roles · Revisión arquitectónica · Java 21

---

## Diagnóstico de Violaciones SOLID

### Violación 1: Interface Segregation Principle (ISP) — la más grave

El ISP establece que *ningún cliente debería verse forzado a depender de métodos que no usa*.

La interfaz `Usuario` es un **fat interface** clásico: agrupa capacidades de tres roles
completamente distintos en un único contrato:

| Método | ¿Quién lo necesita realmente? |
|---|---|
| `comprar(Libro)` | Solo Comprador |
| `vender(Libro)` | Solo Vendedor |
| `moderarComentario(Comentario)` | Solo Administrador |
| `generarReporteVentas()` | Solo Administrador |
| `gestionarUsuarios()` | Solo Administrador |

El resultado es que `Comprador` debe declarar **cuatro métodos** que no le corresponden,
lanzando `UnsupportedOperationException` en cada uno. Esto no es una implementación:
es ruido estructural que comunica lo contrario de lo que el diseño debería expresar.

---

### Violación 2: Liskov Substitution Principle (LSP) — la más peligrosa en runtime

El LSP establece que *los objetos de una subclase deben poder sustituir a los de su
superclase sin alterar la corrección del programa*.

La jerarquía actual rompe LSP en dos puntos concretos:

**Punto A — `Vendedor extends Comprador`:**
```java
// Comprador puede comprar. Vendedor extiende Comprador pero lanza excepción:
public void comprar(Libro libro) {
    throw new UnsupportedOperationException("Un vendedor no compra");
}
```
`Vendedor` **debilita** la postcondición de `comprar()` que `Comprador` garantiza.
Cualquier código que reciba un `Comprador` y llame `comprar()` puede explotar
si recibe un `Vendedor` en su lugar.

**Punto B — `Administrador extends Vendedor`:**
`Administrador` reintroduce `comprar()` que `Vendedor` había roto. La jerarquía
de herencia se convierte en un zigzag de capacidades que aparecen, desaparecen
y reaparecen, lo que hace imposible razonar sobre el comportamiento de la clase
conociendo solo su tipo estático.

---

### Violación 3: Single Responsibility Principle (SRP) — agravante

`Comprador` tiene dos responsabilidades simultáneas e incompatibles:
1. Implementar su comportamiento real (`comprar`).
2. Ser la clase base de toda la jerarquía, forzando a heredar métodos ajenos.

`Vendedor` y `Administrador` heredan código que no deben usar y que activamente
perjudica la corrección del sistema.

---

## Consecuencias en Tiempo de Ejecución

### 1. Errores silenciosos en código polimórfico

```java
// Código perfectamente válido en tiempo de compilación:
List<Usuario> usuarios = List.of(new Comprador(), new Vendedor(), new Administrador());

for (Usuario u : usuarios) {
    u.comprar(libro); // BOOM en runtime: Vendedor lanza UnsupportedOperationException
}
```
El compilador no detecta nada. El error aparece en producción, con el primer
`Vendedor` que entre a esa lista.

### 2. Imposibilidad de usar `instanceof` con confianza

```java
if (usuario instanceof Comprador c) {
    c.comprar(libro); // Vendedor también es instanceof Comprador → BOOM
}
```
Como `Vendedor extends Comprador`, el pattern matching de Java 21 no sirve
para distinguir capacidades reales; hay que añadir checks adicionales que
anulan cualquier ventaja del polimorfismo.

### 3. Tests frágiles y engañosos

```java
@Test
void compradorDebePoderComprar() {
    Usuario u = new Vendedor(); // válido en compilación
    u.comprar(libro);           // falla en runtime, no en el assert
}
```
Los tests no fallan donde deben: fallan en el setup, lo que complica
el diagnóstico y da falsos negativos.

### 4. Imposibilidad de roles combinados

Un usuario que es comprador **y** vendedor simultáneamente (marketplace
bidireccional) no puede modelarse con esta jerarquía de herencia simple
sin duplicar código o forzar herencia múltiple.

---

## Estrategia de Rediseño

### Principio guía: **Composición sobre herencia + segregación de interfaces**

En lugar de una jerarquía vertical `Comprador → Vendedor → Administrador`,
el rediseño usa una **arquitectura horizontal** basada en capacidades:

```
Capacidad          Interfaz segregada
──────────────     ──────────────────────
Comprar libros  →  PuedeComprar
Vender libros   →  PuedeVender
Moderar         →  PuedeModerarContenido
Admin usuarios  →  PuedeAdministrar
Reportes        →  PuedeGenerarReportes
```

Cada clase de usuario implementa **solo las interfaces** que corresponden
a sus capacidades reales. Un usuario con múltiples roles simplemente
implementa múltiples interfaces: no hay herencia forzada.

Adicionalmente, se usa un `record` para los datos de identidad del usuario
(alineado con el modelo `User` real del proyecto), separando los datos
de las capacidades de comportamiento.

### ¿Por qué no herencia?

| Criterio | Herencia | Composición + interfaces |
|---|---|---|
| Agregar rol "Moderador" | Modificar jerarquía | Nueva clase, nuevas interfaces |
| Usuario con dos roles | Imposible sin hack | Implementar dos interfaces |
| LSP | Se rompe con UnsupportedOperationException | Imposible de romper |
| Testabilidad | Heredas todo, mockeas todo | Mockeas solo lo necesario |

---

## Código Refactorizado

### Interfaces segregadas por capacidad

```java
package com.openlib.model.rol;

import com.openlib.model.Book;

/**
 * Capacidad: adquirir libros del catálogo.
 * Solo implementada por quienes realmente pueden comprar.
 */
public interface PuedeComprar {
    void comprar(Book libro);
    java.util.List<Book> verHistorialCompras();
}
```

```java
package com.openlib.model.rol;

import com.openlib.model.Book;

/**
 * Capacidad: publicar y gestionar libros propios a la venta.
 * Solo implementada por quienes realmente pueden vender.
 */
public interface PuedeVender {
    void publicarLibro(Book libro);
    void retirarLibro(Book libro);
    java.util.List<Book> verMisPublicaciones();
}
```

```java
package com.openlib.model.rol;

/**
 * Representa un comentario en el sistema.
 * Record inmutable — Java 16+.
 */
public record Comentario(Long id, String contenido, String autorEmail) {}
```

```java
package com.openlib.model.rol;

/**
 * Capacidad: revisar y moderar contenido generado por usuarios.
 * Solo implementada por roles con permisos de moderación.
 */
public interface PuedeModerarContenido {
    void moderarComentario(Comentario comentario);
    void ocultarResena(Long resenaId);
}
```

```java
package com.openlib.model.rol;

/**
 * Capacidad: gestionar el ciclo de vida de otros usuarios.
 * Solo implementada por administradores del sistema.
 */
public interface PuedeAdministrar {
    void suspenderUsuario(Long usuarioId);
    void rehabilitarUsuario(Long usuarioId);
    java.util.List<UsuarioResumen> listarUsuarios();
}
```

```java
package com.openlib.model.rol;

/**
 * Capacidad: generar reportes de actividad comercial.
 * Solo implementada por roles con acceso a datos de negocio.
 */
public interface PuedeGenerarReportes {
    ReporteVentas generarReporteVentas(java.time.LocalDate desde, java.time.LocalDate hasta);
}
```

```java
package com.openlib.model.rol;

// Tipos auxiliares como records (Java 16+)
public record UsuarioResumen(Long id, String nombre, String email, String rol) {}
public record ReporteVentas(int totalVentas, double montoTotal, java.time.LocalDate desde, java.time.LocalDate hasta) {}
```

---

### Identidad base del usuario

```java
package com.openlib.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad JPA sin cambios respecto al modelo existente del proyecto.
 * Los datos de identidad (nombre, email, rol) viven aquí.
 * Las CAPACIDADES de comportamiento se modelan en clases de dominio separadas.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;
    private String role; // "BUYER" | "ADMIN" | "SELLER"
    private String address;
}
```

---

### Implementaciones de roles

#### `Comprador` — solo implementa lo que le corresponde

```java
package com.openlib.model.rol;

import com.openlib.model.Book;
import com.openlib.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rol Comprador: puede adquirir libros y ver su historial.
 * NO implementa PuedeVender, PuedeModerarContenido, ni PuedeAdministrar.
 * El compilador garantiza que nunca podrá llamar esos métodos.
 *
 * LSP se cumple: cualquier PuedeComprar puede sustituirse por Comprador
 * sin ninguna sorpresa en runtime.
 */
public class Comprador implements PuedeComprar {

    private final User usuario;
    private final List<Book> historialCompras = new ArrayList<>();

    public Comprador(User usuario) {
        this.usuario = usuario;
    }

    @Override
    public void comprar(Book libro) {
        // Lógica real: verificar fondos, procesar pago, registrar compra
        historialCompras.add(libro);
        System.out.printf("✅ %s compró '%s'%n", usuario.getName(), libro.getTitle());
    }

    @Override
    public List<Book> verHistorialCompras() {
        return Collections.unmodifiableList(historialCompras);
    }

    public User getUsuario() { return usuario; }
}
```

---

#### `Vendedor` — solo implementa lo que le corresponde

```java
package com.openlib.model.rol;

import com.openlib.model.Book;
import com.openlib.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rol Vendedor: puede publicar y retirar sus propios libros.
 * NO hereda de Comprador — no tiene nada que ver con comprar.
 * NO lanza UnsupportedOperationException en ningún método.
 */
public class Vendedor implements PuedeVender {

    private final User usuario;
    private final List<Book> publicaciones = new ArrayList<>();

    public Vendedor(User usuario) {
        this.usuario = usuario;
    }

    @Override
    public void publicarLibro(Book libro) {
        libro.setStatus("APROBADO");
        publicaciones.add(libro);
        System.out.printf("📚 %s publicó '%s'%n", usuario.getName(), libro.getTitle());
    }

    @Override
    public void retirarLibro(Book libro) {
        publicaciones.remove(libro);
        System.out.printf("🗑️ %s retiró '%s'%n", usuario.getName(), libro.getTitle());
    }

    @Override
    public List<Book> verMisPublicaciones() {
        return Collections.unmodifiableList(publicaciones);
    }

    public User getUsuario() { return usuario; }
}
```

---

#### `Administrador` — composición de capacidades múltiples

```java
package com.openlib.model.rol;

import com.openlib.model.Book;
import com.openlib.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Rol Administrador: tiene capacidades de comprar, moderar, administrar
 * y generar reportes.
 *
 * Implementa SOLO las interfaces que corresponden a su rol.
 * NO hereda de Comprador ni de Vendedor.
 * Cada capacidad es explícita y documentada en la firma de la clase.
 *
 * Para un Administrador que también pueda vender, solo se agrega
 * "implements PuedeVender" — sin tocar ninguna otra clase.
 */
public class Administrador implements PuedeComprar, PuedeModerarContenido,
                                       PuedeAdministrar, PuedeGenerarReportes {

    private final User usuario;
    private final List<Book> historialCompras = new ArrayList<>();
    private final List<User> usuariosGestionados; // inyectado o accedido via repo

    public Administrador(User usuario, List<User> usuariosGestionados) {
        this.usuario = usuario;
        this.usuariosGestionados = usuariosGestionados;
    }

    // ── PuedeComprar ──────────────────────────────────────────────────────────

    @Override
    public void comprar(Book libro) {
        historialCompras.add(libro);
        System.out.printf("🛒 Admin %s compró '%s'%n", usuario.getName(), libro.getTitle());
    }

    @Override
    public List<Book> verHistorialCompras() {
        return List.copyOf(historialCompras);
    }

    // ── PuedeModerarContenido ─────────────────────────────────────────────────

    @Override
    public void moderarComentario(Comentario comentario) {
        System.out.printf("🔨 Admin moderó comentario id=%d de '%s'%n",
                comentario.id(), comentario.autorEmail());
    }

    @Override
    public void ocultarResena(Long resenaId) {
        System.out.printf("🙈 Admin ocultó reseña id=%d%n", resenaId);
    }

    // ── PuedeAdministrar ──────────────────────────────────────────────────────

    @Override
    public void suspenderUsuario(Long usuarioId) {
        System.out.printf("🚫 Admin suspendió usuario id=%d%n", usuarioId);
    }

    @Override
    public void rehabilitarUsuario(Long usuarioId) {
        System.out.printf("✅ Admin rehabilitó usuario id=%d%n", usuarioId);
    }

    @Override
    public List<UsuarioResumen> listarUsuarios() {
        return usuariosGestionados.stream()
                .map(u -> new UsuarioResumen(u.getId(), u.getName(), u.getEmail(), u.getRole()))
                .toList(); // Java 16+
    }

    // ── PuedeGenerarReportes ──────────────────────────────────────────────────

    @Override
    public ReporteVentas generarReporteVentas(LocalDate desde, LocalDate hasta) {
        // En producción consultaría el repositorio de órdenes
        return new ReporteVentas(0, 0.0, desde, hasta);
    }

    public User getUsuario() { return usuario; }
}
```

---

#### `UsuarioDual` — roles combinados sin herencia múltiple

```java
package com.openlib.model.rol;

import com.openlib.model.Book;
import com.openlib.model.User;

import java.util.List;

/**
 * Caso real de marketplace: un usuario que puede comprar Y vender
 * simultáneamente (ej. un intercambiador de libros de segunda mano).
 *
 * Con la jerarquía original esto era imposible sin hacks.
 * Con composición de interfaces: trivial.
 *
 * Delega la implementación a Comprador y Vendedor mediante composición,
 * evitando duplicación de código.
 */
public class UsuarioDual implements PuedeComprar, PuedeVender {

    private final Comprador capacidadCompra;
    private final Vendedor  capacidadVenta;

    public UsuarioDual(User usuario) {
        this.capacidadCompra = new Comprador(usuario);
        this.capacidadVenta  = new Vendedor(usuario);
    }

    // ── PuedeComprar (delegado) ───────────────────────────────────────────────

    @Override
    public void comprar(Book libro) {
        capacidadCompra.comprar(libro);
    }

    @Override
    public List<Book> verHistorialCompras() {
        return capacidadCompra.verHistorialCompras();
    }

    // ── PuedeVender (delegado) ────────────────────────────────────────────────

    @Override
    public void publicarLibro(Book libro) {
        capacidadVenta.publicarLibro(libro);
    }

    @Override
    public void retirarLibro(Book libro) {
        capacidadVenta.retirarLibro(libro);
    }

    @Override
    public List<Book> verMisPublicaciones() {
        return capacidadVenta.verMisPublicaciones();
    }
}
```

---

### Verificación: polimorfismo correcto con pattern matching (Java 21)

```java
package com.openlib.service;

import com.openlib.model.Book;
import com.openlib.model.rol.*;

/**
 * Demuestra cómo el código cliente trabaja de forma segura con el nuevo diseño.
 */
public class GestorAcciones {

    /**
     * Procesa una compra de forma polimórfica y SEGURA.
     * Solo los usuarios que realmente implementan PuedeComprar llegan aquí.
     * UnsupportedOperationException es físicamente imposible.
     */
    public void procesarCompra(PuedeComprar comprador, Book libro) {
        comprador.comprar(libro); // garantizado sin excepción por el contrato
    }

    /**
     * Pattern matching de Java 21 para despacho por tipo de capacidad.
     * Cada rama solo accede a métodos que el tipo garantiza.
     */
    public void ejecutarAccion(Object usuario, Book libro) {
        switch (usuario) {
            case PuedeComprar c  -> c.comprar(libro);
            case PuedeVender  v  -> v.publicarLibro(libro);
            case PuedeAdministrar a -> a.listarUsuarios()
                    .forEach(u -> System.out.println(u.nombre()));
            default -> throw new IllegalArgumentException(
                "Rol sin capacidad definida para esta acción"
            );
        }
    }
}
```

---

### Test: LSP ahora es verificable en compilación, no en runtime

```java
class JerarquiaUsuariosTest {

    @Test
    void compradorSiempreCompra_sinExcepcion() {
        var user = User.builder().name("Ana").email("ana@test.com").build();
        PuedeComprar comprador = new Comprador(user);

        // Nunca puede ser un Vendedor. El compilador lo garantiza.
        assertDoesNotThrow(() -> comprador.comprar(libroFicticio()));
    }

    @Test
    void vendedorNoPuedeAparecerDondeSePideComprador() {
        var user = User.builder().name("Bob").email("bob@test.com").build();
        Vendedor vendedor = new Vendedor(user);

        // Esta línea NO COMPILA — Vendedor no implementa PuedeComprar.
        // El error que antes ocurría en runtime ahora ocurre en tiempo de compilación.
        // PuedeComprar c = vendedor; // ← Error de compilación ✓
        assertFalse(vendedor instanceof PuedeComprar);
    }

    @Test
    void usuarioDualPuedeComprarYVender() {
        var user = User.builder().name("Carlos").email("carlos@test.com").build();
        UsuarioDual dual = new UsuarioDual(user);

        assertDoesNotThrow(() -> dual.comprar(libroFicticio()));
        assertDoesNotThrow(() -> dual.publicarLibro(libroFicticio()));
    }

    @Test
    void administradorCumpleTodosLosContratos() {
        var user = User.builder().name("Admin").email("admin@test.com").build();
        Administrador admin = new Administrador(user, List.of());

        // Compila porque Administrador implementa todas estas interfaces
        PuedeComprar           comoComprador  = admin;
        PuedeModerarContenido  comoModerador  = admin;
        PuedeAdministrar       comoAdmin      = admin;
        PuedeGenerarReportes   comoReporter   = admin;

        assertDoesNotThrow(() -> comoComprador.comprar(libroFicticio()));
    }

    private Book libroFicticio() {
        return Book.builder().isbn("000-0").title("Test Book").author("Autor").price(10.0).build();
    }
}
```

---

### Estructura de paquetes resultante

```
com.openlib/
├── model/
│   ├── User.java                    ← entidad JPA, sin cambios
│   ├── Book.java                    ← entidad JPA, sin cambios
│   └── rol/
│       ├── PuedeComprar.java        ← interfaz segregada
│       ├── PuedeVender.java         ← interfaz segregada
│       ├── PuedeModerarContenido.java
│       ├── PuedeAdministrar.java
│       ├── PuedeGenerarReportes.java
│       ├── Comprador.java           ← implements PuedeComprar
│       ├── Vendedor.java            ← implements PuedeVender
│       ├── Administrador.java       ← implements 4 interfaces
│       ├── UsuarioDual.java         ← implements PuedeComprar + PuedeVender
│       ├── Comentario.java          ← record
│       ├── UsuarioResumen.java      ← record
│       └── ReporteVentas.java       ← record
└── service/
    └── GestorAcciones.java          ← usa pattern matching Java 21
```

---

### Resumen de mejoras

| Problema original | Solución aplicada |
|---|---|
| Fat interface `Usuario` con 5 métodos | 5 interfaces segregadas de 1-2 métodos cada una |
| LSP roto por `UnsupportedOperationException` | Imposible: el compilador impide asignar tipos incorrectos |
| `Vendedor extends Comprador` sin sentido semántico | Sin herencia entre roles; composición explícita |
| Roles combinados imposibles | `UsuarioDual implements PuedeComprar, PuedeVender` |
| Pattern matching forzaba doble check | `switch` sobre interfaces específicas, limpio y seguro |

```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?



#### 2. ¿Qué se puede mejorar?



#### 3. Respuesta final
