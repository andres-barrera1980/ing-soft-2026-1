
## Pregunta [03]: [Evaluar una jerarquía de clases que viola principios de diseño]

### Estudiante
- **Nombre completo**: Julian Felipe Rojas Almanza

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Gemini 1.5 Pro |
| **¿Por qué elegiste este LLM?** | Según la tabla de la guía, para tareas de arquitectura, diseño jerárquico y razonamiento abstracto, este modelo es de los mejores. Analiza con mucha agudeza los problemas de herencia errónea y los riesgos asociados a excepciones en tiempo de ejecución. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un Arquitecto de Software Senior y experto en Modelado de Dominio y Diseño Orientado a Objetos. Estoy resolviendo un ejercicio técnico académico sobre el proyecto "OpenLib Market" y necesito que respondas exactamente a las tareas solicitadas sobre la jerarquía de clases que te presentaré.

[CONTEXTO]
El equipo de desarrollo ha modelado la jerarquía de los tres tipos de usuarios del sistema (Comprador, Vendedor y Administrador):

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

[PROBLEMA / TAREA]
Necesito que resuelvas y respondas de forma directa y explícita a los siguientes dos puntos requeridos por mi enunciado:
1. Analiza la jerarquía actual, identifica detalladamente qué principios SOLID se están violando (especificando los problemas tanto en la interfaz como en las relaciones de herencia de las clases) y justifica el porqué.
2. Propón un rediseño completo y aplicable para el sistema que corrija estos errores estructurales, eliminando el antipatrón de lanzar excepciones por falta de soporte en tiempo de ejecución.

[RESTRICCIONES]
- Chain of Thought: Antes de escribir el código, explica con claridad el peligro de heredar comportamiento de clases hermanas (como Vendedor heredando de Comprador) y las consecuencias en tiempo de ejecución de las excepciones 'UnsupportedOperationException'.
- El rediseño debe aplicar la segregación de interfaces de manera que ninguna clase implemente métodos que no usa.
- Favorece la composición sobre la herencia si consideras que es un diseño más flexible para el dominio de "OpenLib Market".


[FORMATO DE SALIDA]
Estructura tu respuesta en Markdown utilizando única y estrictamente estos tres encabezados para organizar la información:
- ### 1. Identificación de los Principios SOLID Violados y Errores de Jerarquía
- ### 2. Estrategia de Rediseño Conceptual
- ### 3. Código Java Rediseñado

[VALIDACIÓN]
Sabré que tu respuesta es excelente si identificas con precisión al menos el Principio de Segregación de Interfaces (ISP) y el Principio de Sustitución de Liskov (LSP), si explicas el riesgo sistémico de las excepciones en tiempo de ejecución en este diseño, y si el código final propone contratos de interfaces limpios, modulares y reutilizables.
```

---

### Respuesta del LLM


# Solución

### 1. Identificación de los Principios SOLID Violados y Errores de Jerarquía

**1. Principio de Segregación de Interfaces (ISP)**

La interfaz `Usuario` obliga a todas las clases a implementar métodos que no necesitan.

Por ejemplo:

* `Comprador` no debería implementar `vender()`, `moderarComentario()` o `gestionarUsuarios()`.
* `Vendedor` no debería implementar funciones administrativas.

Esto provoca métodos que terminan lanzando `UnsupportedOperationException`.

---

**2. Principio de Sustitución de Liskov (LSP)**

`Vendedor` hereda de `Comprador`, pero modifica el comportamiento de `comprar()` lanzando una excepción.

Si una variable es de tipo `Comprador`, debería poder utilizar cualquier subclase sin comportamientos inesperados.

```java
Comprador comprador = new Vendedor();
comprador.comprar(libro); // Error en ejecución
```

Esto rompe el principio de sustitución.

---

**3. Problema de Herencia Incorrecta**

Un vendedor no es un tipo de comprador.

Un administrador tampoco es un tipo de vendedor.

La relación "es un" (is-a) no existe realmente entre estas clases.

La herencia está siendo utilizada para reutilizar código y no para representar correctamente el dominio.

---

**Riesgo de las UnsupportedOperationException**

Las excepciones aparecen únicamente en tiempo de ejecución.

Esto significa que el código compila correctamente, pero falla cuando se ejecuta, generando errores difíciles de detectar y mantener.

---

### 2. Estrategia de Rediseño Conceptual

La solución consiste en aplicar **Segregación de Interfaces (ISP)** y **Composición sobre Herencia**.

En lugar de una interfaz gigante `Usuario`, se crean interfaces pequeñas y específicas:

* `Comprador`
* `Vendedor`
* `Moderador`
* `AdministradorSistema`

Luego cada usuario implementa únicamente las capacidades que realmente posee.

Ventajas:

* No existen métodos innecesarios.
* No se lanzan excepciones por funcionalidades no soportadas.
* Se cumple ISP y LSP.
* El modelo representa mejor el negocio.

---

### 3. Código Java Rediseñado

#### Interfaces específicas

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

public interface AdministradorSistema {
    void gestionarUsuarios();
    void generarReporteVentas();
}
```

#### Usuario Comprador

```java
public class UsuarioComprador implements Comprador {

    @Override
    public void comprar(Libro libro) {
        System.out.println("Compra realizada");
    }
}
```

#### Usuario Vendedor

```java
public class UsuarioVendedor implements Vendedor {

    @Override
    public void vender(Libro libro) {
        System.out.println("Libro publicado");
    }
}
```

#### Usuario Administrador

```java
public class UsuarioAdministrador
        implements Comprador,
                   Vendedor,
                   Moderador,
                   AdministradorSistema {

    @Override
    public void comprar(Libro libro) {
        System.out.println("Compra realizada");
    }

    @Override
    public void vender(Libro libro) {
        System.out.println("Venta realizada");
    }

    @Override
    public void moderarComentario(Comentario comentario) {
        System.out.println("Comentario moderado");
    }

    @Override
    public void gestionarUsuarios() {
        System.out.println("Usuarios gestionados");
    }

    @Override
    public void generarReporteVentas() {
        System.out.println("Reporte generado");
    }
}
```

### Conclusión

El rediseño elimina la herencia incorrecta entre compradores, vendedores y administradores. Cada clase implementa únicamente las capacidades que necesita mediante interfaces pequeñas y especializadas. Con esto se eliminan las `UnsupportedOperationException`, se cumplen los principios **ISP** y **LSP**, y el modelo resulta más flexible, mantenible y alineado con el dominio de OpenLib Market.

```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt está correctamente estructurado porque se aplicó la segmentación indicada en el archivo guia_prompting.md, donde se explica cómo construir un buen prompt. El resultado mejoró notablemente gracias al apoyo inicial de una Gema (el agente de IA de Gemini) y a mi posterior revisión y corrección. Las instrucciones son claras, incluye un rol, aporta contexto y la estructura de respuesta solicitada es sencilla y efectiva


#### 2. ¿Qué se puede mejorar?

Dado que el prompt ya cumple estrictamente con la segmentación de la guía y fue refinado, las oportunidades de mejora son reducidas. Podría evaluarse a futuro una ligera variación en el rol, pero en su estado actual, el prompt ya es óptimo y no requiere modificaciones estructurales.


#### 3. Respuesta final

Se identificó cómo la interfaz gigante rompía el principio de segregación de interfaces al obligar a las clases a meter comportamientos que no usan, y se demostró muy bien que la herencia estaba mal planteada rompiendo el principio de sustitución de liskov, pues usar la herencia solo para copiar código provoca errores fatales. el rediseño conceptual separando todo en contratos pequeños es totalmente correcto y aplicable, logrando que cada rol tenga su frontera limpia. además, la inteligencia artificial estuvo muy buena al notar el riesgo de las excepciones de operación no soportada en tiempo de ejecución, pues esos errores silenciosos hacen que el código compile bien pero explote cuando el usuario da clic en la aplicación real.

sin embargo, el análisis omitió un vacío gigante sobre las consecuencias en memoria y el acoplamiento que esto causa en el servidor de openlib market. al crear clases rígidas como usuarioadministrador que implementan de golpe cuatro interfaces distintas mediante herencia múltiple de tipos, se genera un objeto gigante difícil de mantener, pues si cambian las reglas de moderación se afecta a todo el administrador. para que la solución fuera perfecta, faltó mencionar que la inteligencia artificial pasó por alto el patrón de diseño de decoración o el uso de un modelo basado en componentes o roles dinámicos. De esta manera, se evita duplicar el código de comprar y vender dentro de la clase del administrador, lo cual vuelve a parecer a código duplicado y diseño amarrado.

el código original estaba mal diseñado porque la interfaz obligaba a meter métodos vacíos o con errores a las clases que no debían vender ni moderar, violando el principio de segregación de interfaces. además, romper el principio de sustitución de liskov al meter herencia forzada entre comprador y vendedor dañaba el comportamiento del sistema. para solucionarlo bien, se aplicó la segregación de interfaces junto con el concepto de composición sobre herencia, definiendo contratos específicos para cada rol del negocio. las clases independientes implementan solo lo que necesitan, quitando las excepciones en tiempo de ejecución. para evitar repetir código en el administrador, la opción elegida no debería ser meter todas las interfaces en una sola clase gigante, sino que se busca dinámicamente usando un patrón de diseño para componer los roles dentro del motor de spring. de esta manera, el sistema no solo borra las excepciones dañinas, sino que se puede probar cada rol por separado en experimentos de mentiras con mockito, quedando fácil de mantener, seguro y listo para recibir nuevos roles en el futuro.
