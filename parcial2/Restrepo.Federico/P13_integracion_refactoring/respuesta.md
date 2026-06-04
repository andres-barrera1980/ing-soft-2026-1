# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta 13: Integración — Refactoring de Notificador

### Estudiante
- **Nombre completo**: Federico Restrepo

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Gemini 3.1 Pro |
| **¿Por qué elegiste este LLM?** | Usé Gemini 3.1 Pro en antigravity, ya que ofrece relativamente una buena cantidad de tokens y ofrece un manejo visual de los archivos que me gusta. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
Actúa como un desarrollador Senior en Java.

Contexto: Un desarrollador junior escribió la siguiente clase para el sistema de notificaciones de OpenLib Market:

public class Notificador {
    public void enviar(String tipo, String destinatario, String mensaje) {
        if (tipo.equals("EMAIL")) {
            // Conectar a SMTP
            System.out.println("Enviando EMAIL a " + destinatario + ": " + mensaje);
        } else if (tipo.equals("SMS")) {
            // Conectar a API de Twilio
            System.out.println("Enviando SMS a " + destinatario + ": " + mensaje);
        } else if (tipo.equals("PUSH")) {
            // Conectar a Firebase
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

Tarea: Refactoriza esta clase integralmente resolviendo sus problemas de diseño. Debes:
1. Identificar e indicar brevemente qué principios SOLID viola el código actual.
2. Aplicar al menos un patrón de diseño GoF para solucionar la cadena de if-else.
3. Separar la responsabilidad del registro de logs.
4. Escribir el código Java 21 refactorizado completo.
5. Incluir pruebas unitarias con JUnit 5 y Mockito para demostrar que la nueva estructura es fácilmente testeable, algo que la clase original no permitía.

Formato de salida: markdown, con explicaciones breves y bloques de código Java.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

*(1 iteración)*

---

### Análisis del código original y principios SOLID violados

El código original viola principalmente dos principios SOLID:

1.  **SRP (Principio de Responsabilidad Única):** La clase hace demasiadas cosas. Se encarga de enviar correos, enviar SMS, enviar notificaciones Push y, además, maneja infraestructura escribiendo directamente en un archivo de log local en disco.
2.  **OCP (Principio de Abierto/Cerrado):** Si mañana necesitamos agregar notificaciones por "WHATSAPP", tendríamos que modificar la clase `Notificador` para añadir otro `else if`. La clase no está cerrada a modificaciones.

### Solución: Patrón Strategy + Inyección de Dependencias

Para eliminar los `if-else` acoplados, aplicaremos el **Patrón Strategy**. Cada tipo de notificación será una estrategia separada.
Para el logging, extraeremos esa responsabilidad a una interfaz `LoggerPort` (aplicando **DIP - Principio de Inversión de Dependencias**), permitiendo inyectar el comportamiento en lugar de escribir a disco de forma rígida.

#### Código Refactorizado (Java 21)

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

// 1. Interfaz para la Estrategia (Strategy)
public interface EstrategiaNotificacion {
    void enviar(String destinatario, String mensaje);
}

// 2. Estrategias Concretas
public class EmailNotificacion implements EstrategiaNotificacion {
    @Override
    public void enviar(String destinatario, String mensaje) {
        // Conectar a SMTP real
        System.out.println("Enviando EMAIL a " + destinatario + ": " + mensaje);
    }
}

public class SmsNotificacion implements EstrategiaNotificacion {
    @Override
    public void enviar(String destinatario, String mensaje) {
        // Conectar a Twilio
        System.out.println("Enviando SMS a " + destinatario + ": " + mensaje);
    }
}

public class PushNotificacion implements EstrategiaNotificacion {
    @Override
    public void enviar(String destinatario, String mensaje) {
        // Conectar a Firebase
        System.out.println("Enviando PUSH a " + destinatario + ": " + mensaje);
    }
}

// 3. Extracción del Logger (SRP y DIP)
public interface AuditoriaLog {
    void registrarEnvio(String tipo, String destinatario, String mensaje);
}

public class FileAuditoriaLog implements AuditoriaLog {
    private final String logPath;

    public FileAuditoriaLog(String logPath) {
        this.logPath = logPath;
    }

    @Override
    public void registrarEnvio(String tipo, String destinatario, String mensaje) {
        try {
            Files.write(Paths.get(logPath),
                (tipo + "|" + destinatario + "|" + mensaje + "\n").getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            // En un sistema real usaríamos SLF4J
            System.err.println("Fallo al escribir log: " + e.getMessage());
        }
    }
}

// 4. Contexto Refactorizado (El nuevo Notificador)
public class NotificadorService {
    
    private final Map<String, EstrategiaNotificacion> estrategias;
    private final AuditoriaLog logger;

    // Inyección de dependencias
    public NotificadorService(Map<String, EstrategiaNotificacion> estrategias, AuditoriaLog logger) {
        this.estrategias = estrategias;
        this.logger = logger;
    }

    public void enviar(String tipo, String destinatario, String mensaje) {
        EstrategiaNotificacion estrategia = estrategias.get(tipo.toUpperCase());
        
        if (estrategia == null) {
            throw new IllegalArgumentException("Tipo de notificación no soportado: " + tipo);
        }

        estrategia.enviar(destinatario, mensaje);
        logger.registrarEnvio(tipo, destinatario, mensaje);
    }
}
```

---

### Pruebas Unitarias (JUnit 5 + Mockito)

El código original era imposible de testear unitariamente porque el disco duro (`Files.write`) estaba acoplado dentro del método. Con la nueva arquitectura, podemos inyectar mocks fácilmente.

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - NotificadorService")
class NotificadorServiceTest {

    @Mock private EstrategiaNotificacion emailMock;
    @Mock private EstrategiaNotificacion smsMock;
    @Mock private AuditoriaLog loggerMock;

    private NotificadorService notificadorService;

    @BeforeEach
    void setUp() {
        // Configuramos el mapa de estrategias inyectado
        Map<String, EstrategiaNotificacion> estrategias = Map.of(
            "EMAIL", emailMock,
            "SMS", smsMock
        );
        notificadorService = new NotificadorService(estrategias, loggerMock);
    }

    @Test
    @DisplayName("Debe delegar correctamente a la estrategia EMAIL y registrar el log")
    void enviar_conTipoEmail_delegaAEmailMock() {
        // Act
        notificadorService.enviar("EMAIL", "juan@test.com", "Hola Juan");

        // Assert
        verify(emailMock, times(1)).enviar("juan@test.com", "Hola Juan");
        verify(smsMock, never()).enviar(anyString(), anyString());
        
        // Verifica que se haya guardado el log
        verify(loggerMock, times(1)).registrarEnvio("EMAIL", "juan@test.com", "Hola Juan");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el tipo de notificación no existe (Ej. WHATSAPP)")
    void enviar_conTipoInvalido_lanzaExcepcion() {
        // Act & Assert
        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class, 
            () -> notificadorService.enviar("WHATSAPP", "12345", "Hola")
        );
        
        assert(excepcion.getMessage().contains("Tipo de notificación no soportado"));
        
        // El log no debería registrarse si falla
        verify(loggerMock, never()).registrarEnvio(anyString(), anyString(), anyString());
    }
}
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?
Pegar el código completo fue clave, el LLM tuvo el mismo punto de partida y el refactoring fue preciso. Lo mejor fue la justificación de por qué el código original es intesteable: Files.write acoplado dentro del método es el problema concreto que impide mockear, no lo dejó como afirmación suelta. El test de tipo inválido también estuvo bien pensado porque verifica que el log no se registre cuando falla la estrategia.
#### 2. ¿Qué se puede mejorar?
Solo identificó SRP y OCP pero ignoró DIP, que también se viola porque el código depende directamente de Files y Paths, implementaciones concretas de infraestructura. Curioso porque después lo soluciona con AuditoriaLog sin haberlo nombrado como problema. Los tests también quedaron incompletos, solo cubre EMAIL y tipo inválido, falta SMS, verificar que el logger recibe los parámetros correctos, y qué pasa si la estrategia lanza una excepción interna. Además el Map.of en el setUp no incluye PUSH aunque existe PushNotificacion, lo cual es confuso.
#### 3. Respuesta final
El código refactorizado es limpio y testeable. Para la próxima le pediría que identifique DIP desde el análisis inicial, complete los casos de prueba y que el mapa de estrategias en los tests refleje todo lo que existe en el código.          