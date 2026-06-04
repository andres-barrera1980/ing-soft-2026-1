# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [XX]: [Título resumido]

### Estudiante
- **Nombre completo**: [Tu nombre y apellido]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude / ChatGPT / Gemini / Copilot / DeepSeek / Qwen / Mistral / Otro / **Sin IA — respuesta propia**] |
| **Modelo específico** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| **¿Por qué elegiste este LLM?** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

Analiza la siguiente jerarquía de clases de Usuario. Identifica qué principios SOLID se están violando y explica por qué. Luego propone un rediseño completo que corrija los problemas. Tu propuesta debe señalar si la interfaz tiene demasiados métodos, cómo debería dividirse en interfaces más específicas, y cómo reorganizar la herencia para que cada tipo de usuario (Comprador, Vendedor, Administrador) tenga solo las operaciones que realmente necesita. Finalmente, justifica cómo tu diseño evita excepciones en tiempo de ejecución y mejora la mantenibilidad y extensibilidad del sistema.

java
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

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
Análisis de la Jerarquía Usuario
1. Principios SOLID Violados
A. ISP (Interface Segregation Principle) — Violación Principal
Definición

Ningún cliente debe verse obligado a depender de métodos que no utiliza.

La interfaz:

public interface Usuario {
    void comprar(Libro libro);
    void vender(Libro libro);
    void moderarComentario(Comentario comentario);
    void generarReporteVentas();
    void gestionarUsuarios();
}

obliga a todos los usuarios a implementar operaciones que no les corresponden.

Por ejemplo:

public class Comprador implements Usuario

está obligado a implementar:

vender()
moderarComentario()
generarReporteVentas()
gestionarUsuarios()

aunque un comprador nunca realizará esas acciones.

Por eso aparecen implementaciones como:

throw new UnsupportedOperationException("No autorizado");

Esto es una señal clara de que la interfaz está mal diseñada.

B. LSP (Liskov Substitution Principle)
Definición

Una subclase debe poder sustituir a su clase base sin alterar el comportamiento esperado.

Observemos:

public class Vendedor extends Comprador

Un vendedor hereda:

comprar()

Sin embargo luego lo sobrescribe:

@Override
public void comprar(Libro libro) {
    throw new UnsupportedOperationException(
        "Un vendedor no compra"
    );
}

Esto rompe el contrato de la clase padre.

Si un objeto espera un:

Comprador

debería poder recibir un:

Vendedor

sin problemas.

Ejemplo:

public void realizarCompra(Comprador comprador) {
    comprador.comprar(libro);
}

Esto funciona para:

Comprador

pero falla para:

Vendedor

porque lanza:

UnsupportedOperationException

Por tanto se viola LSP.

C. SRP (Single Responsibility Principle)

La interfaz mezcla responsabilidades distintas:

Operación	Responsabilidad
comprar	Compras
vender	Ventas
moderarComentario	Moderación
generarReporteVentas	Reportes
gestionarUsuarios	Administración

Son áreas funcionales completamente diferentes.

La interfaz tiene demasiadas responsabilidades agrupadas.

2. Problemas de Diseño
Problema 1: Interfaz Gigante

La interfaz tiene:

5 métodos

que pertenecen a tres tipos de usuario diferentes.

Esto genera:

Código innecesario.
Métodos vacíos.
Excepciones artificiales.
Alto acoplamiento.
Problema 2: Herencia Incorrecta

Actualmente:

Comprador
   ↑
Vendedor
   ↑
Administrador

Conceptualmente esto significa:

"Un Vendedor ES un Comprador"

y

"Un Administrador ES un Vendedor"

Lo cual no necesariamente es cierto.

Se está usando herencia para reutilizar código y no para representar una relación "es un".

Problema 3: Excepciones en Tiempo de Ejecución

El sistema compila correctamente:

Usuario usuario = new Comprador();
usuario.gestionarUsuarios();

pero en ejecución produce:

UnsupportedOperationException

Los errores aparecen demasiado tarde.

3. Rediseño Aplicando ISP
Paso 1: Crear una interfaz base
public interface Usuario {
}

o incluso eliminarla si no aporta comportamiento común.

Paso 2: Segregar interfaces
Compras
public interface Comprador {

    void comprar(Libro libro);

}
Ventas
public interface Vendedor {

    void vender(Libro libro);

    void generarReporteVentas();

}
Moderación
public interface Moderador {

    void moderarComentario(
        Comentario comentario
    );

}
Administración
public interface AdministradorSistema {

    void gestionarUsuarios();

}
4. Nuevas Implementaciones
Comprador
public class UsuarioComprador
        implements Comprador {

    @Override
    public void comprar(
            Libro libro) {

        // implementación
    }
}

Solo tiene lo que necesita.

Vendedor
public class UsuarioVendedor
        implements Vendedor {

    @Override
    public void vender(
            Libro libro) {

        // implementación
    }

    @Override
    public void generarReporteVentas() {

        // implementación
    }
}
Administrador

Un administrador puede:

Comprar.
Vender.
Moderar.
Gestionar usuarios.

Entonces implementa únicamente las interfaces necesarias.

public class UsuarioAdministrador
        implements Comprador,
                   Vendedor,
                   Moderador,
                   AdministradorSistema {

    @Override
    public void comprar(
            Libro libro) {

        // implementación
    }

    @Override
    public void vender(
            Libro libro) {

        // implementación
    }

    @Override
    public void generarReporteVentas() {

        // implementación
    }

    @Override
    public void moderarComentario(
            Comentario comentario) {

        // implementación
    }

    @Override
    public void gestionarUsuarios() {

        // implementación
    }
}
5. Nueva Estructura
                    +-------------+
                    | Comprador   |
                    +-------------+
                    | comprar()   |
                    +-------------+

                    +-------------+
                    | Vendedor    |
                    +-------------+
                    | vender()    |
                    | reporte()   |
                    +-------------+

                    +-------------+
                    | Moderador   |
                    +-------------+
                    | moderar()   |
                    +-------------+

              +----------------------+
              | AdministradorSistema |
              +----------------------+
              | gestionarUsuarios()  |
              +----------------------+

                     UsuarioAdministrador
                              |
          -----------------------------------------
          |              |            |           |
      Comprador      Vendedor     Moderador   AdministradorSistema
6. ¿Cómo se Corrigen las Violaciones?
ISP

Antes:

Comprador

debía implementar:

vender()
moderarComentario()
gestionarUsuarios()

Ahora solo implementa:

comprar()

Cumple ISP.

LSP

Antes:

Vendedor extends Comprador

y anulaba comportamientos válidos.

Ahora:

UsuarioComprador
UsuarioVendedor
UsuarioAdministrador

son independientes.

Ninguna clase rompe contratos heredados.

Cumple LSP.

SRP

Cada interfaz tiene una única responsabilidad:

Interfaz	Responsabilidad
Comprador	Comprar
Vendedor	Vender
Moderador	Moderar
AdministradorSistema	Administración

Cumple SRP.

7. Beneficios del Nuevo Diseño
Evita excepciones en tiempo de ejecución

Antes:

usuario.gestionarUsuarios();

Compilaba pero fallaba en ejecución.

Ahora:

Comprador comprador =
    new UsuarioComprador();

comprador.gestionarUsuarios();

Ni siquiera compila.

El error se detecta antes de ejecutar.

Mayor mantenibilidad

Agregar una nueva capacidad:

public interface ExportadorReportes

no afecta compradores ni moderadores.

Mayor extensibilidad

Si OpenLib agrega un nuevo rol:

SupervisorContenido

simplemente implementa:

Moderador

sin modificar código existente.

Conclusión

La jerarquía original viola principalmente ISP y LSP, además de presentar síntomas de incumplimiento de SRP. La causa principal es una interfaz demasiado grande que obliga a implementar métodos irrelevantes y una herencia incorrecta que rompe contratos mediante UnsupportedOperationException. La solución consiste en segregar la interfaz en responsabilidades específicas y reemplazar la herencia por composición de capacidades mediante interfaces, logrando un diseño más limpio, extensible, mantenible y seguro, donde los errores se detectan en compilación en lugar de aparecer como excepciones en tiempo de ejecución.

---

### Análisis crítico de la respuesta
La IA identificó correctamente las violaciones de ISP, LSP y SRP en la jerarquía de Usuario, señalando que la interfaz estaba inflada y obligaba a implementar métodos irrelevantes, que la herencia entre Comprador y Vendedor rompía contratos y que las responsabilidades estaban mezcladas; además, propuso un rediseño aplicando segregación de interfaces y composición en lugar de herencia, lo cual es aplicable a OpenLib Market porque cada rol queda con las operaciones que realmente necesita y los errores se detectan en compilación en lugar de aparecer como excepciones en tiempo de ejecución; lo único que pasó por alto fue enfatizar más las consecuencias prácticas de esas excepciones y la falta de sentido semántico en la relación “un vendedor es un comprador”, pero en general el rediseño es correcto y mejora mantenibilidad y extensibilidad.

#### 1. ¿Qué hizo bien el prompt?

Lo más fuerte de tu prompt fue la inclusion de jerarquía completa de Usuario con todas las clases y métodos, lo que dio al modelo el contexto exacto para detectar las violaciones de SOLID sin tener que asumir nada; además, pediste explícitamente que señalara los principios violados y propusiera un rediseño completo, incluyendo la división de la interfaz en partes más específicas y la reorganización de la herencia, lo que guió la respuesta hacia ISP y LSP; en conjunto, el formato claro y la instrucción de justificar cómo se evita el problema de excepciones en tiempo de ejecución hicieron que la salida fuera estructurada y muy alineada con lo que pide el parcial.


#### 2. ¿Qué se puede mejorar?

Lo que le faltó al prompt fue ser más preciso en las restricciones del rediseño, ya que pediste un análisis y propuesta completa pero no aclaraste que querías que el modelo explicara explícitamente cómo se cumplen cada principio después de la refactorización ni que mostrara las consecuencias prácticas de las excepciones en tiempo de ejecución; por eso la IA se centró en dividir la interfaz y reorganizar la herencia, pero no profundizó en el impacto de esos errores en compilación versus ejecución, ni en la semántica incorrecta de “un vendedor es un comprador”; en resumen, el prompt funcionó bien pero pudo ser más claro al exigir justificación principio por principio y al pedir que se explicara cómo el nuevo diseño previene fallos en tiempo de ejecución.


#### 3. Respuesta final

La jerarquía original de Usuario viola principalmente ISP, LSP y SRP, porque la interfaz obliga a implementar métodos irrelevantes, la herencia entre Comprador y Vendedor rompe contratos y mezcla responsabilidades distintas; el rediseño correcto consiste en segregar la interfaz en varias más pequeñas (Comprador, Vendedor, Moderador, AdministradorSistema) y hacer que cada clase implemente solo las capacidades que necesita, mientras que el UsuarioAdministrador puede implementar múltiples interfaces para reflejar sus funciones reales; con este enfoque se eliminan las excepciones en tiempo de ejecución, los errores se detectan en compilación, se respeta la semántica del dominio y se logra un sistema más mantenible y extensible, alineado con los principios SOLID vistos en clase.
