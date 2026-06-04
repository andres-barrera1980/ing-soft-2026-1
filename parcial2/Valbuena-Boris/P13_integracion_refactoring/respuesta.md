# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [13]: [integracion_refactoring]

### Estudiante
- **Nombre completo**: Boris Nicolas Valbuena Gueirsman

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gemini |
| **Modelo específico** | Gemini 3.5 Flash |
| **¿Por qué elegiste este LLM?** | Es la que más usé durante el curso, por lo cual es la misma que usaré en el parcial. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un Arquitecto de Software Senior especializado en Java 21, Spring Boot, SOLID y patrones de diseño.

Analiza la siguiente clase:

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

Refactoriza completamente la solución aplicando:

* Al menos un principio SOLID.
* Al menos un patrón de diseño visto en Ingeniería de Software.
* Separación adecuada de responsabilidades.
* Código fácilmente testeable.
* Java 21.
* JUnit 5.

Incluye:

Problemas detectados

Principios SOLID aplicados

Patrón utilizado

Nueva arquitectura

Código refactorizado

Pruebas unitarias JUnit 5

Beneficios obtenidos
```

---

### Respuesta del LLM

> ¡Saludos! Como Arquitecto de Software Senior, te presento un análisis técnico detallado de la clase `Notificador`, seguido de una propuesta de refactorización de nivel empresarial utilizando las capacidades modernas de **Java 21**, la filosofía de diseño **SOLID** y las mejores prácticas de la industria.

---

## 🛡️ Problemas Detectados (Anti-patrones y "Code Smells")

La clase actual presenta serios problemas de mantenibilidad, escalabilidad y testabilidad:

1. **Violación del Principio de Responsabilidad Única (SRP):** La clase `Notificador` tiene más de una razón para cambiar. Se encarga de la lógica de negocio para enviar correos, enviar SMS, enviar notificaciones Push, y además maneja la persistencia/escritura de logs en el sistema de archivos.
2. **Violación del Principio de Abierto/Cerrado (OCP):** Si el día de mañana el negocio exige un nuevo canal (por ejemplo, *WhatsApp* o *Discord*), nos veríamos obligados a modificar el código fuente de esta clase, agregando más bloques `else-if`. Esto introduce un alto riesgo de romper la lógica existente.
3. **Acoplamiento Rígido con I/O Globales (In-testable):** La clase escribe directamente en un path absoluto (`/var/log/openlib/...`). Esto hace que las pruebas unitarias dependan del sistema de archivos local, requieran permisos específicos y ensucien el entorno de ejecución. No se puede mockear de manera limpia.
4. **Manejo Deficiente de Excepciones:** Capturar una excepción con `e.printStackTrace()` destruye la trazabilidad en entornos productivos modernos, donde se requieren loggers estructurados (SLF4J/Logback).
5. **Tipado Débil (Stringly-Typed):** El uso de cadenas de texto (`String tipo`) para definir la estrategia de envío es propenso a errores tipográficos (`"Email"` vs `"EMAIL"`).

---

## 🛠️ Principios SOLID Aplicados

* **Single Responsibility Principle (SRP):** Separamos la lógica de cada canal de notificación en clases independientes. El servicio de logs también se aísla de la infraestructura de envío.
* **Open/Closed Principle (OCP):** La arquitectura queda abierta a la extensión (podemos añadir nuevos canales creando nuevas clases) pero cerrada a la modificación (no tocamos el core del sistema).
* **Dependency Inversion Principle (DIP):** El motor de notificaciones ahora depende de una abstracción (`NotificacionStrategy`) y no de implementaciones concretas.

---

## 🎨 Patrón de Diseño Utilizado: Estrategia (Strategy)

Para eliminar los bloques condicionales (`if-else`), implementamos el patrón **Strategy** combinado con el patrón **Factory** (aprovechando las ventajas de concurrencia e inmutabilidad de los mapas de Java).

Además, separamos la persistencia usando un enfoque de **Observer** implícito o simplemente delegando la responsabilidad a un servicio de logging dedicado (`NotificacionLogService`).

---

## 🗺️ Nueva Arquitectura

La solución se divide en componentes altamente cohesivos:

* `NotificacionStrategy`: Interfaz funcional que define el contrato para cada canal.
* Implementaciones (`EmailStrategy`, `SmsStrategy`, `PushStrategy`): Encapsulan la lógica específica de cada canal.
* `NotificacionLogService`: Componente de infraestructura encargado del registro histórico de envíos.
* `NotificadorService`: El orquestador que despacha la notificación al canal correspondiente usando la estrategia adecuada.

---

## 💻 Código Refactorizado (Java 21)

Aprovechamos las características modernas de **Java 21**, como el Pattern Matching (si fuera necesario), `List.of` / `Map.of` inmutables, programación funcional y un manejo limpio de streams.

### 1. El Dominio (Tipo de Notificación)

Evitamos errores de dedo usando un `enum`.

```java
package com.openlib.notificador.domain;

public enum TipoNotificacion {
    EMAIL, SMS, PUSH
}

```

### 2. El Contrato (Estrategia)

```java
package com.openlib.notificador.strategy;

import com.openlib.notificador.domain.TipoNotificacion;

public interface NotificacionStrategy {
    void enviar(String destinatario, String mensaje);
    TipoNotificacion getTipo();
}

```

### 3. Implementaciones Concretas de Estrategias

```java
package com.openlib.notificador.strategy.impl;

import com.openlib.notificador.domain.TipoNotificacion;
import com.openlib.notificador.strategy.NotificacionStrategy;

public class EmailStrategy implements NotificacionStrategy {
    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.printf("Enviando EMAIL a %s: %s%n", destinatario, mensaje);
    }

    @Override
    public TipoNotificacion getTipo() {
        return TipoNotificacion.EMAIL;
    }
}

```

*(Nota: `SmsStrategy` y `PushStrategy` se implementan exactamente igual, variando únicamente el método `getTipo()` con `TipoNotificacion.SMS` y `TipoNotificacion.PUSH` respectivamente).*

### 4. Servicio de Registro (Abstracción del Log en Archivo)

Separamos la escritura del log para poder mockearla en los tests.

```java
package com.openlib.notificador.service;

import com.openlib.notificador.domain.TipoNotificacion;
import java.io.IOException;
import java.nio.file.*;

public class NotificacionLogService {
    private final Path logPath;

    // Inyección del path para alta testabilidad
    public NotificacionLogService(Path logPath) {
        this.logPath = logPath;
    }

    public void registrarLog(TipoNotificacion tipo, String destinatario, String mensaje) {
        String logLine = String.format("%s|%s|%s%n", tipo, destinatario, mensaje);
        try {
            // Asegurar que el directorio padre existe antes de escribir
            if (logPath.getParent() != null) {
                Files.createDirectories(logPath.getParent());
            }
            Files.write(logPath, logLine.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            // En producción usaríamos un Logger estructurado (SLF4J)
            System.err.println("Error crítico al escribir log de notificación: " + e.getMessage());
        }
    }
}

```

### 5. El Orquestador Principal (`NotificadorService`)

```java
package com.openlib.notificador.service;

import com.openlib.notificador.domain.TipoNotificacion;
import com.openlib.notificador.strategy.NotificacionStrategy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NotificadorService {
    
    private final Map<TipoNotificacion, NotificacionStrategy> estrategias;
    private final NotificacionLogService logService;

    // En Spring Boot, esta lista se inyecta automáticamente gracias al polimorfismo
    public NotificadorService(List<NotificacionStrategy> estrategiaList, NotificacionLogService logService) {
        this.logService = logService;
        this.estrategias = estrategiaList.stream()
                .collect(Collectors.toUnmodifiableMap(NotificacionStrategy::getTipo, Function.identity()));
    }

    public void procesarNotificacion(TipoNotificacion tipo, String destinatario, String mensaje) {
        NotificacionStrategy estrategia = Optional.ofNullable(estrategias.get(tipo))
                .orElseThrow(() -> new IllegalArgumentException("Canal de notificación no soportado: " + tipo));

        // 1. Ejecutar estrategia de envío
        estrategia.enviar(destinatario, mensaje);

        // 2. Delegar registro de auditoría
        logService.registrarLog(tipo, destinatario, mensaje);
    }
}

```

---

## 🧪 Pruebas Unitarias (JUnit 5 + Mockito)

Gracias a la inyección de dependencias y desacoplamiento, testear este código es sumamente limpio. Usamos la librería integrada de JUnit 5 para directorios temporales (`@TempDir`), evitando escribir logs reales en `/var/log`.

```java
package com.openlib.notificador.service;

import com.openlib.notificador.domain.TipoNotificacion;
import com.openlib.notificador.strategy.NotificacionStrategy;
import com.openlib.notificador.strategy.impl.EmailStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificadorServiceTest {

    private NotificadorService notificadorService;
    private NotificacionLogService logService;
    private Path tempLogFile;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        // Creamos un path temporal seguro para cada ejecución de prueba
        tempLogFile = tempDir.resolve("notificaciones.log");
        logService = new NotificacionLogService(tempLogFile);
        
        // Instanciamos la estrategia de Email para la prueba
        NotificacionStrategy emailStrategy = new EmailStrategy();
        
        notificadorService = new NotificadorService(List.of(emailStrategy), logService);
    }

    @Test
    @DisplayName("Debería enviar notificación por Email de manera exitosa y registrar en el log")
    void deberiaEnviarEmailYRegistrarLog() throws IOException {
        // Arrange
        TipoNotificacion tipo = TipoNotificacion.EMAIL;
        String destinatario = "dev@javeriana.edu.co";
        String mensaje = "Tu refactorización está lista.";

        // Act
        assertDoesNotThrow(() -> notificadorService.procesarNotificacion(tipo, destinatario, mensaje));

        // Assert
        assertTrue(Files.exists(tempLogFile), "El archivo de log debió crearse.");
        List<String> lineasLog = Files.readAllLines(tempLogFile);
        assertEquals(1, lineasLog.size());
        assertEquals("EMAIL|dev@javeriana.edu.co|Tu refactorización está lista.", lineasLog.getFirst()); 
        // Nota: .getFirst() es una feature añadida en las colecciones secuenciales de Java 21
    }

    @Test
    @DisplayName("Debería lanzar una excepción si el tipo de notificación no tiene estrategia mapeada")
    void deberiaLanzarExcepcionCuandoNoExisteEstrategia() {
        // Act & Assert
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> 
                notificadorService.procesarNotificacion(TipoNotificacion.SMS, "3001234567", "Hola")
        );

        assertTrue(excepcion.getMessage().contains("Canal de notificación no soportado"));
    }
}

```

---

## 🚀 Beneficios Obtenidos

1. **Extensibilidad Radical:** Si necesitas agregar *WhatsApp*, solo creas `WhatsappStrategy` implementando `NotificacionStrategy`. `NotificadorService` la absorberá automáticamente (si usas Spring Boot) sin cambiar una sola línea de código existente.
2. **Alta Testabilidad:** Al usar `@TempDir` de JUnit 5 e inyectar el servicio de logs, las pruebas corren en memoria y de forma aislada, garantizando que el pipeline de CI/CD funcione en cualquier servidor (Linux, Windows, Docker) sin problemas de permisos de escritura.
3. **Mapeo Inmutable y Concurrente:** El uso de `Collectors.toUnmodifiableMap` en el constructor de Java 21 asegura que el diccionario de estrategias sea de *solo lectura* y seguro para ambientes multihilo de alto rendimiento.
4. **Clean Code robusto:** Reemplazamos la fragilidad de las cadenas de texto (`String tipo`) con la seguridad tipada de los enums de Java.
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El prompt acerto al estructurar el rol como un Arquitecto de Software Senior enfocado en Java 21 y Spring Boot, lo que elevo el nivel tecnico y de herramientas de la respuesta. La inclusion del codigo original con la logica condicional y el acoplamiento al sistema de archivos le dio una base solida al modelo para evaluar problemas clasicos de diseño. Ademas, fijar requisitos explicitos como el uso de Java 21, JUnit 5, la aplicacion de SOLID y la separacion de responsabilidades permitio que la estructura de la respuesta fuera directa y cubriera cada rubro de forma ordenada.

#### 2. ¿Qué se puede mejorar?
Aunque el analisis identifica bien las fallas de SRP, OCP y DIP, y el patron Strategy es la eleccion ideal, la solucion flaquea para un entorno de produccion real. El modelo movio la escritura del archivo a la clase NotificacionLogService, pero la sigue instanciando con dependencias del sistema de archivos (Path), manteniendo el acoplamiento rigido con la infraestructura local en lugar de abstraer la persistencia con una interfaz limpia. Ademas, en el codigo del test se cometio el error de instanciar las clases manualmente con new en vez de aprovechar el motor de inyeccion de dependencias o usar mocks nativos si estabamos bajo el contexto de Spring Boot. Tambien se mezclan niveles de abstraccion, metiendo un System.err.println dentro del manejo de errores de infraestructura, algo inviable en un entorno real de produccion donde la telemetria y los loggers estructurados como Logback son obligatorios.

#### 3. Respuesta final

Para solucionar los problemas de diseño de la clase original, aplicamos el principio de inversion de dependencias mediante el patron Strategy, eliminando los bloques condicionales y el acoplamiento rigido con el sistema de archivos. Primero, estructuramos el diseño con un enum TipoNotificacion para el tipado fuerte y definimos la interfaz NotificacionStrategy con metodos para enviar y obtener el tipo. Cada canal (EmailStrategy, SmsStrategy, etc.) implementa este contrato de forma aislada, permitiendo que el sistema crezca sin modificar el codigo central. Para la auditoria, creamos la interfaz AuditoriaNotificacionRepository, abstrayendo por completo el almacenamiento para que no dependa del disco duro local y permitiendo que en el futuro se guarde en una base de datos o servicio en la nube sin tocar la logica de negocio.

El orquestador NotificadorService recibe la lista de estrategias y el repositorio de auditoria por constructor; al usar Spring Boot, el framework inyecta estos componentes de forma automatica y creamos un mapa inmutable seguro para entornos multihilo mediante Collectors.toUnmodifiableMap de Java 21. El metodo principal simplemente busca la estrategia correspondiente, ejecuta el envio y delega el registro de auditoria, lanzando una excepcion controlada si el canal no existe. Gracias a este desacoplamiento, la clase de pruebas con JUnit 5 corre de forma rapida y aislada en memoria sin requerir permisos de carpetas del sistema operativo, utilizando mocks de Mockito (mock(NotificacionStrategy.class)) para verificar que las interacciones y los metodos delegados se ejecuten exactamente las veces esperadas.
