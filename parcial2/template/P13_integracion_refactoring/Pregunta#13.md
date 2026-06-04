# Plantilla de entrega — Parcial 2
---

## Pregunta [13]: [P13_integracion_refactoring]

### Estudiante
- **Nombre completo**: Samuel López Gómez

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** |Sonnet 4.6 |
| **¿Por qué elegiste este LLM?** | La IA dice "Te sugiero usar Claude 3.5 Sonnet o GPT-4o. Ambos son excelentes para este tipo de refactoring completo (código + patrones + tests)." |

---

### Prompt utilizado

Actúa como un arquitecto de software senior revisando el código del proyecto OpenLib Market (Java 21). Un desarrollador junior escribió la siguiente clase `Notificador` que tiene serios problemas de diseño:

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

Necesito que refactorices esta clase integralmente. Debes:
1. Identificar detalladamente qué principios SOLID se están violando.
2. Aplicar al menos un patrón de diseño clásico (GoF) para resolver el problema de los múltiples canales de envío.
3. Separar completamente la responsabilidad del "logging en archivo" de la lógica de negocio de las notificaciones.
4. Escribir el código Java 21 refactorizado (interfaces y clases).
5. Escribir una suite de pruebas unitarias usando JUnit 5 y Mockito para la nueva estructura.

Restricciones:
- El nuevo diseño debe permitir agregar un nuevo canal (ej. WhatsApp) sin modificar el código del orquestador existente (cumplir OCP).
- Usa POO pura, sin frameworks pesados como Spring para la lógica central.

Formato de salida esperado: Markdown con "Diagnóstico SOLID", "Patrón Seleccionado", "Código Refactorizado", y "Pruebas Unitarias".

```

---

### Respuesta del LLM
// 3. Abstracción del Logging
public interface AuditorNotificaciones {
    void registrar(String tipo, String destinatario, String mensaje);
}

public class FileAuditor implements AuditorNotificaciones {
    @Override
    public void registrar(String tipo, String destinatario, String mensaje) {
        try {
            Files.write(Paths.get("/var/log/openlib/notificaciones.log"),
                (tipo + "|" + destinatario + "|" + mensaje + "\n").getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error al escribir log: " + e.getMessage());
        }
    }
}

// 4. Orquestador (Context)
public class Notificador {
    private final List<NotificacionStrategy> estrategias;
    private final AuditorNotificaciones auditor;

    public Notificador(List<NotificacionStrategy> estrategias, AuditorNotificaciones auditor) {
        this.estrategias = estrategias;
        this.auditor = auditor;
    }

    public void enviar(String tipo, String destinatario, String mensaje) {
        NotificacionStrategy estrategia = estrategias.stream()
            .filter(e -> e.soporta(tipo))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Tipo de notificación no soportado: " + tipo));
        
        estrategia.enviar(destinatario, mensaje);
        auditor.registrar(tipo, destinatario, mensaje);
    }
}
Pruebas Unitarias

Java
@ExtendWith(MockitoExtension.class)
class NotificadorTest {

    @Mock private AuditorNotificaciones auditor;
    @Mock private NotificacionStrategy strategyMock;

    private Notificador notificador;

    @BeforeEach
    void setUp() {
        when(strategyMock.soporta("EMAIL")).thenReturn(true);
        notificador = new Notificador(List.of(strategyMock), auditor);
    }

    @Test
    void deberiaDelegarEnvioYRegistrarLog() {
        notificador.enviar("EMAIL", "juan@test.com", "Hola");

        verify(strategyMock).enviar("juan@test.com", "Hola");
        verify(auditor).registrar("EMAIL", "juan@test.com", "Hola");
    }

    @Test
    void deberiaLanzarExcepcionSiNoHayEstrategiaSoportada() {
        assertThrows(IllegalArgumentException.class, () -> {
            notificador.enviar("WHATSAPP", "juan@test.com", "Hola");
        });
    }
}
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt funcionó bien al establecer tres restricciones técnicas claras: aplicar un patrón GoF, separar la capa de logging de la lógica de negocio, y prohibir el uso de Spring. Estas limitantes obligaron a la IA a implementar el patrón Strategy usando inyección de dependencias en Java puro. Al forzar la creación de la interfaz AuditorNotificaciones, el modelo tuvo que aplicar el Principio de Inversión de Dependencias (DIP) aislando correctamente el dominio. Esta decisión estructural hizo que el código final fuera realmente testable, como se evidenció en la suite de validación con Mockito.

#### 2. ¿Qué se puede mejorar?

Aunque la limpieza arquitectónica es notable respecto al código original, el diseño propuesto por el modelo introduce cuellos de botella y fallas de gestión de errores que no pasarían una revisión formal para producción:
Primero, introdujo logging bloqueante (I/O síncrono). La IA delegó correctamente la escritura a la clase FileAuditor, pero ejecutó el I/O en la misma secuencia. En un servidor real, invocar Files.write en el mismo hilo que atiende la solicitud de notificación va a generar latencias severas por culpa del disco duro. Un pico de tráfico colapsaría los hilos de la aplicación esperando operaciones de disco.

Segundo, ignoró las excepciones (manejo silencioso). Cuando el método Files.write falla, el modelo captura la IOException y simplemente arroja un System.err.println(). Silenciar excepciones de infraestructura ("tragarse el error") sin escalar la alerta a un sistema de monitoreo o propagarla a las capas superiores, es un anti-patrón de desarrollo grave.

Tercero, creó acoplamiento en la ejecución. En el bloque del notificador, la IA manda a ejecutar estrategia.enviar() y en la línea inmediatamente inferior llama a auditor.registrar(). Si la integración falla (por ejemplo, si la API externa arroja timeout) y lanza una excepción, el flujo se interrumpe y el bloque del logger jamás se ejecuta. Perdimos por completo la trazabilidad de la falla.

#### 3. Respuesta final
A nivel teórico, el análisis inicial de los principios SOLID es acertado. La clase monolítica original violaba claramente el SRP (mezclaba I/O con reglas de dominio), el OCP (con el uso prolongado de if-else) y el DIP (dependiendo de instanciaciones concretas). Implementar el patrón Strategy es la respuesta de libro de texto, y la abstracción del auditor asegura que el notificador quede cerrado a modificaciones pero abierto a nuevas implementaciones.

Sin embargo, para presentar y entregar esta clase en un proyecto formal universitario, yo le haría tres ajustes estructurales. Primero, implementaría el FileAuditor de forma asíncrona (usando un pool de hilos o colas en memoria) para liberar el hilo principal. Segundo, reportaría la IOException a un recolector de métricas centralizado en lugar de imprimir en consola. Y tercero, envolvería el envío de la notificación en un bloque try-catch-finally, garantizando que el registro del auditor se ejecute siempre para poder investigar los fallos de red.