# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [3]: [solid_jerarquia]

### Estudiante
- **Nombre completo**: Boris Nicolas Valbuena Gueirsman

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Es la que más usé durante el curso, por lo cual es la misma que usaré en el parcial. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un Arquitecto de Software Senior especializado en Java, SOLID y patrones de diseño.

Analiza la siguiente jerarquía de clases:

public interface Usuario {
    void comprar(Libro libro);
    void vender(Libro libro);
    void moderarComentario(Comentario comentario);
    void generarReporteVentas();
    void gestionarUsuarios();
}
public class Comprador implements Usuario {
    public void comprar(Libro libro) { }
    public void vender(Libro libro) { throw new UnsupportedOperationException(); }
    public void moderarComentario(Comentario comentario) { throw new UnsupportedOperationException(); }
    public void generarReporteVentas() { throw new UnsupportedOperationException(); }
    public void gestionarUsuarios() { throw new UnsupportedOperationException(); }
}
public class Vendedor extends Comprador {
    @Override
    public void comprar(Libro libro) { throw new UnsupportedOperationException(); }
    @Override
    public void vender(Libro libro) { }
}
public class Administrador extends Vendedor {
    @Override
    public void comprar(Libro libro) { }
}

Identifica:

1. Qué principios SOLID se violan.
2. Qué problemas existen en la jerarquía de herencia.
3. Qué riesgos aparecen en tiempo de ejecución.
4. Cómo rediseñarías completamente la solución aplicando SOLID.
5. Propón interfaces y clases nuevas justificando cada decisión.

Entrega la respuesta en formato:

Principios violados

Problemas de diseño

Riesgos en ejecución

Rediseño propuesto

Código ejemplo

Beneficios del rediseño
```

---

### Respuesta del LLM

> Aquí tienes el análisis arquitectónico y la propuesta de rediseño para la jerarquía de clases presentada.

---

## Principios violados

El diseño actual es un catálogo de antipatrones que rompe el corazón de SOLID. Específicamente, viola tres principios fundamentales:

* **LSP (Liskov Substitution Principle - Principio de Sustitución de Liskov):** Este es el principio más gravemente vulnerado. LSP dicta que las subclases deben poder sustituir a sus clases base sin alterar el comportamiento correcto del programa. Lanzar `UnsupportedOperationException` rompe por completo este contrato. `Vendedor` rompe el comportamiento de `Comprador` (al no poder comprar), y `Administrador` altera arbitrariamente el comportamiento de ambos.
* **ISP (Interface Segregation Principle - Principio de Segregación de Interfaces):** La interfaz `Usuario` es una "interfaz grasa" o *Polluted Interface*. Obliga a las clases que la implementan a depender de métodos que no necesitan ni utilizan. Un `Comprador` no tiene por qué saber qué es `moderarComentario` o `gestionarUsuarios`.
* **OCP (Open/Closed Principle - Principio de Abierto/Cerrado):** El diseño no es extensible. Si mañana aparece un rol intermedio (por ejemplo, un "Moderador" que solo modera comentarios pero no es administrador), la jerarquía de herencia colapsaría, obligándonos a modificar la interfaz `Usuario` o a meter con calzador métodos lanzando excepciones en nuevas subclases.

---

## Problemas de diseño

* **Abuso de la herencia (*Is-A* vs *Has-A*):** Se utilizó la herencia como un mecanismo perezoso para reutilizar código o modelar jerarquías de poder del mundo real. Decir que un `Vendedor` *es un* `Comprador` es falso en este contexto de negocio (ya que el vendedor no puede comprar). Menos aún un `Administrador` *es un* `Vendedor`. Los roles de usuario son dinámicos y comportamentales, no jerárquicos por ADN.
* **Acoplamiento rígido y frágil:** `Administrador` hereda indirectamente de `Comprador` a través de `Vendedor`. Cualquier cambio en la estructura o comportamiento de `Comprador` impactará en cascada a toda la aplicación de forma impredecible.
* **Falta de Cohesión:** La interfaz `Usuario` mezcla tres dominios de negocio completamente distintos: Operaciones de cliente (comprar/vender), Moderación de comunidad (comentarios) y Administración del sistema (gestión de usuarios y reportes).

---

## Riesgos en ejecución

* **Explosiones en tiempo de ejecución (`RuntimeExceptions`):** El uso de `UnsupportedOperationException` transforma errores que deberían ser detectados por el compilador en bombas de tiempo en producción.
* **Fragilidad en el Polimorfismo:** Si un desarrollador confía en el contrato de la interfaz y escribe un código como el siguiente, la aplicación fallará inmediatamente al procesar un `Vendedor`:

```java
public void procesarCompra(Usuario usuario, Libro libro) {
    usuario.comprar(libro); // Lanza excepción si el usuario es Vendedor
}

```

* **Complejidad cognitiva y propensión a *Bugs*:** Para evitar los bloqueos, el equipo se verá obligado a usar bloques `try-catch` excesivos o, peor aún, a llenar el código de condicionales con `instanceof` para verificar el tipo real de la clase antes de invocar un método, anulando toda ventaja de la programación orientada a objetos.

---

## Rediseño propuesto

Para solucionar esto de raíz, aplicaremos dos estrategias de diseño de nivel Senior:

1. **Segregación por Capacidades (ISP):** Dividiremos la interfaz única en interfaces de rol o "capacidades" atómicas (`Comprable`, `Vendible`, `Moderable`, etc.).
2. **Composición sobre Herencia (Favor Composition over Inheritance):** En lugar de heredar los roles, un `Usuario` genérico *tendrá* un conjunto de permisos o capacidades, o bien separaremos los actores de manera limpia si son entidades de negocio distintas. Dado que un usuario real puede mutar de rol (un comprador puede volverse vendedor), el enfoque basado en interfaces limpias o en el patrón *Strategy/Role* es el ideal.

Para este ejemplo, mantendremos las entidades separadas e independientes implementando solo lo que saben hacer, eliminando la herencia entre ellas.

---

## Código ejemplo

### 1. Interfaces Segregadas (Contratos Atómicos)

```java
public interface Comprador {
    void comprar(Libro libro);
}

public interface Vendedor {
    void vender(Libro libro);
}

public interface Moderador {
    void moderarComentario(Comentario comentario);
}

public interface Administrador Sistema {
    void generarReporteVentas();
    void gestionarUsuarios();
}

```

### 2. Implementaciones Cohesivas y Libres de Excepciones

```java
// El cliente estándar solo compra
public class ClienteComprador implements Comprador {
    @Override
    public void comprar(Libro libro) {
        // Lógica de compra
        System.out.println("Libro comprado con éxito.");
    }
}

// El vendedor independiente solo vende
public class ClienteVendedor implements Vendedor {
    @Override
    public void vender(Libro libro) {
        // Lógica de venta
        System.out.println("Libro puesto a la venta.");
    }
}

// Si un usuario del negocio puede hacer ambas cosas, COMPONE los contratos sin romper nada
public class ClienteHibrido implements Comprador, Vendedor {
    @Override
    public void comprar(Libro libro) { System.out.println("Comprando..."); }

    @Override
    public void vender(Libro libro) { System.out.println("Vendiendo..."); }
}

// El administrador se enfoca en la gestión del sistema
public class Administrador implements Moderador, AdministradorSistema {
    @Override
    public void moderarComentario(Comentario comentario) {
        System.out.println("Comentario moderado.");
    }

    @Override
    public void generarReporteVentas() {
        System.out.println("Generando reporte...");
    }

    @Override
    public void gestionarUsuarios() {
        System.out.println("Gestionando usuarios...");
    }
}

```

---

## Beneficios del rediseño

* **Seguridad en tiempo de compilación:** Desaparecen por completo las `UnsupportedOperationException`. Si intentas hacer que un `ClienteComprador` venda, el código simplemente no compilará. El compilador vuelve a ser tu aliado.
* **Flexibilidad y Extensibilidad (OCP):** Si mañana el negocio requiere un rol de "Moderador Junior" que solo modere comentarios pero no administre usuarios, basta con crear una clase `ModeradorJunior implements Moderador`. No hay que tocar ninguna clase existente.
* **Código Altamente Testeable:** Al tener interfaces pequeñas y cohesivas, crear *mocks* para pruebas unitarias se vuelve una tarea trivial, eliminando la necesidad de simular comportamientos excepcionales por métodos no soportados.
* **Mantenimiento Sencillo:** Cada clase tiene una única razón para cambiar (SRP - Single Responsibility Principle), lo que reduce drásticamente el riesgo de que un cambio en la lógica de ventas rompa el módulo de administración o compras.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt establecio un rol claro y experto al solicitar la actuacion como un Arquitecto de Software Senior, lo que modulo el nivel tecnico esperado en la respuesta. Tambien proporciono el contexto completo del codigo fuente con la jerarquia de herencia fallida para que el modelo pudiera analizar la estructura real del problema de OpenLib Market.

Ademas, las instrucciones fueron explicitas y estructuradas, solicitando categorias puntuales como principios violados, riesgos en ejecucion y una propuesta de rediseño justificada. El uso de un formato de entrega especifico facilito una respuesta organizada por secciones, asegurando que se abordaran de forma individual el analisis conceptual y la solucion de codigo en Java.

#### 2. ¿Qué se puede mejorar?

Aunque el analisis del modelo identifico correctamente las violaciones graves a los principios de sustitucion de Liskov, segregacion de interfaces y abierto/cerrado, la respuesta paso por alto las consecuencias mas profundas en tiempo de ejecucion, como el uso inevitable de bloques condicionales con verificaciones manuales de tipo que ensucian el codigo de cliente. Ademas, el rediseño propuesto cometio el error de nombrar las interfaces utilizando sustantivos de rol en lugar de capacidades, y sugirio la creacion de clases hibridas que duplican logica de negocio y provocan una explosion combinatoria de componentes rigidos.


#### 3. Respuesta final

Para resolver los problemas de OpenLib Market de forma limpia, debemos eliminar por completo la herencia rigida y estructurar el diseño mediante la composicion de capacidades, renombrando los contratos bajo nombres de comportamiento como Comprable, Vendible, Moderable y Administrable. En lugar de crear tipos especificos para cada usuario, la clase Usuario actua como un contenedor cohesivo que almacena referencias dinamicas hacia estas interfaces. Esto permite asignar o remover permisos en tiempo de ejecucion a traves de metodos de configuracion, delegando la ejecucion de cada operacion de forma segura mediante validaciones internas que evitan por completo el lanzamiento de excepciones inesperadas. Con este enfoque, el sistema gana flexibilidad estructural y el compilador vuelve a garantizar la seguridad del codigo sin comprometer el polimorfismo.
