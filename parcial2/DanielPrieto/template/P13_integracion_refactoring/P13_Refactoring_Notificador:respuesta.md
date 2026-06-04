# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [13]: [Refactoring Notificador]

### Estudiante
- **Nombre completo**: [Daniel Prieto]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [**Sin IA — respuesta propia**] |
| **Modelo específico** | [N/A] |
| **¿Por qué elegiste este LLM?** | [porque decidi retarme a resolver el sin ia, ya que confio en mis conocimientos adquiridos en clase y considero que tengo dominio del tema] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
[Pega aquí el prompt exacto que enviaste al LLM. 
Incluye TODO el texto, sin editar ni resumir.

Un buen prompt incluye:
- Contexto del proyecto OpenLib Market
- El código o situación específica
- Lo que esperas que el LLM haga
- Restricciones (ej: "usa Java 21", "aplica SOLID")
- Formato de salida esperado (ej: "respuesta en markdown con código Java")]
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
[Pega aquí la respuesta COMPLETA del LLM, sin editar, sin resumir.
Incluye TODO el texto, código, explicaciones que generó el LLM.

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿Qué parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt? ¿Qué harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Principios violados: SRP (una clase hace envío + logging de 3 canales) + OCP (agregar WhatsApp = modificar la clase).

Patrón: Strategy + separación de concerns con logging transversal

public interface CanalNotificacion {
    void enviar(String destinatario, String mensaje);
    String getTipo();
}

public class CanalEmail implements CanalNotificacion {
    public void enviar(String destinatario, String mensaje) { /* SMTP */ }
    public String getTipo() { return "EMAIL"; }
}
public class CanalSMS implements CanalNotificacion {
    public void enviar(String destinatario, String mensaje) { /* Twilio */ }
    public String getTipo() { return "SMS"; }
}
public class CanalPush implements CanalNotificacion {
    public void enviar(String destinatario, String mensaje) { /* Firebase */ }
    public String getTipo() { return "PUSH"; }
}

public interface NotificacionLogger {
    void registrar(String tipo, String destinatario, String mensaje);
}
public class FileNotificacionLogger implements NotificacionLogger {
    public void registrar(String tipo, String destinatario, String mensaje) {
        try {
            Files.write(Paths.get("/var/log/openlib/notificaciones.log"),
                (tipo + "|" + destinatario + "|" + mensaje + "\n").getBytes(),
                StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) { /* manejar apropiadamente, no printStackTrace */ }
    }
}

public class Notificador {
    private final Map<String, CanalNotificacion> canales;
    private final NotificacionLogger logger;
    
    public Notificador(List<CanalNotificacion> canales, NotificacionLogger logger) {
        this.canales = canales.stream().collect(Collectors.toMap(CanalNotificacion::getTipo, c -> c));
        this.logger = logger;
    }
    
    public void enviar(String tipo, String destinatario, String mensaje) {
        CanalNotificacion canal = canales.get(tipo);
        if (canal == null) throw new IllegalArgumentException("Canal no soportado: " + tipo);
        canal.enviar(destinatario, mensaje);
        logger.registrar(tipo, destinatario, mensaje);
    }
}


Tests:

@Test void enviarEmail_llamaAlCanalCorrecto()
@Test void enviarTipoDesconocido_lanzaException()
@Test void despuesDeEnviar_registraEnLog()
@Test void agregarNuevoCanal_sinModificarNotificador() // verifica OCP


El logging quedó en su propia interfaz, inyectable y mockeable. Agregar WhatsApp = nueva clase CanalWhatsApp, cero cambios a Notificador. En producción usaría SLF4J + Logback en lugar del Files.write manual.]
