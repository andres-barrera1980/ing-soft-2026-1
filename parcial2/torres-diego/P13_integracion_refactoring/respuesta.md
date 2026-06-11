# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [XX]: [Título resumido]

### Estudiante
- **Nombre completo**: [Diego Alejandro Torres Barrgan]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude] |
| **Modelo específico** | [Ej: Claude Sonnet 4.6/bajo]|
| **¿Por qué elegiste este LLM?** | [Es el modelo que actualmente mas uso y en el que estoy confiando, ademas que es muy poderoso para generar codigo y para explicarlo ] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actua como un desarrollador Backend Senior. En OpenLib Market tenemos esta clase Notificador que funciona pero tiene problemas de diseño:
[pegar codigo]
Refactorizala aplicando:

Al menos un principio SOLID, explica cual y por que se viola en el codigo original
Al menos un patron de diseño de los vistos en clase (Strategy, Observer, Factory, etc), justifica por que ese patron y no otro
El logging debe quedar separado de la logica de notificacion
Incluye pruebas unitarias con JUnit 5 y Mockito

La nueva version debe permitir agregar un canal nuevo (por ejemplo Slack) sin modificar codigo existente.
```

---

### Respuesta del LLM

Principios violados:

SRP: la clase hace demasiado, maneja EMAIL, SMS, PUSH y logging todo en un solo metodo
OCP: para agregar un canal nuevo hay que modificar el metodo con otro else if

Patron aplicado: Strategy
Cada canal de notificacion es una estrategia intercambiable. Se define una interfaz y cada canal la implementa por separado.
```java
java// Interfaz (Strategy)
public interface CanalNotificacion {
    void enviar(String destinatario, String mensaje);
}

// Implementaciones
public class CanalEmail implements CanalNotificacion {
    public void enviar(String destinatario, String mensaje) {
        System.out.println("Enviando EMAIL a " + destinatario + ": " + mensaje);
        // logica SMTP
    }
}

public class CanalSMS implements CanalNotificacion {
    public void enviar(String destinatario, String mensaje) {
        System.out.println("Enviando SMS a " + destinatario + ": " + mensaje);
        // logica Twilio
    }
}

public class CanalPush implements CanalNotificacion {
    public void enviar(String destinatario, String mensaje) {
        System.out.println("Enviando PUSH a " + destinatario + ": " + mensaje);
        // logica Firebase
    }
}
```
Notificador refactorizado con logging separado:
```java
javapublic interface ServicioLog {
    void registrar(String tipo, String destinatario, String mensaje);
}

public class LogArchivoImpl implements ServicioLog {
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

public class Notificador {
    private final Map<String, CanalNotificacion> canales;
    private final ServicioLog log;

    public Notificador(Map<String, CanalNotificacion> canales, ServicioLog log) {
        this.canales = canales;
        this.log = log;
    }

    public void enviar(String tipo, String destinatario, String mensaje) {
        CanalNotificacion canal = canales.get(tipo);
        if (canal == null) throw new IllegalArgumentException("Canal no soportado: " + tipo);
        canal.enviar(destinatario, mensaje);
        log.registrar(tipo, destinatario, mensaje);
    }
}
```
Agregar Slack sin tocar codigo existente:
```java
javapublic class CanalSlack implements CanalNotificacion {
    public void enviar(String destinatario, String mensaje) {
        System.out.println("Enviando SLACK a " + destinatario + ": " + mensaje);
        // logica Slack API
    }
}
```
Pruebas unitarias:
```java
java@ExtendWith(MockitoExtension.class)
class NotificadorTest {

    @Mock
    private CanalNotificacion canalEmail;

    @Mock
    private ServicioLog log;

    private Notificador notificador;

    @BeforeEach
    void setUp() {
        Map<String, CanalNotificacion> canales = new HashMap<>();
        canales.put("EMAIL", canalEmail);
        notificador = new Notificador(canales, log);
    }

    @Test
    void enviar_deberiaLlamarAlCanalCorrecto() {
        notificador.enviar("EMAIL", "user@mail.com", "Hola");
        verify(canalEmail).enviar("user@mail.com", "Hola");
    }

    @Test
    void enviar_deberiaRegistrarEnLog() {
        notificador.enviar("EMAIL", "user@mail.com", "Hola");
        verify(log).registrar("EMAIL", "user@mail.com", "Hola");
    }

    @Test
    void enviar_canalNoExiste_deberiaLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class,
            () -> notificador.enviar("TELEGRAM", "user@mail.com", "Hola"));
    }
}
```
---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

Pedir explicitamente "sin modificar codigo existente" fue lo que forzó al LLM a aplicar OCP correctamente y mostrar el ejemplo de Slack como canal nuevo. Pedir que justificara el patron y descartara otros tambien ayudo porque la respuesta explico por que Strategy y no Factory. Separar el logging como requisito explicito evito que quedara mezclado con la logica de notificacion como en el original.


#### 2. ¿Qué se puede mejorar?

El LLM no cubrio que pasa si el envio del canal falla, no hay manejo de excepciones en las implementaciones concretas. En produccion eso seria un problema porque un fallo de SMTP no deberia tumbar todo el flujo. Tampoco se pidio que mostrara como se configura el mapa de canales con Spring, entonces quedo sin explicar como se conecta todo en la aplicacion real.


#### 3. Respuesta final

La clase original viola SRP porque hace demasiado en un solo metodo y viola OCP porque agregar un canal nuevo obliga a modificar el codigo existente. La solucion con Strategy resuelve los dos problemas: cada canal es una clase independiente y agregar Slack o Telegram es solo crear una clase nueva sin tocar Notificador.
El logging quedo correctamente separado detras de la interfaz ServicioLog, lo que hace que el Notificador sea completamente testeable con mocks sin necesitar acceso al sistema de archivos.
Lo que modificaria para produccion es agregar manejo de excepciones en cada canal:
```java
javapublic void enviar(String tipo, String destinatario, String mensaje) {
    CanalNotificacion canal = canales.get(tipo);
    if (canal == null) throw new IllegalArgumentException("Canal no soportado: " + tipo);
    try {
        canal.enviar(destinatario, mensaje);
        log.registrar(tipo, destinatario, mensaje);
    } catch (Exception e) {
        log.registrar("ERROR", destinatario, "Fallo al enviar por " + tipo + ": " + e.getMessage());
    }
}
```
Asi un fallo en el canal de EMAIL no impide que el log registre el intento fallido y no tumba el resto del sistema.
