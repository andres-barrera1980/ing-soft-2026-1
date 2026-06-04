# Plantilla de entrega — Parcial 2

---

## Pregunta P13: Integración — Refactoring

### Estudiante
- **Nombre completo**: Ivan Wright

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo especifico** | Gemini 3.1 Pro |
| **¿Por que elegiste este LLM?** | Lo elegi porque es extremadamente rapido detectando bad smells en Java y generando codigo estructurado usando patrones clasicos como Strategy y buenas practicas de inyeccion de dependencias. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```text
hola gemini, actua como un Tech Lead en Java.

[CONTEXTO]
Basicamente un junior en OpenLib Market escribio una clase `Notificador` que tiene un metodo `enviar` lleno de bloques if-else para mandar EMAIL, SMS y PUSH, y ademas guarda un log escribiendo directo en el disco (`/var/log/openlib/notificaciones.log`). Aca te dejo el codigo original en mente para que lo refactorices.

[PROBLEMA]
Primeramente necesito que refactorices esta clase integralmente. Tienes que identificar que principios SOLID se estan violando, aplicar un patron de diseño para solucionarlo, e incluir pruebas unitarias con JUnit 5 y Mockito.

[RESTRICCIONES]
Seguidamente para tu solucion debes cumplir esto:
- Explicar que principio SOLID violaba el codigo original.
- Justificar el patron de diseño que elegiste.
- Separar la logica de logging de la logica de notificacion.
- Entregar pruebas unitarias para tu solucion aislando la base de datos o el disco.

[FORMATO]
Ahi entregame todo en markdown separando las explicaciones, el codigo refactorizado y las pruebas unitarias en bloques de codigo distintos.

[VALIDACION]
Sabre que tu refactoring sirvio si el dia de mañana puedo agregar un nuevo canal de notificacion (como WhatsApp) sin tener que tocar ni modificar la clase principal.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```markdown
¡Hola! Excelente iniciativa para mejorar la calidad del código de OpenLib Market. El código original del desarrollador junior es un clásico ejemplo de código altamente acoplado. Vamos a refactorizarlo profesionalmente.

### 1. Principios SOLID Violados
El código original violaba dos principios fundamentales de diseño:
*   **Single Responsibility Principle (SRP):** La clase `Notificador` hacía tres cosas totalmente distintas: decidía la lógica del canal a usar, enviaba el mensaje, y gestionaba la escritura física en un archivo de texto en el disco duro.
*   **Open/Closed Principle (OCP):** El código no estaba cerrado a la modificación. Si mañana querías agregar un nuevo canal (ej. WhatsApp), te obligaba a abrir la clase principal y añadir otro bloque `else if`, arriesgándote a romper lo que ya funcionaba.

### 2. Patrón de Diseño Seleccionado
Para erradicar los múltiples `if-else` acoplados, aplicaremos el patrón **Strategy** (Estrategia). Este patrón de comportamiento nos permite definir una familia de algoritmos (los canales de notificación), encapsular cada uno en su propia clase e inyectarlos, haciéndolos totalmente intercambiables sin alterar el contexto que los utiliza.

### 3. Código Refactorizado en Java

Primero, creamos interfaces limpias para abstraer las responsabilidades de notificar y de registrar logs.

```java
public interface CanalNotificacion {
    boolean soporta(String tipo);
    void enviar(String destinatario, String mensaje);
}

public interface LoggerService {
    void registrar(String tipo, String destinatario, String mensaje);
}
```

Luego, implementamos las estrategias específicas por cada canal:

```java
public class EmailNotificacion implements CanalNotificacion {
    @Override
    public boolean soporta(String tipo) { return "EMAIL".equals(tipo); }
    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.println("Enviando EMAIL a " + destinatario + ": " + mensaje);
    }
}

public class SmsNotificacion implements CanalNotificacion {
    @Override
    public boolean soporta(String tipo) { return "SMS".equals(tipo); }
    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.println("Enviando SMS a " + destinatario + ": " + mensaje);
    }
}
```

Implementamos el Logger separado de la lógica de negocio, encapsulando la escritura:

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileLoggerService implements LoggerService {
    @Override
    public void registrar(String tipo, String destinatario, String mensaje) {
        try {
            Files.write(Paths.get("/var/log/openlib/notificaciones.log"),
                (tipo + "|" + destinatario + "|" + mensaje + "\n").getBytes(),
                StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

Finalmente, nuestro `Notificador` queda totalmente limpio y cerrado a modificaciones:

```java
import java.util.List;

public class Notificador {
    private final List<CanalNotificacion> canales;
    private final LoggerService logger;

    public Notificador(List<CanalNotificacion> canales, LoggerService logger) {
        this.canales = canales;
        this.logger = logger;
    }

    public void enviar(String tipo, String destinatario, String mensaje) {
        CanalNotificacion canalSeleccionado = canales.stream()
            .filter(c -> c.soporta(tipo))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Canal no soportado: " + tipo));

        canalSeleccionado.enviar(destinatario, mensaje);
        logger.registrar(tipo, destinatario, mensaje);
    }
}
```

### 4. Pruebas Unitarias (JUnit 5 y Mockito)

```java
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class NotificadorTest {

    @Test
    void enviar_CanalSoportado_DebeLlamarAlCanalYAlLogger() {
        // Arrange
        CanalNotificacion emailMock = mock(CanalNotificacion.class);
        when(emailMock.soporta("EMAIL")).thenReturn(true);
        LoggerService loggerMock = mock(LoggerService.class);
        
        Notificador notificador = new Notificador(Arrays.asList(emailMock), loggerMock);

        // Act
        notificador.enviar("EMAIL", "juan@test.com", "Hola");

        // Assert
        verify(emailMock).enviar("juan@test.com", "Hola");
        verify(loggerMock).registrar("EMAIL", "juan@test.com", "Hola");
    }

    @Test
    void enviar_CanalNoSoportado_DebeLanzarExcepcion() {
        // Arrange
        Notificador notificador = new Notificador(Arrays.asList(), mock(LoggerService.class));
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            notificador.enviar("WHATSAPP", "juan@test.com", "Hola");
        });
    }
}
```
Con este diseño moderno, si quieres agregar WhatsApp, solo creas la clase `WhatsappNotificacion implements CanalNotificacion` y la inyectas a la lista. No tocas `Notificador` en absoluto. Misión cumplida.
```

---

### Analisis critico de la respuesta

#### 1. ¿Que hizo bien el prompt?

[Evalua tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudo a obtener una buena respuesta? ¿Que parte de tu prompt fue mas efectiva? Se especifico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Que se puede mejorar?

[¿Que le falto a tu prompt? ¿Que harias diferente si pudieras reformularlo? ¿El LLM entendio mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Que no cubrio el LLM que tu si sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo mas alla. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tu dominas el tema.]
