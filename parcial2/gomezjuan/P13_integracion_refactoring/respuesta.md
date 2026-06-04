# Pregunta P13: Refactoring integral de la clase Notificador

### Estudiante
- **Nombre completo**: Juan Camilo Gomez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Claude Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | Esta pregunta de integración requiere aplicar SOLID + patrón de diseño + pruebas unitarias en conjunto. Es una tarea donde la generación de código extenso y bien estructurado es el valor principal, y Claude es consistente en ese tipo de respuestas. |

---

### Prompt utilizado

```
Soy estudiante de Ingeniería de Software en la Pontificia Universidad Javeriana.
Proyecto: OpenLib Market, plataforma de venta de libros digitales en Colombia.

Este código compila y funciona, pero tiene problemas de diseño:

public class Notificador {
    public void enviar(String tipo, String destinatario, String mensaje) {
        if (tipo.equals("EMAIL")) {
            System.out.println("Enviando EMAIL a " + destinatario + ": " + mensaje);
        } else if (tipo.equals("SMS")) {
            System.out.println("Enviando SMS a " + destinatario + ": " + mensaje);
        } else if (tipo.equals("PUSH")) {
            System.out.println("Enviando PUSH a " + destinatario + ": " + mensaje);
        }
        try {
            Files.write(Paths.get("/var/log/openlib/notificaciones.log"),
                (tipo + "|" + destinatario + "|" + mensaje + "\n").getBytes(),
                StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

Refactoriza esta clase integralmente:
1. Identifica todos los principios SOLID violados con argumentos concretos
2. Aplica AL MENOS UN principio SOLID en la solución
3. Aplica AL MENOS UN patrón de diseño de los vistos en clase (Singleton, Factory, Observer, 
   Strategy, Decorator, Command, Template Method). Justifica tu elección.
4. Separa el logging de la lógica de notificación
5. Asegúrate de que la nueva versión permita agregar un canal nuevo (WhatsApp) 
   SIN modificar código existente
6. Incluye pruebas unitarias con JUnit 5 para los canales de notificación

Restricciones:
- Java 21
- El logging debe estar separado de la lógica de envío
- Usa interfaces
- Responde en markdown con bloques de código Java bien formateados
```

---

### Respuesta del LLM

## Refactoring del Notificador

### Principios SOLID violados

**SRP**: `Notificador` hace tres cosas: decide cómo enviar según el tipo, implementa el envío de cada canal, y escribe en el log. Tres responsabilidades distintas.

**OCP**: Agregar WhatsApp requiere modificar `enviar()` con otro `else if`. La clase no está cerrada a modificación.

**DIP**: La clase depende de implementaciones concretas (la lógica hardcodeada de SMS, EMAIL, PUSH) en lugar de abstracciones.

### Patrón elegido: Strategy

Cada canal de notificación es una estrategia independiente. El `Notificador` es el contexto que delega a la estrategia correcta.

---

### Implementación refactorizada

```java
// Abstracción — interfaz Strategy
public interface CanalNotificacion {
    void enviar(String destinatario, String mensaje);
    String getTipo();
}
```

```java
// Estrategia concreta: Email
public class CanalEmail implements CanalNotificacion {
    @Override
    public void enviar(String destinatario, String mensaje) {
        // Conectar a SMTP y enviar correo
        System.out.println("Enviando EMAIL a " + destinatario + ": " + mensaje);
    }

    @Override
    public String getTipo() { return "EMAIL"; }
}
```

```java
// Estrategia concreta: SMS
public class CanalSMS implements CanalNotificacion {
    @Override
    public void enviar(String destinatario, String mensaje) {
        // Conectar a API de Twilio
        System.out.println("Enviando SMS a " + destinatario + ": " + mensaje);
    }

    @Override
    public String getTipo() { return "SMS"; }
}
```

```java
// Estrategia concreta: Push
public class CanalPush implements CanalNotificacion {
    @Override
    public void enviar(String destinatario, String mensaje) {
        // Conectar a Firebase
        System.out.println("Enviando PUSH a " + destinatario + ": " + mensaje);
    }

    @Override
    public String getTipo() { return "PUSH"; }
}
```

```java
// WhatsApp — agregado SIN modificar código existente
public class CanalWhatsApp implements CanalNotificacion {
    @Override
    public void enviar(String destinatario, String mensaje) {
        // Conectar a WhatsApp Business API
        System.out.println("Enviando WHATSAPP a " + destinatario + ": " + mensaje);
    }

    @Override
    public String getTipo() { return "WHATSAPP"; }
}
```

```java
// Interfaz para el logger — separación de responsabilidades
public interface LoggerNotificaciones {
    void registrar(String tipo, String destinatario, String mensaje);
}
```

```java
// Implementación de logger en archivo
public class LoggerArchivo implements LoggerNotificaciones {
    private final Path rutaLog;

    public LoggerArchivo(Path rutaLog) {
        this.rutaLog = rutaLog;
    }

    @Override
    public void registrar(String tipo, String destinatario, String mensaje) {
        try {
            Files.write(rutaLog,
                (tipo + "|" + destinatario + "|" + mensaje + "\n").getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error escribiendo log: " + e.getMessage());
        }
    }
}
```

```java
// Notificador refactorizado — solo coordina, no implementa
public class Notificador {
    private final Map<String, CanalNotificacion> canales = new HashMap<>();
    private final LoggerNotificaciones logger;

    public Notificador(LoggerNotificaciones logger, CanalNotificacion... canalesIniciales) {
        this.logger = logger;
        for (CanalNotificacion canal : canalesIniciales) {
            registrar(canal);
        }
    }

    public void registrar(CanalNotificacion canal) {
        canales.put(canal.getTipo(), canal);
    }

    public void enviar(String tipo, String destinatario, String mensaje) {
        CanalNotificacion canal = canales.get(tipo);
        if (canal == null) throw new IllegalArgumentException("Canal no soportado: " + tipo);
        canal.enviar(destinatario, mensaje);
        logger.registrar(tipo, destinatario, mensaje);
    }
}
```

---

### Pruebas unitarias

```java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificadorTest {

    private LoggerNotificaciones loggerMock;
    private CanalEmail emailMock;
    private CanalSMS smsMock;
    private Notificador notificador;

    @BeforeEach
    void setUp() {
        loggerMock = mock(LoggerNotificaciones.class);
        emailMock = mock(CanalEmail.class);
        smsMock = mock(CanalSMS.class);
        when(emailMock.getTipo()).thenReturn("EMAIL");
        when(smsMock.getTipo()).thenReturn("SMS");
        notificador = new Notificador(loggerMock, emailMock, smsMock);
    }

    @Test
    void debeEnviarEmailCuandoTipoEsEmail() {
        notificador.enviar("EMAIL", "juan@openlib.com", "Libro publicado");
        verify(emailMock).enviar("juan@openlib.com", "Libro publicado");
    }

    @Test
    void debeEnviarSMSCuandoTipoEsSMS() {
        notificador.enviar("SMS", "+573001234567", "Tu pedido llegó");
        verify(smsMock).enviar("+573001234567", "Tu pedido llegó");
    }

    @Test
    void debeRegistrarEnLogDespuesDeEnviar() {
        notificador.enviar("EMAIL", "juan@openlib.com", "Mensaje");
        verify(loggerMock).registrar("EMAIL", "juan@openlib.com", "Mensaje");
    }

    @Test
    void debeLanzarExcepcionParaCanalNoRegistrado() {
        assertThrows(IllegalArgumentException.class,
            () -> notificador.enviar("WHATSAPP", "destino", "mensaje"));
    }

    @Test
    void debePermitirAgregarNuevoCanalEnTiempoDeEjecucion() {
        CanalWhatsApp whatsapp = mock(CanalWhatsApp.class);
        when(whatsapp.getTipo()).thenReturn("WHATSAPP");

        notificador.registrar(whatsapp);
        notificador.enviar("WHATSAPP", "+573009999999", "Oferta");

        verify(whatsapp).enviar("+573009999999", "Oferta");
    }

    @Test
    void noDebeEnviarSMSCuandoSeEnviaEmail() {
        notificador.enviar("EMAIL", "juan@openlib.com", "Mensaje");
        verify(smsMock, never()).enviar(any(), any());
    }
}
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt fue efectivo en exigir integración de múltiples conceptos en una sola respuesta. 

#### 2. ¿Qué se puede mejorar?

El prompt no especificó qué hacer cuando el logger falla — el LLM simplemente imprime en stderr, lo cual no es apropiado en producción. 

#### 3. Respuesta final

El diseño del LLM es bueno y demuestra buena aplicación de Strategy + SRP. Lo que modificaria para producción en OpenLib Market:

Mejora 1 — Pruebas contra interfaz, no contra clase concreta:

// Mejor: mock de la interfaz, no de la clase concreta
CanalNotificacion emailMock = mock(CanalNotificacion.class);
when(emailMock.getTipo()).thenReturn("EMAIL");
```

Mejora 2 — Logger no debe fallar silenciosamente:

public void registrar(String tipo, String destinatario, String mensaje) {
    try {
        Files.write(rutaLog, ...);
    } catch (IOException e) {
        // En producción: re-intentar, o enviar a un sistema de alertas
        // NUNCA ignorar silenciosamente un error de log de auditoría
        throw new RuntimeException("Falla crítica en log de auditoría", e);
    }
}
```
