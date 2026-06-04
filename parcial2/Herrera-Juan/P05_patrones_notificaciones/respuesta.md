# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [5]: [patrones notificacciones]

### Estudiante
- **Nombre completo**: [Juan Fernando Herrera Patiño]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [ChatGPT] |
| **Modelo específico** | [GPT-4o ]|
| **¿Por qué elegiste este LLM?** | [mas comodo para mi] |

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude / ChatGPT / Gemini / Copilot / DeepSeek / Qwen / Mistral / Otro / **Sin IA — respuesta propia**] |
| **Modelo específico** | [Ej: Claude Opus 4.5, GPT-4o, Gemini 2.5 Pro, etc. Si respondes sin IA, escribe "N/A"] |
| **¿Por qué elegiste este LLM?** | [Justifica en 1-3 oraciones. Si respondes sin IA, explica por qué decidiste no usar LLM para esta pregunta.] |

---

### Prompt utilizado

Eres un arquitecto de software experto en patrones de diseño GoF, principios SOLID y desarrollo orientado a objetos en Java.

Analiza el siguiente escenario:

En OpenLib Market, cuando un libro agotado vuelve a estar disponible, varios componentes del sistema deben reaccionar automáticamente:

Enviar notificación por correo a los usuarios que lo agregaron a su wishlist.
Enviar notificación push a los usuarios que lo marcaron como favorito en la aplicación móvil.
Actualizar la caché de Redis para que el libro aparezca nuevamente en las búsquedas.
Registrar el evento en un log de auditoría.

Diseña una solución utilizando DOS patrones de diseño vistos en clase que trabajen conjuntamente.

Debes:

Identificar los dos patrones más adecuados y justificar detalladamente por qué fueron seleccionados.
Explicar cómo interactúan entre sí dentro de la solución.
Descartar al menos dos patrones alternativos indicando por qué son menos apropiados.
Diseñar la arquitectura completa.
Incluir un diagrama de clases en Mermaid o texto.
Implementar las clases principales en Java.
Mostrar cómo registrar y desregistrar dinámicamente componentes interesados en recibir notificaciones.
Explicar qué sucede si una notificación falla y cómo podría manejarse el error.
Explicar cómo se cumple el bajo acoplamiento entre componentes.
Analizar qué principios SOLID se aplican en la solución.

La respuesta debe incluir explicación, diagrama de clases, código Java funcional y una conclusión justificando la elección de ambos patrones. 

---

### Respuesta del LLM

Solución Arquitectónica para OpenLib Market
1. Análisis del Problema

Cuando un libro vuelve a estar disponible deben ejecutarse automáticamente varias acciones:

Enviar correos electrónicos.
Enviar notificaciones push.
Actualizar Redis.
Registrar auditoría.

Características del problema:

Existen múltiples componentes interesados en el mismo evento.
Los componentes pueden agregarse o eliminarse dinámicamente.
El libro no debería conocer los detalles internos de correo, Redis o logs.
Debe existir bajo acoplamiento.
El sistema debe ser extensible.
2. Patrones Seleccionados
Patrón 1: Observer (Observador)
Justificación

El patrón Observer resuelve exactamente el problema:

"Varios objetos necesitan ser notificados automáticamente cuando otro objeto cambia de estado."

Cuando un libro pasa de:

AGOTADO

a

DISPONIBLE

todos los interesados deben enterarse.

Los observadores serán:

Servicio de correo
Servicio Push
Actualizador Redis
Auditoría
Patrón 2: Strategy (Estrategia)

Dentro de las notificaciones existen distintos mecanismos:

Email
Push
SMS (futuro)
WhatsApp (futuro)
Telegram (futuro)

Cada mecanismo representa una estrategia distinta de notificación.

Strategy permite intercambiarlas sin modificar el código existente.

3. Cómo Interactúan Ambos Patrones
Observer

Se encarga de:

Detectar disponibilidad
↓
Notificar interesados
Strategy

Se encarga de:

Cómo enviar la notificación

Por ejemplo:

LibroDisponible
      ↓
Observer Email
      ↓
EmailStrategy

LibroDisponible
      ↓
Observer Push
      ↓
PushStrategy

Observer decide quién recibe.

Strategy decide cómo se envía.

4. Patrones Descartados
Factory Method

Podría crear observadores.

Pero NO resuelve:

Comunicación automática entre componentes

Es un patrón de creación, no de comunicación.

Template Method

Define pasos fijos de un algoritmo.

Aquí no existe un flujo fijo.

Los observadores pueden agregarse o eliminarse dinámicamente.

Por tanto no es adecuado.

Decorator

Sirve para agregar comportamientos a un objeto.

No modela eventos ni suscriptores.

No resuelve el problema principal.

5. Arquitectura Propuesta
Interfaces Base
Observer
public interface ObservadorLibro {

    void actualizar(Libro libro);

}
Subject
public interface Sujeto {

    void registrar(
            ObservadorLibro observador);

    void eliminar(
            ObservadorLibro observador);

    void notificar();
}
6. Strategy de Notificaciones
public interface NotificacionStrategy {

    void enviar(
            String destinatario,
            String mensaje);

}
Implementaciones
Email
public class EmailStrategy
        implements NotificacionStrategy {

    @Override
    public void enviar(
            String destinatario,
            String mensaje) {

        System.out.println(
            "EMAIL enviado a "
            + destinatario
        );
    }
}
Push
public class PushStrategy
        implements NotificacionStrategy {

    @Override
    public void enviar(
            String destinatario,
            String mensaje) {

        System.out.println(
            "PUSH enviada a "
            + destinatario
        );
    }
}
7. Clase Libro (Subject)
import java.util.ArrayList;
import java.util.List;

public class Libro
        implements Sujeto {

    private String titulo;

    private boolean disponible;

    private List<ObservadorLibro>
            observadores =
            new ArrayList<>();

    public Libro(String titulo) {

        this.titulo = titulo;
        this.disponible = false;
    }

    public String getTitulo() {
        return titulo;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void volverDisponible() {

        disponible = true;

        notificar();
    }

    @Override
    public void registrar(
            ObservadorLibro observador) {

        observadores.add(observador);
    }

    @Override
    public void eliminar(
            ObservadorLibro observador) {

        observadores.remove(observador);
    }

    @Override
    public void notificar() {

        for (ObservadorLibro obs
                : observadores) {

            obs.actualizar(this);
        }
    }
}
8. Observador de Email
public class WishlistObserver
        implements ObservadorLibro {

    private NotificacionStrategy
            estrategia;

    public WishlistObserver(
            NotificacionStrategy estrategia) {

        this.estrategia = estrategia;
    }

    @Override
    public void actualizar(
            Libro libro) {

        estrategia.enviar(
                "usuario@email.com",
                "Disponible nuevamente: "
                + libro.getTitulo()
        );
    }
}
9. Observador Push
public class FavoritosObserver
        implements ObservadorLibro {

    private NotificacionStrategy
            estrategia;

    public FavoritosObserver(
            NotificacionStrategy estrategia) {

        this.estrategia = estrategia;
    }

    @Override
    public void actualizar(
            Libro libro) {

        estrategia.enviar(
                "usuarioApp",
                "Tu favorito volvió"
        );
    }
}
10. Observador Redis
public class RedisObserver
        implements ObservadorLibro {

    @Override
    public void actualizar(
            Libro libro) {

        System.out.println(
            "Redis actualizado para "
            + libro.getTitulo()
        );
    }
}
11. Observador Auditoría
public class AuditoriaObserver
        implements ObservadorLibro {

    @Override
    public void actualizar(
            Libro libro) {

        System.out.println(
            "AUDITORIA: "
            + libro.getTitulo()
            + " volvió a stock"
        );
    }
}
12. Registro Dinámico
Libro libro =
    new Libro("Clean Code");

ObservadorLibro email =
    new WishlistObserver(
        new EmailStrategy());

ObservadorLibro push =
    new FavoritosObserver(
        new PushStrategy());

ObservadorLibro redis =
    new RedisObserver();

ObservadorLibro auditoria =
    new AuditoriaObserver();

libro.registrar(email);
libro.registrar(push);
libro.registrar(redis);
libro.registrar(auditoria);
Eliminación Dinámica
libro.eliminar(push);

A partir de ese momento:

Push ya no recibirá eventos.
Ejecución
libro.volverDisponible();

Salida:

EMAIL enviado a usuario@email.com

PUSH enviada a usuarioApp

Redis actualizado para Clean Code

AUDITORIA: Clean Code volvió a stock
Diagrama UML (Mermaid)
Manejo de Errores

Supongamos:

EmailStrategy

falla porque el SMTP está caído.

Podemos encapsular cada notificación:

@Override
public void notificar() {

    for (ObservadorLibro obs
            : observadores) {

        try {

            obs.actualizar(this);

        } catch(Exception e) {

            System.out.println(
                "Error notificando: "
                + e.getMessage()
            );
        }
    }
}

Resultado:

Email falla
↓
Push continúa
↓
Redis continúa
↓
Auditoría continúa

Un fallo no afecta los demás observadores.

Bajo Acoplamiento

La clase:

Libro

no conoce:

SMTP
Firebase
Redis
Auditoría

Solo conoce:

ObservadorLibro

(abstracción)

Esto reduce significativamente el acoplamiento.

Principios SOLID Aplicados
SRP

Cada clase tiene una única responsabilidad.

Clase	Responsabilidad
Libro	Gestionar disponibilidad
WishlistObserver	Correo wishlist
FavoritosObserver	Push favoritos
RedisObserver	Redis
AuditoriaObserver	Auditoría
OCP

Agregar:

SMSObserver

o

WhatsAppStrategy

no requiere modificar código existente.

LSP

Todos los observadores pueden sustituirse por:

ObservadorLibro

sin romper el sistema.

ISP

Las interfaces son pequeñas:

actualizar()
enviar()

No existen métodos innecesarios.

DIP

Las dependencias apuntan a abstracciones:

ObservadorLibro
NotificacionStrategy

y no a implementaciones concretas.

Conclusión

La combinación de Observer + Strategy es la solución más adecuada para OpenLib Market. Observer permite desacoplar completamente al libro de los componentes que reaccionan ante el evento de reposición de stock, mientras que Strategy permite intercambiar dinámicamente los distintos mecanismos de notificación. Juntos proporcionan una arquitectura extensible, mantenible y alineada con los principios SOLID, permitiendo agregar nuevos observadores o nuevos canales de notificación sin modificar el código existente y manteniendo un bajo acoplamiento entre los componentes del sistema.

### Análisis crítico de la respuesta
El LLM eligió patrones adecuados para este escenario. Observer es el patrón principal porque permite que múltiples componentes reaccionen automáticamente cuando un libro vuelve a estar disponible, además de permitir registrar y desregistrar observadores dinámicamente mediante los métodos registrar() y eliminar(). La combinación con Strategy también tiene sentido, ya que separa la lógica de notificación de los distintos canales de envío (correo, push, etc.), permitiendo agregar nuevos mecanismos sin modificar el sistema existente. La implementación aplica correctamente ambos patrones y mantiene un bajo acoplamiento, pues la clase Libro solo conoce la abstracción ObservadorLibro y no depende de servicios concretos. Además, contempla el manejo básico de fallos mediante bloques try-catch para evitar que el error de una notificación impida la ejecución de las demás. Sin embargo, podría mejorarse registrando los errores en un sistema de monitoreo o reintentando automáticamente las notificaciones fallidas. En general, la combinación de patrones no se siente forzada, es coherente con el problema y produce una solución flexible, extensible y alineada con los principios SOLID.
#### 1. ¿Qué hizo bien el prompt?

El prompt fue efectivo porque proporcionó un contexto claro del problema y especificó que debían utilizarse dos patrones de diseño trabajando en conjunto. Además, solicitó explícitamente la justificación de los patrones elegidos, un diagrama de clases, código Java y el análisis del manejo de errores, lo que ayudó a obtener una respuesta completa y bien estructurada. La parte más útil fue pedir cómo interactúan ambos patrones y cómo se realiza el registro y desregistro dinámico de componentes, ya que esto llevó al LLM a proponer correctamente el uso de Observer para la gestión de eventos y a explicar el desacoplamiento entre los componentes del sistema.


#### 2. ¿Qué se puede mejorar?

El prompt podría mejorarse especificando que uno de los patrones debe encargarse explícitamente de la creación o gestión de los observadores, ya que el uso de Strategy resulta válido pero no necesariamente la única combinación posible. También habría sido útil exigir un diagrama UML completo en Mermaid y una implementación más realista del manejo de fallos, por ejemplo mediante reintentos o colas de mensajes. Aunque la respuesta fue correcta, no profundizó en aspectos como concurrencia, procesamiento asíncrono o persistencia de eventos, temas que podrían ser relevantes en un sistema de notificaciones real.


#### 3. Respuesta final

La mejor solución para este escenario es combinar los patrones Observer y Strategy. Observer permite que distintos componentes interesados (correo, push, Redis y auditoría) sean notificados automáticamente cuando un libro cambia de estado a disponible, manteniendo un bajo acoplamiento entre el libro y los servicios que reaccionan al evento. Por su parte, Strategy permite encapsular diferentes mecanismos de notificación, como correo o push, facilitando agregar nuevos canales en el futuro sin modificar el código existente. Esta combinación cumple los principios SOLID, especialmente OCP y DIP, ya que el sistema depende de abstracciones y puede extenderse mediante nuevos observadores o estrategias. Además, permite registrar y eliminar dinámicamente componentes interesados y manejar fallos de forma independiente para que un error en una notificación no afecte las demás acciones del sistema.
