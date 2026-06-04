# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

---

## Pregunta [13]: [integracion refactoring]

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
Refactoriza la clase Notificador aplicando al menos un principio SOLID y al menos un patrón de diseño visto en clase. Separa responsabilidades de envío y logging, y permite agregar nuevos canales de notificación sin modificar código existente. Incluye pruebas unitarias con JUnit 5 que validen el envío por cada canal. Justifica qué principio SOLID se corrige, qué patrón aplicas y por qué es el más adecuado, descartando al menos dos alternativas.

---

### Respuesta del LLM
Voy a asumir una versión típica del problema (muy común en OpenLib): una clase Notificador que hoy está “hardcodeada” para enviar correos, WhatsApp o push y además hace logging dentro del mismo flujo.

🧨 Versión problemática (antes del refactor)
public class Notificador {

    public void enviar(String tipo, String destino, String mensaje) {

        if (tipo.equals("EMAIL")) {
            System.out.println("Email enviado a " + destino);
        } else if (tipo.equals("SMS")) {
            System.out.println("SMS enviado a " + destino);
        } else if (tipo.equals("PUSH")) {
            System.out.println("Push enviado a " + destino);
        }

        // logging mezclado
        System.out.println("LOG: notificación enviada");
    }
}
❌ Problemas detectados
🔴 Violación SOLID
1. SRP (Single Responsibility Principle)

La clase hace 2 cosas:

Enviar notificaciones
Hacer logging
2. OCP (Open/Closed Principle)

Cada vez que agregas un canal nuevo:

Slack, Telegram, Email2, etc.

👉 tienes que modificar Notificador.

🧠 Objetivo del refactor
Separar responsabilidades
Permitir agregar canales sin modificar código existente
Separar logging
Hacer el sistema extensible
🧩 Patrón de diseño elegido
✅ Strategy Pattern

Permite encapsular cada canal como una estrategia independiente.

🧱 Estructura final
Notificador
   ↓
NotificationChannel (interface)
   ↓
EmailChannel
SMSChannel
PushChannel

LoggerService (separado)
🧩 SOLUCIÓN REFACTORIZADA
1. Interface (Strategy)
public interface NotificationChannel {
    void send(String destino, String mensaje);
}
2. Implementaciones (estrategias)
Email
public class EmailChannel implements NotificationChannel {

    @Override
    public void send(String destino, String mensaje) {
        System.out.println("EMAIL enviado a " + destino + ": " + mensaje);
    }
}
SMS
public class SMSChannel implements NotificationChannel {

    @Override
    public void send(String destino, String mensaje) {
        System.out.println("SMS enviado a " + destino + ": " + mensaje);
    }
}
Push
public class PushChannel implements NotificationChannel {

    @Override
    public void send(String destino, String mensaje) {
        System.out.println("PUSH enviado a " + destino + ": " + mensaje);
    }
}
📜 Separación de Logging (SRP corregido)
public class LoggerService {

    public void log(String message) {
        System.out.println("LOG: " + message);
    }
}
🚀 Notificador refactorizado
import java.util.List;

public class Notificador {

    private final List<NotificationChannel> channels;
    private final LoggerService logger;

    public Notificador(List<NotificationChannel> channels,
                       LoggerService logger) {
        this.channels = channels;
        this.logger = logger;
    }

    public void enviar(String destino, String mensaje) {

        for (NotificationChannel channel : channels) {
            channel.send(destino, mensaje);
        }

        logger.log("Notificación enviada a " + destino);
    }
}
🧪 PRUEBAS UNITARIAS (JUnit 5)
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class NotificadorTest {

    @Test
    void debeEnviarNotificacionPorEmail() {

        NotificationChannel email = mock(EmailChannel.class);
        LoggerService logger = mock(LoggerService.class);

        Notificador notificador =
                new Notificador(List.of(email), logger);

        notificador.enviar("user@test.com", "Hola");

        verify(email).send("user@test.com", "Hola");
        verify(logger).log(anyString());
    }

    @Test
    void debeEnviarPorMultiplesCanales() {

        NotificationChannel email = mock(EmailChannel.class);
        NotificationChannel sms = mock(SMSChannel.class);
        LoggerService logger = mock(LoggerService.class);

        Notificador notificador =
                new Notificador(List.of(email, sms), logger);

        notificador.enviar("123", "Mensaje");

        verify(email).send("123", "Mensaje");
        verify(sms).send("123", "Mensaje");
    }

    @Test
    void debeRegistrarLogSiempre() {

        NotificationChannel email = mock(EmailChannel.class);
        LoggerService logger = mock(LoggerService.class);

        Notificador notificador =
                new Notificador(List.of(email), logger);

        notificador.enviar("user@test.com", "Hola");

        verify(logger).log(contains("Notificación enviada"));
    }
}
🧠 Principios SOLID aplicados
✅ SRP (corregido)

Antes:

Notificador = envío + logging

Ahora:

Notificador → coordinación
LoggerService → logging
Channels → envío
✅ OCP (Open/Closed Principle)

Ahora puedes agregar:

public class TelegramChannel implements NotificationChannel

sin modificar Notificador.

✅ DIP (Dependency Inversion Principle)

Notificador depende de abstracciones:

NotificationChannel
LoggerService

no de clases concretas.

🧠 Por qué Strategy es el mejor patrón
✔ Permite múltiples algoritmos (canales)

Cada canal = una estrategia.

✔ Permite extensión sin modificar código

Cumple OCP perfectamente.

✔ Fácil testeo

Cada canal puede mockearse.

❌ Patrones descartados
❌ 1. Factory Pattern

Problema:

Solo ayuda a CREAR objetos
No resuelve el problema de ejecución dinámica ni múltiples canales

👉 No encapsula comportamiento

❌ 2. Singleton

Problema:

Introduce estado global
Dificulta testing
Aumenta acoplamiento

👉 No aporta extensibilidad ni separación de comportamiento

❌ 3. Template Method

Problema:

Útil si hay un flujo fijo con pasos
Aquí los canales son independientes, no pasos de un algoritmo único
🚀 Cómo agregar nuevos canales (sin modificar código)

Ejemplo: Slack

public class SlackChannel implements NotificationChannel {

    @Override
    public void send(String destino, String mensaje) {
        System.out.println("SLACK: " + mensaje);
    }
}

👉 No se toca Notificador

🧾 Conclusión

El refactor del Notificador aplica:

✅ SRP (separación de responsabilidades)
✅ OCP (extensión sin modificación)
✅ DIP (dependencia de abstracciones)
🎯 Strategy Pattern como diseño principal

Esto permite que OpenLib Market:

escale canales de notificación fácilmente
reduzca acoplamiento
mejore testabilidad
mantenga código limpio y extensible

Si quieres, 
puedo llevar esto a un nivel más realista tipo producción (Spring Boot + inyección de dependencias + Kafka + colas de notificación).

---

### Análisis crítico de la respuesta

El LLM identificó correctamente las violaciones de SRP y OCP en la versión original: la clase mezclaba envío y logging, y obligaba a modificar código para cada nuevo canal. Aplicó el patrón Strategy, lo cual es adecuado porque encapsula cada canal como una estrategia independiente y permite extensión sin modificar el Notificador. Además, separó el logging en una clase aparte, cumpliendo SRP. También mencionó el DIP, ya que el Notificador depende de abstracciones (NotificationChannel, LoggerService) y no de implementaciones concretas.

#### 1. ¿Qué hizo bien el prompt?

Identificación de problemas de diseño: Señaló con precisión SRP y OCP.

Patrón elegido: Strategy es el más adecuado para este escenario, porque permite múltiples algoritmos (canales) y extensión sin tocar el código existente.

Separación de responsabilidades: Logging quedó en LoggerService, y cada canal en su propia clase.

Testabilidad: Usó JUnit 5 con mocks (Mockito), lo que permite verificar que cada canal y el logger se invocan correctamente.

Extensibilidad: Demostró cómo agregar un nuevo canal (ej. Slack) sin modificar Notificador.


#### 2. ¿Qué se puede mejorar?

Pruebas unitarias: Aunque usó Mockito, las pruebas podrían validar también el contenido del mensaje enviado, no solo la invocación.

Logging: El ejemplo usa System.out.println; en producción sería mejor usar un framework de logging (SLF4J, Logback) para mayor flexibilidad.

Inyección de dependencias: En un entorno real (ej. Spring Boot), convendría usar DI para instanciar canales y logger, en lugar de pasarlos manualmente.

Multiplicidad de canales: El Notificador envía a todos los canales de la lista; en algunos casos podría ser necesario elegir dinámicamente qué canal usar según contexto (ej. preferencia del usuario).


#### 3. Respuesta final

La clase original Notificador violaba SRP y OCP porque mezclaba envío y logging en un mismo método y obligaba a modificar código cada vez que se agregaba un canal nuevo. El refactor con Strategy Pattern y separación de responsabilidades corrige estos problemas: ahora cada canal es una estrategia independiente (EmailChannel, SMSChannel, PushChannel), el LoggerService maneja el registro, y el Notificador solo coordina. Esto cumple también con DIP, ya que depende de abstracciones y no de implementaciones concretas.

La solución del LLM fue acertada en la elección del patrón y en la separación de responsabilidades, además de incluir pruebas unitarias con JUnit 5 usando mocks para verificar que cada canal y el logger se invocan correctamente. Sin embargo, se puede mejorar en tres aspectos:

Pruebas más completas, validando no solo la invocación sino también el contenido del mensaje.

Uso de un framework de logging (SLF4J/Logback) en lugar de System.out.println, para mayor flexibilidad y nivel de detalle.

Inyección de dependencias en un entorno real (ej. Spring Boot), para instanciar canales y logger automáticamente y facilitar la configuración.
