# Plantilla de entrega — Parcial 2

---

## Pregunta 13: Refactorizar aplicando SOLID + patrón de diseño + pruebas

### Estudiante
- **Nombre completo**: Samuel Iregui

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Antigravity |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Elegi este modelo por comodidad propia y debido a que tengo pago gemini pro y ya tengo todo bien organizado para los diferentes trabajos |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un arquitecto de software senior y especialista en refactoring. Tengo la siguiente clase Notificador en Java que tiene serios problemas de diseño:

public class Notificador {
    public void enviar(String tipo, String destinatario, String mensaje) {
        if (tipo.equals("EMAIL")) {
            // Conectar a SMTP
            // Enviar correo
            System.out.println("Enviando EMAIL a " + destinatario + ": " + mensaje);
        } else if (tipo.equals("SMS")) {
            // Conectar a API de Twilio
            // Enviar SMS
            System.out.println("Enviando SMS a " + destinatario + ": " + mensaje);
        } else if (tipo.equals("PUSH")) {
            // Conectar a Firebase
            // Enviar notificación push
            System.out.println("Enviando PUSH a " + destinatario + ": " + mensaje);
        }
        // Registrar en archivo de log
        try {
            Files.write(Paths.get("/var/log/openlib/notificaciones.log"),
                (tipo + "|" + destinatario + "|" + mensaje + "\n").getBytes(),
                StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

Necesito que:
1. Identifiques qué principios SOLID se están violando en esta implementación.
2. Refactorices el código completo utilizando Java 21, aplicando al menos un principio SOLID y al menos un patrón de diseño clásico que permita añadir nuevos canales de notificación sin modificar la clase existente.
3. Separes limpiamente la lógica de logging de la lógica de envío de notificaciones.
4. Escribas pruebas unitarias completas para el nuevo diseño usando JUnit 5 y Mockito.

Formato de salida esperado: Markdown estructurado con secciones de "Principios SOLID violados", "Estrategia de Refactoring", "Código Java 21 Refactorizado" y "Pruebas Unitarias JUnit 5".
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

### Principios SOLID violados

1.  **Principio de Responsabilidad Única (SRP)**: La clase `Notificador` tiene más de una razón para cambiar. Se encarga de la lógica específica de múltiples canales de comunicación (EMAIL, SMS, PUSH) y, además, maneja la persistencia física del log en el sistema de archivos.
2.  **Principio de Abierto/Cerrado (OCP)**: Si mañana queremos enviar notificaciones por WhatsApp, estamos obligados a abrir la clase `Notificador` y añadir otro bloque `else if` al método `enviar()`, lo que puede introducir errores en los canales ya probados.
3.  **Principio de Inversión de Dependencias (DIP)**: La clase depende directamente de una ruta de archivo física quemada en el código (`/var/log/openlib/notificaciones.log`) y de llamadas estáticas de `Files.write()`, impidiendo cambiar el destino de logs o mockear el proceso en pruebas unitarias.

---

### Estrategia de Refactoring

*   **Patrón Strategy (Estrategia)**: Se encapsula la lógica de envío de cada canal en su propia clase implementando la interfaz `CanalNotificacion`.
*   **Patrón Decorator (Decorador)**: Para separar el logging de forma no intrusiva. Creamos un decorador (`NotificadorConLogging`) que envuelve la interfaz del servicio, ejecuta el envío y posteriormente registra el log a través de una abstracción de logging.

---

### Código Java 21 Refactorizado

#### 1. Abstracciones e Implementaciones de Canales (Strategy)
```java
public interface CanalNotificacion {
    void despachar(String destinatario, String mensaje);
    boolean soporta(String tipo);
}

public class CanalEmail implements CanalNotificacion {
    @Override
    public void despachar(String destinatario, String mensaje) {
        System.out.println("Enviando EMAIL a " + destinatario + ": " + mensaje);
    }
    @Override
    public boolean soporta(String tipo) { return "EMAIL".equalsIgnoreCase(tipo); }
}

public class CanalSms implements CanalNotificacion {
    @Override
    public void despachar(String destinatario, String mensaje) {
        System.out.println("Enviando SMS a " + destinatario + ": " + mensaje);
    }
    @Override
    public boolean soporta(String tipo) { return "SMS".equalsIgnoreCase(tipo); }
}

public class CanalPush implements CanalNotificacion {
    @Override
    public void despachar(String destinatario, String mensaje) {
        System.out.println("Enviando PUSH a " + destinatario + ": " + mensaje);
    }
    @Override
    public boolean soporta(String tipo) { return "PUSH".equalsIgnoreCase(tipo); }
}
```

#### 2. Servicio Base de Notificaciones
```java
import java.util.List;

public interface NotificadorService {
    void enviar(String tipo, String destinatario, String mensaje);
}

public class NotificadorServiceImpl implements NotificadorService {
    private final List<CanalNotificacion> canales;

    public NotificadorServiceImpl(List<CanalNotificacion> canales) {
        this.canales = canales;
    }

    @Override
    public void enviar(String tipo, String destinatario, String mensaje) {
        CanalNotificacion canal = canales.stream()
                .filter(c -> c.soporta(tipo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Canal no soportado: " + tipo));
        canal.despachar(destinatario, mensaje);
    }
}
```

#### 3. Abstracción de Log y Decorador (Decorator)
```java
public interface LogRepository {
    void registrar(String tipo, String destinatario, String mensaje);
}

public class NotificadorConLogging implements NotificadorService {
    private final NotificadorService decorado;
    private final LogRepository logRepository;

    public NotificadorConLogging(NotificadorService decorado, LogRepository logRepository) {
        this.decorado = decorado;
        this.logRepository = logRepository;
    }

    @Override
    public void enviar(String tipo, String destinatario, String mensaje) {
        decorado.enviar(tipo, destinatario, mensaje);
        logRepository.registrar(tipo, destinatario, mensaje);
    }
}
```

---

### Pruebas Unitarias JUnit 5

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificadorTest {

    @Mock private CanalNotificacion canalEmail;
    @Mock private CanalNotificacion canalSms;
    @Mock private LogRepository logRepository;

    private NotificadorService notificadorService;
    private NotificadorService notificadorDecorado;

    @BeforeEach
    void setUp() {
        when(canalEmail.soporta("EMAIL")).thenReturn(true);
        when(canalSms.soporta("SMS")).thenReturn(true);

        notificadorService = new NotificadorServiceImpl(List.of(canalEmail, canalSms));
        notificadorDecorado = new NotificadorConLogging(notificadorService, logRepository);
    }

    @Test
    void enviar_CanalSoportado_EjecutaDespachoYLog() {
        notificadorDecorado.enviar("EMAIL", "user@test.com", "Hola Mundo");

        verify(canalEmail).despachar("user@test.com", "Hola Mundo");
        verify(logRepository).registrar("EMAIL", "user@test.com", "Hola Mundo");
        verifyNoInteractions(canalSms);
    }

    @Test
    void enviar_CanalNoSoportado_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> 
            notificadorDecorado.enviar("WHATSAPP", "3001234567", "Hola")
        );
        verifyNoInteractions(logRepository);
    }
}
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt expone claramente las falencias del código original (condicionales anidados, logging acoplado, rutas de archivo quemadas) y define restricciones de diseño específicas (SOLID, patrones de diseño y pruebas unitarias con JUnit 5) que obligan al LLM a ir por una arquitectura limpia.

#### 2. ¿Qué se puede mejorar?

Se pudo solicitar explícitamente cómo manejar la ruta física del archivo `/var/log/...` en producción para sistemas Windows/Linux sin causar problemas de portabilidad (por ejemplo, mediante configuración de propiedades externas o inyección de dependencias), lo que habría obligado al LLM a resolver este acoplamiento con infraestructura de configuración real.

#### 3. Respuesta final

En conclusión, la respuesta del LLM es de gran calidad. Identificó correctamente las violaciones a SRP, OCP y DIP, y estructuró un refactoring limpio:
- El uso de **Strategy** para los canales elimina los condicionales anidados y permite agregar nuevos canales (como WhatsApp) de forma extensible y sin modificar las clases base.
- El uso de **Decorator** para separar el Logging de las notificaciones es una excelente decisión de diseño (sigue SRP y OCP de forma elegante), asegurando que el servicio de negocio principal (`NotificadorServiceImpl`) no se entere de cómo o dónde se guardan los archivos de registro.

**Mejoras indispensables para una versión en producción en OpenLib Market:**
1.  **Asincronía (I/O no bloqueante)**: Los canales de envío real (SMTP, Firebase, Twilio) hacen llamadas de red bloqueantes que demoran. Ejecutar esto de forma síncrona en el hilo principal del request afectaría el rendimiento del frontend. En producción, la ejecución de los canales debería ser asíncrona (ej: inyectando un `Executor` o usando `@Async` en Spring Boot).
2.  **Tolerancia a fallos en el Decorador de Logging**: En el decorador propuesto, si `logRepository.registrar()` falla (por ejemplo, disco lleno o excepción de escritura), la excepción se propagará hacia arriba. Esto significa que la acción del usuario podría fallar o reportarse como errónea aunque el mensaje *sí se haya enviado por correo*. Debemos añadir un bloque `try-catch` dentro del decorador de logging para que los fallos del log no afecten la transacción de negocio principal.
3.  **Portabilidad de la Ruta de Logs**: El log no debe escribir directamente en rutas quemadas `/var/log/...` (lo cual fallaría en entornos Windows). La clase que implemente `LogRepository` debe inyectar la ruta desde las propiedades de configuración (`application.properties` en Spring) o, mejor aún, delegar esta tarea a un logger estándar como SLF4J/Logback.
