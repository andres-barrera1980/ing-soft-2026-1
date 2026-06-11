## Pregunta [13]: [Integracion refactoeing]

### Estudiante
- **Nombre completo**: [Marlon Garcia]

---

# Respuesta sin IA

La clase original tiene problemas de diseño porque mezcla varias responsabilidades en un solo método: decide el tipo de notificación, envía EMAIL/SMS/PUSH y además registra en un archivo de log. También usa condicionales if, else-if, por lo que cada nuevo canal obliga a modificar la clase.

El refactor aplica principalmente:

* Sigle responsability: cada clase tiene una sola responsabilidad.
* abierto-cerrado: se pueden agregar nuevos canales sin modificar el servicio principal.
* inversión de dependencias: el servicio depende de interfaces, no de implementaciones concretas.

Patrón de diseño usado: Strategy, porque cada tipo de notificación se maneja como una estrategia diferente.

---

## Código refactorizado

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

// ==========================
// ENUM
// ==========================

enum TipoNotificacion {
    EMAIL,
    SMS,
    PUSH
}

// STRATEGY

interface CanalNotificacion {
    void enviar(String destinatario, String mensaje);
}

// Estrategia Email

class EmailNotificacion implements CanalNotificacion {

    @Override
    public void enviar(String destinatario, String mensaje) {
        // Conectar a SMTP
        // Enviar correo
        System.out.println("Enviando EMAIL a " + destinatario + ": " + mensaje);
    }
}

// Estrategia SMS

class SmsNotificacion implements CanalNotificacion {

    @Override
    public void enviar(String destinatario, String mensaje) {
        // Conectar a API de Twilio
        // Enviar SMS
        System.out.println("Enviando SMS a " + destinatario + ": " + mensaje);
    }
}

class PushNotificacion implements CanalNotificacion {

    @Override
    public void enviar(String destinatario, String mensaje) {
        // Conectar a Firebase
        // Enviar notificación push
        System.out.println("Enviando PUSH a " + destinatario + ": " + mensaje);
    }
}


interface RegistroNotificacion {
    void registrar(TipoNotificacion tipo, String destinatario, String mensaje);
}

class ArchivoRegistroNotificacion implements RegistroNotificacion {

    private final Path rutaArchivo;

    public ArchivoRegistroNotificacion(Path rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    @Override
    public void registrar(TipoNotificacion tipo, String destinatario, String mensaje) {
        String linea = tipo + "|" + destinatario + "|" + mensaje + System.lineSeparator();

        try {
            Files.write(
                    rutaArchivo,
                    linea.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new RuntimeException("Error registrando notificación", e);
        }
    }
}


public class NotificadorService {

    private final Map<TipoNotificacion, CanalNotificacion> canales;
    private final RegistroNotificacion registroNotificacion;

    public NotificadorService(
            Map<TipoNotificacion, CanalNotificacion> canales,
            RegistroNotificacion registroNotificacion
    ) {
        this.canales = canales;
        this.registroNotificacion = registroNotificacion;
    }

    public void enviar(TipoNotificacion tipo, String destinatario, String mensaje) {
        validarDatos(tipo, destinatario, mensaje);

        CanalNotificacion canal = canales.get(tipo);

        if (canal == null) {
            throw new IllegalArgumentException("Tipo de notificación no soportado: " + tipo);
        }

        canal.enviar(destinatario, mensaje);
        registroNotificacion.registrar(tipo, destinatario, mensaje);
    }

    private void validarDatos(TipoNotificacion tipo, String destinatario, String mensaje) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de notificación es obligatorio");
        }

        if (destinatario == null || destinatario.isBlank()) {
            throw new IllegalArgumentException("El destinatario es obligatorio");
        }

        if (mensaje == null || mensaje.isBlank()) {
            throw new IllegalArgumentException("El mensaje es obligatorio");
        }
    }
}
```

---


# Pruebas unitarias con JUnit 5 y Mockito

```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificadorServiceTest {

    private CanalNotificacion emailCanal;
    private CanalNotificacion smsCanal;
    private CanalNotificacion pushCanal;
    private RegistroNotificacion registroNotificacion;

    private NotificadorService notificadorService;

    @BeforeEach
    void setUp() {
        emailCanal = mock(CanalNotificacion.class);
        smsCanal = mock(CanalNotificacion.class);
        pushCanal = mock(CanalNotificacion.class);
        registroNotificacion = mock(RegistroNotificacion.class);

        Map<TipoNotificacion, CanalNotificacion> canales = new HashMap<>();
        canales.put(TipoNotificacion.EMAIL, emailCanal);
        canales.put(TipoNotificacion.SMS, smsCanal);
        canales.put(TipoNotificacion.PUSH, pushCanal);

        notificadorService = new NotificadorService(canales, registroNotificacion);
    }

    @Test
    void enviar_conTipoEmail_deberiaUsarCanalEmailYRegistrar() {
        notificadorService.enviar(
                TipoNotificacion.EMAIL,
                "comprador@openlib.com",
                "Libro disponible"
        );

        verify(emailCanal).enviar("comprador@openlib.com", "Libro disponible");
        verify(registroNotificacion).registrar(
                TipoNotificacion.EMAIL,
                "comprador@openlib.com",
                "Libro disponible"
        );

        verifyNoInteractions(smsCanal);
        verifyNoInteractions(pushCanal);
    }

    @Test
    void enviar_conTipoSms_deberiaUsarCanalSmsYRegistrar() {
        notificadorService.enviar(
                TipoNotificacion.SMS,
                "3001234567",
                "Código de confirmación"
        );

        verify(smsCanal).enviar("3001234567", "Código de confirmación");
        verify(registroNotificacion).registrar(
                TipoNotificacion.SMS,
                "3001234567",
                "Código de confirmación"
        );

        verifyNoInteractions(emailCanal);
        verifyNoInteractions(pushCanal);
    }

    @Test
    void enviar_conTipoPush_deberiaUsarCanalPushYRegistrar() {
        notificadorService.enviar(
                TipoNotificacion.PUSH,
                "usuario-app-123",
                "Tienes una nueva notificación"
        );

        verify(pushCanal).enviar("usuario-app-123", "Tienes una nueva notificación");
        verify(registroNotificacion).registrar(
                TipoNotificacion.PUSH,
                "usuario-app-123",
                "Tienes una nueva notificación"
        );

        verifyNoInteractions(emailCanal);
        verifyNoInteractions(smsCanal);
    }

    @Test
    void enviar_conTipoNoConfigurado_deberiaLanzarExcepcion() {
        Map<TipoNotificacion, CanalNotificacion> canales = new HashMap<>();
        canales.put(TipoNotificacion.EMAIL, emailCanal);

        NotificadorService service = new NotificadorService(canales, registroNotificacion);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.enviar(
                        TipoNotificacion.SMS,
                        "3001234567",
                        "Mensaje de prueba"
                )
        );

        assertEquals("Tipo de notificación no soportado: SMS", exception.getMessage());

        verifyNoInteractions(emailCanal);
        verifyNoInteractions(registroNotificacion);
    }

    @Test
    void enviar_conTipoNull_deberiaLanzarExcepcion() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> notificadorService.enviar(
                        null,
                        "usuario@openlib.com",
                        "Mensaje"
                )
        );

        assertEquals("El tipo de notificación es obligatorio", exception.getMessage());

        verifyNoInteractions(emailCanal);
        verifyNoInteractions(smsCanal);
        verifyNoInteractions(pushCanal);
        verifyNoInteractions(registroNotificacion);
    }

    @Test
    void enviar_conDestinatarioVacio_deberiaLanzarExcepcion() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> notificadorService.enviar(
                        TipoNotificacion.EMAIL,
                        "",
                        "Mensaje"
                )
        );

        assertEquals("El destinatario es obligatorio", exception.getMessage());

        verifyNoInteractions(emailCanal);
        verifyNoInteractions(registroNotificacion);
    }

    @Test
    void enviar_conMensajeVacio_deberiaLanzarExcepcion() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> notificadorService.enviar(
                        TipoNotificacion.EMAIL,
                        "usuario@openlib.com",
                        ""
                )
        );

        assertEquals("El mensaje es obligatorio", exception.getMessage());

        verifyNoInteractions(emailCanal);
        verifyNoInteractions(registroNotificacion);
    }

    @Test
    void enviar_siCanalFalla_noDeberiaRegistrarNotificacion() {
        doThrow(new RuntimeException("Error enviando email"))
                .when(emailCanal)
                .enviar("usuario@openlib.com", "Mensaje");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> notificadorService.enviar(
                        TipoNotificacion.EMAIL,
                        "usuario@openlib.com",
                        "Mensaje"
                )
        );

        assertEquals("Error enviando email", exception.getMessage());

        verify(emailCanal).enviar("usuario@openlib.com", "Mensaje");
        verifyNoInteractions(registroNotificacion);
    }
}
```

---

## Explicación del refactor

En el código original, la clase `Notificador` incumplía **SRP** porque hacía varias cosas al mismo tiempo: decidía el canal, enviaba la notificación y escribía en el log.

También incumplía **OCP**, porque si se quería agregar un nuevo canal, por ejemplo WhatsApp, había que modificar el método `enviar` y agregar otro `else if`.

Con el refactor, cada canal se convierte en una estrategia diferente mediante el patrón **Strategy**:

```java
EmailNotificacion
SmsNotificacion
PushNotificacion
```

Todas implementan la misma interfaz:

```java
CanalNotificacion
```

El servicio principal `NotificadorService` ya no sabe cómo se envía cada notificación. Solo selecciona el canal correspondiente y delega la acción.

Además, el registro en archivo se separó en otra interfaz:

```java
RegistroNotificacion
```

Así el sistema queda más extensible. Si mañana se quiere registrar en base de datos en vez de archivo, solo se crea otra implementación, por ejemplo:

```java
BaseDatosRegistroNotificacion
```

sin modificar `NotificadorService`.

---

## Conclusión

El refactor mejora el diseño porque separa responsabilidades, elimina condicionales largos y permite extender el sistema sin modificar el código principal.

La solución aplica:

* **SRP**: separación entre envío y registro.
* **OCP**: nuevos canales se agregan creando nuevas clases.
* **DIP**: el servicio depende de interfaces.
* **Strategy**: cada canal de notificación es una estrategia independiente.
* **JUnit 5 + Mockito**: las pruebas validan el comportamiento sin enviar notificaciones reales ni escribir archivos reales.
