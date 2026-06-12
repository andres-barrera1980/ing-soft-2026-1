## P03_solid_jerarquia

### Estudiante
- **Nombre completo**: [Alejandro Macias Barrios]

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude] |
| **Modelo específico** | [Claude Sonnet 4.6] |
| **¿Por qué elegiste este LLM?** | [Claude ha demostrado tener un buen rendimiento en tareas de diseño y refactorización de código, así como también en la aplicación de principios de ingeniería de software. Además, ha sido uno de los que mejor hemos usado en clase, y considero que es uno de los modelos más avanzados disponibles.] |
---

### Prompt utilizado
```
[Imagina que eres un estudiante de Ingeniería de sistemas de la Pontificia Universidad Javeriana cursando la materia de Fundamentos de Ingeniería de Software. El profesor te ha hecho la siguiente pregunta y debes responderla de la mejor manera posible.
OpenLib Market tiene tres tipos de usuarios. El equipo modeló esta jerarquía:

```java
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
Analice esta jerarquía señalando qué principios SOLID se violan y proponga un rediseño completo.]
```

### Respuesta del LLM
[Principios SOLID violados
1. Interface Segregation Principle (ISP) — violación principal
La interfaz Usuario es una "fat interface": obliga a todas las clases a implementar métodos que no les corresponden. Comprador lanza UnsupportedOperationException en cuatro de sus cinco métodos. Esto es exactamente lo que ISP prohíbe: ningún cliente debería depender de métodos que no usa.
2. Liskov Substitution Principle (LSP)
Este es el más peligroso en tiempo de ejecución. Si un servicio recibe un Usuario y llama vender(), no puede saber si explotará o no sin inspeccionar el tipo concreto. La sustitución está rota: un Comprador no puede reemplazar a un Usuario sin alterar el comportamiento esperado del programa.
3. Single Responsibility Principle (SRP)
La interfaz mezcla responsabilidades de dominios distintos: operaciones comerciales (comprar, vender), moderación de contenido y administración del sistema. Son tres razones de cambio diferentes conviviendo en un solo contrato.
Nota sobre la herencia: Vendedor extends Comprador y Administrador extends Vendedor modelan roles como si fueran tipos, usando herencia de implementación para reutilizar código. Esto acopla innecesariamente los roles y es el origen estructural de las violaciones de LSP.

Rediseño completo: ISP + composición de roles
La solución es segregar las interfaces por capacidad y componer los usuarios a partir de los roles que realmente ejercen.

// ============================================================
// 1. INTERFACES SEGREGADAS — un contrato por capacidad
// ============================================================

public interface Comprable {
    void comprar(Libro libro);
}

public interface Vendible {
    void vender(Libro libro);
    void generarReporteVentas();
}

public interface Moderable {
    void moderarComentario(Comentario comentario);
}

public interface Administrable {
    void gestionarUsuarios();
}

// ============================================================
// 2. IMPLEMENTACIONES DE ROL — cada una con SRP limpio
// ============================================================

// Lógica de compra reutilizable como componente independiente
public class RolComprador implements Comprable {
    private final ServicioCompras servicioCompras;

    public RolComprador(ServicioCompras servicioCompras) {
        this.servicioCompras = servicioCompras;
    }

    @Override
    public void comprar(Libro libro) {
        servicioCompras.procesarCompra(libro);
    }
}

public class RolVendedor implements Vendible {
    private final ServicioVentas servicioVentas;

    public RolVendedor(ServicioVentas servicioVentas) {
        this.servicioVentas = servicioVentas;
    }

    @Override
    public void vender(Libro libro) {
        servicioVentas.publicarLibro(libro);
    }

    @Override
    public void generarReporteVentas() {
        servicioVentas.generarReporte();
    }
}

public class RolModerador implements Moderable {
    private final ServicioModeracion servicioModeracion;

    public RolModerador(ServicioModeracion servicioModeracion) {
        this.servicioMod = servicioModeracion;
    }

    @Override
    public void moderarComentario(Comentario comentario) {
        servicioModeracion.evaluar(comentario);
    }
}

public class RolAdministrador implements Administrable {
    private final ServicioUsuarios servicioUsuarios;

    public RolAdministrador(ServicioUsuarios servicioUsuarios) {
        this.servicioUsuarios = servicioUsuarios;
    }

    @Override
    public void gestionarUsuarios() {
        servicioUsuarios.administrar();
    }
}

// ============================================================
// 3. USUARIOS CONCRETOS — compuestos solo con lo que necesitan
// ============================================================

// Comprador: solo puede comprar. Sin métodos que lanzar.
public class Comprador implements Comprable {

    private final RolComprador rolComprador;

    public Comprador(ServicioCompras servicioCompras) {
        this.rolComprador = new RolComprador(servicioCompras);
    }

    @Override
    public void comprar(Libro libro) {
        rolComprador.comprar(libro);
    }
}

// Vendedor: solo puede vender y generar reportes.
public class Vendedor implements Vendible {

    private final RolVendedor rolVendedor;

    public Vendedor(ServicioVentas servicioVentas) {
        this.rolVendedor = new RolVendedor(servicioVentas);
    }

    @Override
    public void vender(Libro libro) {
        rolVendedor.vender(libro);
    }

    @Override
    public void generarReporteVentas() {
        rolVendedor.generarReporteVentas();
    }
}

// Administrador: tiene todos los roles. Composición explícita.
public class Administrador implements Comprable, Vendible, Moderable, Administrable {

    private final RolComprador rolComprador;
    private final RolVendedor rolVendedor;
    private final RolModerador rolModerador;
    private final RolAdministrador rolAdministrador;

    public Administrador(ServicioCompras sc, ServicioVentas sv,
                         ServicioModeracion sm, ServicioUsuarios su) {
        this.rolComprador     = new RolComprador(sc);
        this.rolVendedor      = new RolVendedor(sv);
        this.rolModerador     = new RolModerador(sm);
        this.rolAdministrador = new RolAdministrador(su);
    }

    @Override public void comprar(Libro libro)               { rolComprador.comprar(libro); }
    @Override public void vender(Libro libro)                { rolVendedor.vender(libro); }
    @Override public void generarReporteVentas()             { rolVendedor.generarReporteVentas(); }
    @Override public void moderarComentario(Comentario c)    { rolModerador.moderarComentario(c); }
    @Override public void gestionarUsuarios()                { rolAdministrador.gestionarUsuarios(); }
}

Por qué la compisición es mayor a la herencia?
// ❌ Herencia original: los roles se "heredan" aunque no correspondan
Administrador → Vendedor → Comprador
                               ↑
               Comprador lanza excepciones en 4/5 métodos

// ✅ Composición: cada usuario declara exactamente sus capacidades
Comprador    → [Comprable]
Vendedor     → [Vendible]
Administrador→ [Comprable + Vendible + Moderable + Administrable]
El compilador ahora garantiza las capacidades en tiempo de compilación. Un servicio que recibe Vendible sabe con certeza que puede llamar vender() sin riesgo de excepción.

Principio guía del rediseño: "Prefiere composición sobre herencia" — cuando la herencia modela roles accidentales en lugar de tipos genuinos, la composición es siempre la herramienta correcta.]

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[El prompt estableció un rol de estudiante de ingeniería de software y proporcionó el contexto del problema. Dio un ejemplo claro de cómo estaba implementado inicialmente y pidió explícitamente que se señalaran los principios violados y un rediseño completo. Además proporcionó una imagen con una explicación clara de los principios.]

#### 2. ¿Qué se puede mejorar?

[Yo creo que el prompt podria haber sido un poco mas especifico en cuanto a lo que queria, por ejemplo, podria haber pedido que se señalaran los principios violados en orden de importancia, o que se proporcionara un ejemplo de como se podria implementar el rediseño. Por otro lado, no pidio que se explicara por que se debia cambiar el diseño, solo pidio que se mostrara un rediseño. Adicionalmente, el prompt no estableció un formato de salida, lo que hace que sea mas dificil entender lo que se queria.]


#### 3. Respuesta final
[La jerarquía original presenta principalmente violaciones de ISP, LSP y SRP, además de dificultades para cumplir adecuadamente el OCP. El error fundamental es modelar los usuarios mediante una cadena de herencia (Administrador → Vendedor → Comprador) cuando en realidad se trata de roles o capacidades independientes.
La solución correcta consiste en segregar las interfaces, eliminar los métodos que lanzan UnsupportedOperationException y reemplazar la herencia por composición de roles, aplicando el principio de diseño ampliamente aceptado.

Los principios violados fueron los siguientes:
1. Interface Segregation Principle ya que Usuario es una interfaz de software que contiene demasiados métodos y responsabilidades agrupados, es decir que obliga a todas las clases a implementar métodos que no les corresponden. Comprador lanza UnsupportedOperationException en cuatro de sus cinco métodos. Esto es exactamente lo que ISP prohíbe, ningún cliente debería depender de métodos que no usa.
2. Liskov Substitution Principle ya que si un servicio recibe un Usuario y llama vender(), no puede saber si se ejecutara o no sin inspeccionar el tipo concreto. La sustitución está rota, un Comprador no puede reemplazar a un Usuario sin alterar el comportamiento esperado del programa.
3. Single Responsibility Principle ya que la interfaz mezcla responsabilidades de dominios distintos, es decir operaciones comerciales (comprar, vender), moderación de contenido y administración del sistema. Son tres razones de cambio diferentes conviviendo en un solo contrato.]