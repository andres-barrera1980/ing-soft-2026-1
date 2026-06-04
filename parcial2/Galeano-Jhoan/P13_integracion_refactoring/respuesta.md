# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [13]: [P13_integracion_refactoring]

### Estudiante
- **Nombre completo**: [Jhoan Galeano]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude  |
| **Modelo específico** |  Claude pro 3.1|
| **¿Por qué elegiste este LLM?** | Claude ayuda a obtener mejores respueestas] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
Actúa como un ingeniero de software senior y experto en arquitectura de software.

Tu tarea es refactorizar integralmente la siguiente clase en Java ('Notificador') para solucionar sus problemas de diseño, aplicando principios de arquitectura limpia y buenas prácticas.

Aquí está el código original que debes mejorar:

```java
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
```

---

### Respuesta del LLM

Value Object – Notificacion.java
javapublic record Notificacion(
    String destinatario,
    String mensaje
) {
    public Notificacion {
        Objects.requireNonNull(destinatario, "destinatario no puede ser nulo");
        Objects.requireNonNull(mensaje, "mensaje no puede ser nulo");
    }
}

2. Interfaz del canal – CanalNotificacion.java
java// Abstracción: Notificador depende de esto, no de implementaciones concretas
public interface CanalNotificacion {
    void enviar(Notificacion notificacion);
    String tipo(); // "EMAIL", "SMS", "PUSH"
}

3. Interfaz del registro – RegistroNotificacion.java
javapublic interface RegistroNotificacion {
    void registrar(String tipo, Notificacion notificacion);
}

4. Implementaciones de canales
java// canales/CanalEmail.java
public class CanalEmail implements CanalNotificacion {

    @Override
    public void enviar(Notificacion notificacion) {
        // Aquí iría la conexión real a SMTP
        System.out.println("Enviando EMAIL a "
            + notificacion.destinatario() + ": " + notificacion.mensaje());
    }

    @Override
    public String tipo() { return "EMAIL"; }
}
java// canales/CanalSms.java
public class CanalSms implements CanalNotificacion {

    @Override
    public void enviar(Notificacion notificacion) {
        // Aquí iría la conexión real a Twilio
        System.out.println("Enviando SMS a "
            + notificacion.destinatario() + ": " + notificacion.mensaje());
    }

    @Override
    public String tipo() { return "SMS"; }
}
java// canales/CanalPush.java
public class CanalPush implements CanalNotificacion {

    @Override
    public void enviar(Notificacion notificacion) {
        // Aquí iría la conexión real a Firebase
        System.out.println("Enviando PUSH a "
            + notificacion.destinatario() + ": " + notificacion.mensaje());
    }

    @Override
    public String tipo() { return "PUSH"; }
}

5. Implementación del registro – RegistroEnArchivo.java
javapublic class RegistroEnArchivo implements RegistroNotificacion {

    private final Path rutaLog;

    public RegistroEnArchivo(Path rutaLog) {
        this.rutaLog = Objects.requireNonNull(rutaLog);
    }

    @Override
    public void registrar(String tipo, Notificacion notificacion) {
        String entrada = tipo + "|"
            + notificacion.destinatario() + "|"
            + notificacion.mensaje() + "\n";
        try {
            Files.write(rutaLog, entrada.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            // Propagar como excepción de dominio, no silenciar con printStackTrace
            throw new RuntimeException("Error al registrar notificación en log", e);
        }
    }
}

6. Clase principal refactorizada – Notificador.java
javapublic class Notificador {

    private final Map<String, CanalNotificacion> canales;
    private final RegistroNotificacion registro;

    // Inyección de dependencias → testeable, extensible, desacoplado
    public Notificador(List<CanalNotificacion> canales, RegistroNotificacion registro) {
        this.canales = canales.stream()
            .collect(Collectors.toMap(CanalNotificacion::tipo, c -> c));
        this.registro = Objects.requireNonNull(registro);
    }

    public void enviar(String tipo, String destinatario, String mensaje) {
        CanalNotificacion canal = canales.get(tipo);
        if (canal == null) {
            throw new IllegalArgumentException("Canal no soportado: " + tipo);
        }

        Notificacion notificacion = new Notificacion(destinatario, mensaje);
        canal.enviar(notificacion);
        registro.registrar(tipo, notificacion);
    }
}

7. Punto de ensamblaje (composición)
java// En tu configuración, factory, o main:
Notificador notificador = new Notificador(
    List.of(new CanalEmail(), new CanalSms(), new CanalPush()),
    new RegistroEnArchivo(Paths.get("/var/log/openlib/notificaciones.log"))
);

notificador.enviar("EMAIL", "user@mail.com", "Tu pedido fue confirmado");

#### 1. ¿Qué hizo bien el prompt?

[El prompt ayudao mucho para verificar que se tomaran las notificaciones que se pudieran]


#### 2. ¿Qué se puede mejorar?

[Un prompt mas especifico con una mejor solucion para el problema general.]


#### 3. Respuesta final
[¿El LLM identificó correctamente los principios SOLID violados? Sí. El código original violaba el Principio de Responsabilidad Única (SRP) porque la clase Notificador se encargaba de la lógica de envío de tres tecnologías distintas (SMTP, Twilio, Firebase)
 ¿Qué patrón aplicó y fue el más adecuado? Aplicó el Patrón Strategy (Estrategia) combinado con un mapa de canales inyectado en el constructor
  ¿La solución con pruebas es completa y testeable? La solución es altamente testeable gracias a la Inyección de Dependencias. Al pasar la lista de canales y el registro por el constructor del Notificador
   ¿El logging quedó correctamente separado de la lógica de notificación? Sí. Se creó la interfaz RegistroNotificacion y su implementación RegistroEnArchivo.]


  