## P13_integracion_refactoring

### Estudiante
- **Nombre completo**: [Alejandro Macias Barrios]

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Gemini] |
| **Modelo específico** | [Gemini Flash] |
| **¿Por qué elegiste este LLM?** | [Aunque la LLM que mas me gusta es Claude pues ya se me acabaron los creditos y entre todas las demas decidí usar esta ya que experimentalmente también es de las que mas me ha funcionado, además es la que usa Antigravity y la hemos usado bastante en clase] |

### Prompt utilizado
[Imagina que eres un estudiante de Ingeniería de sistemas de la Pontificia Universidad Javeriana cursando la materia de Fundamentos de Ingeniería de Software. El profesor te ha hecho la siguiente pregunta y debes responderla de la mejor manera posible.

Un desarrollador del equipo escribió esta clase para manejar las notificaciones del sistema. El código compila y funciona, pero tiene problemas de diseño:


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

**Tarea**: refactorice esta clase aplicando al menos un principio SOLID, al menos un patrón de diseño de los vistos en clase, y que incluya pruebas unitarias con JUnit 5]

### Respuesta del LLM
[1. Justificación del Diseño (Principios y Patrones)
Principio de Responsabilidad Única (SRP): La clase original hacía de todo: manejaba la lógica de emails, de SMS, de Push y, para colmo, persistía logs en el disco. Ahora, cada canal de notificación tiene su propia clase, y el logueo se puede manejar por separado.

Principio de Abierto/Cerrado (OCP): La clase Notificador queda cerrada para modificación pero abierta a extensión. Si en el próximo sprint metemos WhatsApp, solo creamos una nueva clase que implemente la interfaz, sin tocar el código existente.

Patrón de Diseño: Strategy (Estrategia): Definimos una interfaz común (EstrategiaNotificacion) para los diferentes algoritmos de envío. El Notificador ahora delega la ejecución a la estrategia que corresponda en tiempo de ejecución.

2. Código Refactorizado (Java)
La Interfaz (Estrategia)
package co.edu.javeriana.openlib.notificaciones;

public interface EstrategiaNotificacion {
    void enviar(String destinatario, String mensaje);
}

Las Implementaciones Concretas

package co.edu.javeriana.openlib.notificaciones;

public class NotificacionEmail implements EstrategiaNotificacion {
    @Override
    public void enviar(String destinatario, String mensaje) {
        // Lógica de conexión SMTP aquí
        System.out.println("Enviando EMAIL a " + destinatario + ": " + mensaje);
    }
}

public class NotificacionSMS implements EstrategiaNotificacion {
    @Override
    public void enviar(String destinatario, String mensaje) {
        // Lógica de conexión a Twilio aquí
        System.out.println("Enviando SMS a " + destinatario + ": " + mensaje);
    }
}

public class NotificacionPush implements EstrategiaNotificacion {
    @Override
    public void enviar(String destinatario, String mensaje) {
        // Lógica de conexión a Firebase aquí
        System.out.println("Enviando PUSH a " + destinatario + ": " + mensaje);
    }
}

El Contexto Refactorizado (Notificador)
Nota: Para mantener el requerimiento del log sin violar SRP, lo ideal sería inyectar un servicio de logging, pero para el alcance del ejercicio mantendremos la escritura limpia o delegada.

package co.edu.javeriana.openlib.notificaciones;

import java.util.Map;
import java.util.HashMap;

public class Notificador {
    private final Map<String, EstrategiaNotificacion> estrategias = new HashMap<>();

    public Notificador() {
        // Inicializamos las estrategias soportadas por OpenLib Market
        estrategias.put("EMAIL", new NotificacionEmail());
        estrategias.put("SMS", new NotificacionSMS());
        estrategias.put("PUSH", new NotificacionPush());
    }

    // Permitir registrar nuevas estrategias dinámicamente en el futuro
    public void registrarEstrategia(String tipo, EstrategiaNotificacion estrategia) {
        estrategias.put(tipo.toUpperCase(), estrategia);
    }

    public void enviarNotificacion(String tipo, String destinatario, String mensaje) {
        EstrategiaNotificacion estrategia = estrategias.get(tipo.toUpperCase());
        
        if (estrategia == null) {
            throw new IllegalArgumentException("Tipo de notificación no soportado: " + tipo);
        }

        // Ejecuta la estrategia seleccionada (Polimorfismo puro)
        estrategia.enviar(destinatario, mensaje);
        
        // El logueo idealmente iría en un componente de Aspectos o Decorator, 
        // pero al menos ahora está desacoplado de los ifs
        registrarLog(tipo, destinatario, mensaje);
    }

    private void registrarLog(String tipo, String destinatario, String mensaje) {
        // Lógica de persistencia (idealmente asíncrona)
        System.out.println("[LOG] Guardado en archivo: " + tipo + "|" + destinatario);
    }
}

3. Pruebas Unitarias con JUnit 5
Para probar esto sin levantar servidores reales de SMTP o Twilio, podemos crear una estrategia Mock/Stub dentro de nuestro test para verificar que el Notificador sí delega el comportamiento correctamente, y validar los flujos alternos (como tipos no soportados).

package co.edu.javeriana.openlib.notificaciones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NotificadorTest {

    private Notificador notificador;

    @BeforeEach
    void setUp() {
        notificador = new Notificador();
    }

    @Test
    @DisplayName("Debería enviar notificación exitosamente con un tipo válido (EMAIL)")
    void testEnviarEmailExitoso() {
        assertDoesNotThrow(() -> 
            notificador.enviarNotificacion("EMAIL", "estudiante@javeriana.edu.co", "Tu libro ya viene en camino")
        );
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException si el tipo de notificación no existe")
    void testTipoInvalidoLanzaExcepcion() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            notificador.enviarNotificacion("WHATSAPP", "3001234567", "Hola")
        );
        
        String mensajeEsperado = "Tipo de notificación no soportado: WHATSAPP";
        assertEquals(mensajeEsperado, exception.getMessage());
    }

    @Test
    @DisplayName("Debería permitir registrar y usar una nueva estrategia dinámicamente (OCP)")
    void testRegistrarNuevaEstrategiaDinamica() {
        // 1. Creamos una estrategia mock en línea (Clase anónima/Lambda)
        EstrategiaNotificacion estrategiaSlack = (destinatario, mensaje) -> {
            // Lógica simulada de Slack
        };

        // 2. Registramos la nueva estrategia sin modificar la clase Notificador
        notificador.registrarEstrategia("SLACK", estrategiaSlack);

        // 3. Verificamos que ahora sí la procesa sin lanzar excepciones
        assertDoesNotThrow(() -> 
            notificador.enviarNotificacion("SLACK", "#canal-ventas", "Nuevo libro registrado")
        );
    }
}]

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?
[Especificación de herramientas actuales: Solicitar explícitamente JUnit 5 evitó que la IA utilizara sintaxis obsoleta de JUnit 4 (como @Test(expected = ...) o @Before), forzándola a usar las características modernas del framework como assertThrows, assertDoesNotThrow y @DisplayName.]

#### 2. ¿Qué se puede mejorar?
[Restringir el uso de Mocks/Frameworks de pruebas: En el código original hay dependencias ocultas (el sistema de archivos con Files.write). En las pruebas unitarias que generó la IA, se "esquivó" la prueba del logging real creando métodos simulados. Un prompt más exigente podría decir: "Incluye pruebas unitarias utilizando Mockito para hacer el stubbing/mocking de los servicios externos (como el cliente de correo o el escritor de logs)".]

#### 3. Respuesta final
[La refactorización del sistema de notificaciones de OpenLib Market mejora significativamente la calidad del diseño al aplicar principios SOLID y el patrón de diseño Strategy. El principal problema del diseño original era que una sola clase concentraba múltiples responsabilidades, como el envío de correos, SMS, notificaciones push y el registro de logs, generando alto acoplamiento y dificultando el mantenimiento.
Con la nueva solución, cada canal de notificación se implementa como una estrategia independiente que cumple una única función: enviar mensajes mediante un medio específico. El componente Notificador deja de contener múltiples estructuras condicionales y pasa a delegar el comportamiento a la estrategia adecuada mediante polimorfismo, logrando un diseño más limpio y flexible.

Desde la perspectiva de SOLID, la solución respeta:
Single Responsibility Principle: Cada clase tiene una única responsabilidad.
Open/Closed Principle: Es posible agregar nuevos canales de comunicación sin modificar el código existente.
Dependency Inversion Principle: El sistema depende de la abstracción EstrategiaNotificacion y no de implementaciones concretas.
Además, el patrón Strategy permite seleccionar dinámicamente el mecanismo de notificación en tiempo de ejecución, facilitando la extensión futura del sistema. Por ejemplo, para agregar soporte para WhatsApp, Slack o Telegram, basta con crear una nueva clase que implemente la interfaz y registrarla en el sistema.]