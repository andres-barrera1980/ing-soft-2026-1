
## Pregunta [13]: [Refactorizar aplicando SOLID + patrón de diseño + pruebas]

### Estudiante
- **Nombre completo**: Julian Felipe Rojas Almanza

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Gpt |
| **Modelo específico** | Gpt-4o |
| **¿Por qué elegiste este LLM?** | Es uno de los modelos con mayor capacidad de cohesión para resolver ejercicios cruzados que involucran código, patrones, principios de diseño y testing en un solo bloque de instrucciones. Aseguran la separación de la lógica de infraestructura (el archivo de log rígido) del dominio de notificaciones sin dejar cabos sueltos. |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un Desarrollador de Software Principal y experto en Refactoring y Clean Code. Estoy resolviendo un ejercicio técnico académico para el sistema "OpenLib Market" y necesito que refactorices integralmente una clase con serios problemas de diseño.

[CONTEXTO]
Un desarrollador del equipo escribió la siguiente clase `Notificador` para manejar las alertas del sistema. Aunque el código compila y cumple su función básica en un entorno controlado, introduce un acoplamiento rígido y rompe con las buenas prácticas de diseño orientado a objetos:

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

[PROBLEMA / TAREA]
Necesito que realices una refactorización profunda y completa de este componente. Tu respuesta debe cubrir de manera obligatoria y explícita los siguientes tres puntos requeridos por mi enunciado:
1. Identifica qué principios SOLID se están violando en la estructura original y justifica detalladamente por qué (deben aplicar al menos dos principios).
2. Refactoriza el código aplicando el o los patrones de diseño que consideres más adecuados para resolver la estructura condicional y aislar de forma limpia el sistema de logging en un componente independiente. Demuestra cómo el nuevo diseño permite agregar un nuevo canal de notificación (como Slack o WhatsApp) sin modificar las clases de producción existentes (Open/Closed Principle).
3. Genera una suite de pruebas unitarias robusta utilizando JUnit 5 y Mockito que valide de forma aislada el comportamiento de los canales y asegure la correcta delegación de responsabilidades.

[RESTRICCIONES]
- Chain of Thought: Antes de escribir el código final, detalla tu plan de acción explicando qué interfaces vas a introducir y cómo desacoplarás la escritura física en el archivo de log para que pueda ser mockeada en los tests.
- Queda prohibida la persistencia de bloques if/else o switch basados en strings para ejecutar el envío dentro del servicio principal.
- Utiliza inyección de dependencias por constructor compatible con Java 21 moderno.

[FORMATO DE SALIDA]
Estructura tu respuesta en Markdown utilizando única y estrictamente los siguientes cuatro encabezados para organizar la información:
- ### 1. Diagnóstico de Principios SOLID Violados
- ### 2. Patrón de Diseño Seleccionado y Estrategia de Desacoplamiento
- ### 3. Código Java Refactorizado (Estructura Modular Completa)
- ### 4. Suite de Pruebas Unitarias (JUnit 5 + Mockito)

[VALIDACIÓN]
Sabré que tu respuesta es excelente si el código final separa por completo el mecanismo de logs de los canales de envío, si la suite de tests unitarios no requiere escribir en el sistema de archivos real (`/var/log/...`), y si demuestras con código lo sencillo que es extender el sistema para soportar un canal de WhatsApp aplicando polimorfismo.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
### 1. Diagnóstico de Principios SOLID Violados

Al analizar el código original de la clase `Notificador` para OpenLib Market, se evidencia que, aunque es funcional a corto plazo, arrastra serios problemas de mantenibilidad, extensibilidad y acoplamiento. Viola de forma directa los siguientes principios fundamentales de SOLID:

* **Principio de Responsabilidad Única (SRP - Single Responsibility Principle):**
    Una clase debe tener una única razón para cambiar. La clase `Notificador` actual asume múltiples responsabilidades de negocio y de infraestructura al mismo tiempo: conoce los detalles de conexión y envío de correos (SMTP), la lógica de mensajería móvil (Twilio), las notificaciones móviles (Firebase) y, por si fuera poco, gestiona la persistencia física en el sistema de archivos (`Files.write`). Si el formato del log cambia, la clase cambia; si las credenciales de Firebase cambian, la clase cambia; si se cae el soporte de SMS, la clase cambia.
* **Principio de Abierto/Cerrado (OCP - Open/Closed Principle):**
    Las entidades de software deben estar abiertas para la extensión, pero cerradas para la modificación. Cada vez que OpenLib Market requiera integrar un nuevo canal de comunicación (por ejemplo, WhatsApp, Telegram o Slack), nos vemos obligados a abrir la clase `Notificador`, añadir un nuevo bloque `else if`, alterar los parámetros y volver a probar toda la estructura. Esto incrementa exponencialmente el riesgo de introducir efectos secundarios en los canales que ya funcionaban correctamente.
* **Principio de Inversión de Dependencias (DIP - Dependency Inversion Principle):**
    Los módulos de alto nivel no deben depender de módulos de bajo nivel; ambos deben depender de abstracciones. `Notificador` depende directamente de implementaciones concretas de bajo nivel y acoplamientos estáticos como `Files.write` y `Paths.get`. Esto genera un bloqueo absoluto al intentar realizar pruebas unitarias, ya que el componente no puede ser aislado de la infraestructura real del servidor (el directorio `/var/log/openlib/`), haciendo que los tests dependan del sistema de archivos local.

---

### 2. Patrón de Diseño Seleccionado y Estrategia de Desacoplamiento

Para solucionar estas deficiencias, implementaremos una arquitectura limpia basada en los siguientes componentes estratégicos:

1.  **Patrón Strategy (Estrategia):** Definiremos una interfaz común llamada `CanalNotificacion`. Cada medio de envío (`EmailCanal`, `SmsCanal`, `PushCanal`) implementará esta interfaz de forma aislada. El servicio principal delegará polimórficamente la ejecución, eliminando por completo los bloques condicionales `if/else`.
2.  **Inyección de Dependencias por Registro / Mapa:** Para evitar delegar la selección de la estrategia a un `switch-case`, utilizaremos un mapa de estrategias inyectado a través del constructor aprovechando las bondades de Java 21. El servicio principal resolverá el canal adecuado en tiempo de ejecución de forma $O(1)$.
3.  **Desacoplamiento del Sistema de Log (Inversión de Dependencias):** Crearemos una interfaz `ServicioLog`. La escritura en disco se aislará en una implementación concreta (`ArchivoServicioLog`). De este modo, el componente de notificaciones solo interactuará con la abstracción del log. Durante las pruebas unitarias, podremos mockear esta interfaz con Mockito de manera transparente, eliminando cualquier interacción real con el disco duro.

---

### 3. Código Java Refactorizado (Estructura Modular Completa)

A continuación se presenta el diseño modular desacoplado en Java 21, libre de condicionales rígidos y preparado para la extensión.

```java
package com.openlib.notificacion;

import java.util.Map;
import java.util.Optional;

// --- 1. ABSTRACCIONES CORE ---

public interface CanalNotificacion {
    void enviar(String destinatario, String mensaje);
    String getTipo();
}

public interface ServicioLog {
    void registrar(String tipo, String destinatario, String mensaje);
}

// --- 2. IMPLEMENTACIÓN DE INFRAESTRUCTURA DE LOGS ---

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ArchivoServicioLog implements ServicioLog {
    private static final String RUTA_LOG = "/var/log/openlib/notificaciones.log";

    @Override
    public void registrar(String tipo, String destinatario, String mensaje) {
        try {
            String linea = String.format("%s|%s|%s\n", tipo, destinatario, mensaje);
            Files.write(Paths.get(RUTA_LOG), linea.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            // En producción se usaría un Logger formal (SLF4J / Logback)
            System.err.println("Error al escribir en el log de notificaciones: " + e.getMessage());
        }
    }
}

// --- 3. IMPLEMENTACIONES CONCRETAS DE CANALES (STRATEGIES) ---

public class EmailCanal implements CanalNotificacion {
    @Override
    public void enviar(String destinatario, String mensaje) {
        // Lógica de conexión SMTP simulada
        System.out.println("Enviando EMAIL a " + destinatario + ": " + mensaje);
    }

    @Override
    public String getTipo() {
        return "EMAIL";
    }
}

public class SmsCanal implements CanalNotificacion {
    @Override
    public void enviar(String destinatario, String mensaje) {
        // Lógica de conexión a API de Twilio simulada
        System.out.println("Enviando SMS a " + destinatario + ": " + mensaje);
    }

    @Override
    public String getTipo() {
        return "SMS";
    }
}

public class PushCanal implements CanalNotificacion {
    @Override
    public void enviar(String destinatario, String mensaje) {
        // Lógica de conexión a Firebase simulada
        System.out.println("Enviando PUSH a " + destinatario + ": " + mensaje);
    }

    @Override
    public String getTipo() {
        return "PUSH";
    }
}

// --- DEMOSTRACIÓN DE EXTENSIBILIDAD (OCP) ---
// Agregar WhatsApp no requiere modificar ninguna clase existente
public class WhatsAppCanal implements CanalNotificacion {
    @Override
    public void enviar(String destinatario, String mensaje) {
        // Lógica de conexión a la API de WhatsApp Business
        System.out.println("Enviando WHATSAPP a " + destinatario + ": " + mensaje);
    }

    @Override
    public String getTipo() {
        return "WHATSAPP";
    }
}

// --- 4. SERVICIO PRINCIPAL REFACTORIZADO ---

public class NotificadorService {
    private final Map<String, CanalNotificacion> canales;
    private final ServicioLog servicioLog;

    // Inyección de dependencias por constructor compatible con Java 21
    public NotificadorService(Map<String, CanalNotificacion> canales, ServicioLog servicioLog) {
        this.canales = Map.copyOf(canales);
        this.servicioLog = servicioLog;
    }

    public void enviarNotificacion(String tipo, String destinatario, String mensaje) {
        CanalNotificacion canal = Optional.ofNullable(canales.get(tipo.toUpperCase()))
                .orElseThrow(() -> new IllegalArgumentException("Canal de notificación no soportado: " + tipo));

        // Delegación polimórfica de responsabilidades
        canal.enviar(destinatario, mensaje);
        servicioLog.registrar(tipo.toUpperCase(), destinatario, mensaje);
    }
}
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

El éxito del prompt radica en que se respetó la segmentación descrita en el archivo guia_prompting.md para hacer buenos prompts. Una Gema de Gemini ayudó a optimizarlo en primera instancia, y luego yo me encargué de su corrección final. La fórmula es directa: un rol establecido, un contexto claro e instrucciones sencillas que garantizan el resultado esperado.


#### 2. ¿Qué se puede mejorar?

prompt es completamente funcional y está listo. De manera opcional, se podrían añadir ejemplos de salidas esperadas si la tarea se vuelve más compleja, pero para el requerimiento actual la estructura es impecable y no amerita ninguna corrección.


#### 3. Respuesta final

el análisis de la inteligencia artificial identificó correctamente los principios solid violados al detallar cómo la mezcla de lógicas de infraestructura rompía la responsabilidad única, obligaba a modificar el código ante nuevos canales violando el principio abierto cerrado y amarraba el sistema al disco impidiendo cumplir la inversión de dependencias. además, el patrón de diseño de estrategia aplicado fue el más adecuado junto con el mapa de componentes para borrar las condiciones, logrando que el sistema de registro de eventos quedara correctamente separado de la lógica de notificación mediante una abstracción limpia. la nueva versión permite de forma exitosa agregar un canal como whatsapp sin alterar el código existente, ganando una testabilidad unitaria espectacular comparada con la versión rígida original.

sin embargo, el análisis omitió un vacío gigante sobre las consecuencias de este diseño al llevarlo a un entorno de producción real para openlib market. si tuviera que implementar esta solución, no usaría exactamente la propuesta del robot sino que la modificaría radicalmente, pues la inteligencia artificial cometió el error de pasar por alto que las llamadas a los canales externos de correo o mensajes se están ejecutando de forma síncrona y bloqueante dentro del mismo hilo del cliente, lo que significa que si el servidor de twilio o firebase tarda en responder, toda la aplicación web de openlib market se va a colgar. para que la solución fuera perfecta, faltó mencionar que en la vida real la opción elegida debe procesar los envíos en hilos separados de manera asíncrona usando la anotación correspondiente de spring, y se debería cambiar el mapa manual del constructor por la inyección automática del motor para que busque los componentes por su nombre técnico de frijol en el sistema. tampoco se especificó que si la escritura en el archivo falla, se perdería el aviso por completo al no contar con una cola de mensajería persistente que tolere fallos de red.

el código original estaba mal diseñado porque la clase se cargaba con muchas tareas al mismo tiempo al decidir las rutas y procesar los detalles del correo, los mensajes móviles y el disco duro, violando los principios de diseño limpio. además, romper las reglas obligaba a dañar la lógica central con cada requerimiento técnico en openlib market. para solucionarlo bien, se aplicó el patrón de diseño de estrategia junto con el enfoque de puertos y adaptadores, definiendo la interfaz canalnotificacion como el puente o contrato limpio del negocio. las clases independientes actúan como los conectores externos que implementan cada medio de envío por separado. el servicio principal final queda limpio y protegido, recibiendo las estrategias desde afuera y ordenando su ejecución, pues para evitar bloqueos y amarrar el código en producción, la opción elegida debe procesar las alertas mediante eventos asíncronos apoyados en el contenedor de spring. de esta manera, el sistema no solo borra las condiciones lógicas, sino que se puede probar cada canal por separado en experimentos de mentiras con mockito, quedando fácil de mantener, seguro y listo para recibir cualquier otra plataforma en el futuro.
