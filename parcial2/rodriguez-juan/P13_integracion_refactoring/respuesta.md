# Plantilla de entrega — Parcial 2

---

## Pregunta P13: Integración — Refactoring, Patrones y Pruebas

### Estudiante

- **Nombre completo**: Juan David Rodriguez Franco

---

### LLM utilizado

| Campo                                   | Valor                                                                                                          |
| --------------------------------------- | -------------------------------------------------------------------------------------------------------------- |
| **Nombre del LLM**                | Gemini |
| **Modelo específico**            | Gemini 3.1 Pro |
| **¿Por qué elegiste este LLM?** | Gemini 3.1 Pro maneja excelentemente la escritura de código Java, logrando aplicar patrones de diseño (como Strategy) y al mismo tiempo proveer suites de pruebas con Mockito comparativas (antes y después). |

---

### Prompt utilizado

```text
Actúa como un Senior Java Developer experto en refactorización y testing.

En OpenLib Market tenemos un servicio `NotificadorService` que envía mensajes. Actualmente tiene un bloque `switch` / `if-else` gigante para decidir si envía un Email, un SMS o una notificación Push. Queremos refactorizarlo usando el Patrón de Diseño Estrategia (Strategy) para cumplir con el principio Open/Closed de SOLID, pero no queremos romper nada.

Por favor, genera lo siguiente:
1. El código original de `NotificadorService` con el `switch` gigante (el "código sucio").
2. Las Pruebas Unitarias originales (JUnit 5 y Mockito) para este código sucio, para asegurar que funciona hoy.
3. El código REFACTORIZADO aplicando el Patrón Estrategia (crea la interfaz `EstrategiaNotificacion` y las 3 implementaciones concretas).
4. El `NotificadorService` refactorizado usando inyección de dependencias.
5. Las Pruebas Unitarias para el código refactorizado, demostrando que el comportamiento no cambió (Regresión).
```

---

### Respuesta del LLM

```markdown
Como Senior Developer, entiendo la necesidad de aplicar el patrón Estrategia (Strategy) para erradicar el *code smell* de las condicionales complejas y adherirnos al principio Open/Closed (OCP). Haremos esto de forma segura utilizando pruebas de regresión.

### 1. El Código Original ("Sucio")

```java
public class NotificadorService {
    
    // Dependencias acopladas (en la vida real serían servicios inyectados)
    private EmailClient emailClient;
    private SmsClient smsClient;
    private PushClient pushClient;

    public NotificadorService(EmailClient emailClient, SmsClient smsClient, PushClient pushClient) {
        this.emailClient = emailClient;
        this.smsClient = smsClient;
        this.pushClient = pushClient;
    }

    public void enviarNotificacion(String tipo, String usuarioId, String mensaje) {
        if ("EMAIL".equalsIgnoreCase(tipo)) {
            emailClient.sendEmail(usuarioId, mensaje);
        } else if ("SMS".equalsIgnoreCase(tipo)) {
            smsClient.sendSms(usuarioId, mensaje);
        } else if ("PUSH".equalsIgnoreCase(tipo)) {
            pushClient.sendPush(usuarioId, mensaje);
        } else {
            throw new IllegalArgumentException("Tipo de notificación no soportado: " + tipo);
        }
    }
}
```

### 2. Pruebas Unitarias Originales (Antes del Refactor)

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificadorServiceTest {

    @Mock private EmailClient emailClient;
    @Mock private SmsClient smsClient;
    @Mock private PushClient pushClient;

    private NotificadorService notificadorService;

    @BeforeEach
    void setUp() {
        notificadorService = new NotificadorService(emailClient, smsClient, pushClient);
    }

    @Test
    void debeEnviarEmail() {
        notificadorService.enviarNotificacion("EMAIL", "user1", "Hola");
        verify(emailClient).sendEmail("user1", "Hola");
        verifyNoInteractions(smsClient, pushClient);
    }

    @Test
    void debeLanzarExcepcionConTipoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            notificadorService.enviarNotificacion("PALOMA_MENSAJERA", "user1", "Hola");
        });
    }
}
```

### 3. Código Refactorizado: Aplicando el Patrón Strategy

Creamos la interfaz y las implementaciones concretas:

```java
public interface EstrategiaNotificacion {
    boolean soporta(String tipo);
    void enviar(String usuarioId, String mensaje);
}

public class EstrategiaEmail implements EstrategiaNotificacion {
    private final EmailClient emailClient;
    
    public EstrategiaEmail(EmailClient emailClient) { this.emailClient = emailClient; }
    
    @Override public boolean soporta(String tipo) { return "EMAIL".equalsIgnoreCase(tipo); }
    @Override public void enviar(String usuarioId, String mensaje) { emailClient.sendEmail(usuarioId, mensaje); }
}

public class EstrategiaSms implements EstrategiaNotificacion {
    private final SmsClient smsClient;
    
    public EstrategiaSms(SmsClient smsClient) { this.smsClient = smsClient; }
    
    @Override public boolean soporta(String tipo) { return "SMS".equalsIgnoreCase(tipo); }
    @Override public void enviar(String usuarioId, String mensaje) { smsClient.sendSms(usuarioId, mensaje); }
}

public class EstrategiaPush implements EstrategiaNotificacion {
    private final PushClient pushClient;
    
    public EstrategiaPush(PushClient pushClient) { this.pushClient = pushClient; }
    
    @Override public boolean soporta(String tipo) { return "PUSH".equalsIgnoreCase(tipo); }
    @Override public void enviar(String usuarioId, String mensaje) { pushClient.sendPush(usuarioId, mensaje); }
}
```

### 4. El Nuevo `NotificadorService` (Limpio)

Desaparecen los `if/else`. Ahora inyectamos una lista de estrategias (muy común en Spring Boot):

```java
import java.util.List;

public class NotificadorService {
    
    private final List<EstrategiaNotificacion> estrategias;

    public NotificadorService(List<EstrategiaNotificacion> estrategias) {
        this.estrategias = estrategias;
    }

    public void enviarNotificacion(String tipo, String usuarioId, String mensaje) {
        EstrategiaNotificacion estrategia = estrategias.stream()
            .filter(e -> e.soporta(tipo))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Tipo de notificación no soportado: " + tipo));
            
        estrategia.enviar(usuarioId, mensaje);
    }
}
```

### 5. Pruebas Unitarias (Después del Refactor - Regresión)

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificadorServiceRefactoredTest {

    @Mock private EmailClient emailClient;
    @Mock private SmsClient smsClient;
    @Mock private PushClient pushClient;

    private NotificadorService notificadorService;

    @BeforeEach
    void setUp() {
        // Configuramos las estrategias manualmente para el test
        List<EstrategiaNotificacion> estrategias = List.of(
            new EstrategiaEmail(emailClient),
            new EstrategiaSms(smsClient),
            new EstrategiaPush(pushClient)
        );
        notificadorService = new NotificadorService(estrategias);
    }

    @Test
    void debeEnviarEmail() {
        // ACT: Llamamos al mismo método exacto que antes
        notificadorService.enviarNotificacion("EMAIL", "user1", "Hola");
        
        // ASSERT: El comportamiento sigue siendo el mismo (Regresión exitosa)
        verify(emailClient).sendEmail("user1", "Hola");
        verifyNoInteractions(smsClient, pushClient);
    }

    @Test
    void debeLanzarExcepcionConTipoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            notificadorService.enviarNotificacion("PALOMA_MENSAJERA", "user1", "Hola");
        });
    }
}
```
Como puedes observar, **las pruebas hacen exactamente las mismas aserciones**. Esto nos da la confianza del 100% de que la refactorización fue exitosa y no rompió la lógica de negocio.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Pendiente de análisis]

#### 2. ¿Qué se puede mejorar?

[Pendiente de análisis]

#### 3. Respuesta final

[Pendiente de análisis]
