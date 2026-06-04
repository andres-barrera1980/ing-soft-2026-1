# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [P07_clean_architecture]

### Estudiante
- **Nombre completo**: [Alejandro Andres Perez Diaz]

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | [Claude ] |
| **Modelo específico** | [Sonet 4.6 |
| **¿Por qué elegiste este LLM?** | [mejor ia opnion persnal] |

---

### Prompt utilizado

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**

```
[Actúa como Ingeniero de Software Senior. Rediseña el módulo de pagos de OpenLib Market aplicando Clean Architecture. Define qué va en cada capa: Entities, Use Cases, Interface Adapters y Frameworks & Drivers. Dibuja un diagrama en Mermaid de la estructura, muestra cómo se respeta la regla de dependencia y explica cómo se inyectan las dependencias externas sin violar la arquitectura.

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
[Entities:
javapublic class Pago {
    private String id;
    private double monto;
    private EstadoPago estado;
    
    public void confirmar() {
        this.estado = EstadoPago.CONFIRMADO;
    }
}

public class Orden {
    private String id;
    private Pago pago;
    
    public void marcarPagada() {
        this.pago.confirmar();
    }
}
Use Cases:
javapublic interface PagoRepository {
    void guardar(Pago pago);
}

public interface NotificadorPago {
    void notificar(Pago pago);
}

public interface PasarelaPago {
    boolean procesar(Pago pago);
}

public class RealizarPagoUseCase {
    private final PagoRepository repositorio;
    private final NotificadorPago notificador;
    private final PasarelaPago pasarela;

    public RealizarPagoUseCase(PagoRepository repositorio,
                                NotificadorPago notificador,
                                PasarelaPago pasarela) {
        this.repositorio = repositorio;
        this.notificador = notificador;
        this.pasarela = pasarela;
    }

    public void ejecutar(Pago pago) {
        pasarela.procesar(pago);
        pago.confirmar();
        repositorio.guardar(pago);
        notificador.notificar(pago);
    }
}
Interface Adapters:
javapublic class PagoController {
    private final RealizarPagoUseCase useCase;

    public ResponseEntity pagar(@RequestBody PagoDTO dto) {
        useCase.ejecutar(dto.toDomain());
        return ResponseEntity.ok().build();
    }
}

public class PagoRepositoryImpl implements PagoRepository {
    public void guardar(Pago pago) {
        // lógica PostgreSQL
    }
}

public class EmailNotificador implements NotificadorPago {
    public void notificar(Pago pago) {
        // enviar email
    }
}
Frameworks & Drivers:
Spring Boot, PostgreSQL, SMTP, pasarela externa de pagos. No contienen lógica de negocio.
![alt text](image.png)
Cómo se respeta la regla de dependencia
Pago y Orden no saben nada de Spring ni de PostgreSQL. RealizarPagoUseCase solo conoce interfaces, nunca implementaciones concretas. Las implementaciones concretas como PagoRepositoryImpl están en la capa de adaptadores y apuntan hacia adentro implementando las interfaces del caso de uso.

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]
```

---

### Análisis crítico de la respuesta

#### 1. ¿Qué hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente?
-Si yo diria que si porque al moento de analisar la respeusta me di cuanta qu esta bien echa 
¿Las restricciones fueron claras?
-si Las restricciones fueron claras porque le pedí explícitamente que definiera cada capa.
- ¿El formato de salida que pediste ayudó a obtener una buena respuesta? 
-Sí ayudó porque le pedí el diagrama y el código por capas separadas,y  hizo que la respuesta fuera ordenada y fácil de entender. 
¿Qué parte de tu prompt fue más efectiva?
-La parte más efectiva fue pedir explícitamente que definiera qué va en cada capa con ejemplos de OpenLib Market.
 Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿Qué se puede mejorar?

[¿Qué le faltó a tu prompt?
-pueda que le halla faltado mas contexo para una respuesta mucho mejor apesar que esta esta bien segun mi analisis.
 ¿Qué harías diferente si pudieras reformularlo?
-mas contexto y mas espesifico con la salida que deseo.
 ¿El LLM entendió mal algo por falta de claridad en tu prompt?
 -segun lo que vi no porque salio la salida tal caul com la pedi en el promt 
  ¿La respuesta tiene errores u omisiones?
  -yo diria que no
   ¿Qué no cubrió el LLM que tú sí sabes por lo visto en clase?
   -pues no explico tan explisito la arquitectura tanto la tradicional como la clean ya que ne a calse no la vimos tan por ensima omo esta en promt]


#### 3. Respuesta final

[Que el módulo de pagos esta  separado en capas donde  cada componente tiene su responsabilidad. El caso de uso solo conoce interfaces, nunca implementaciones concretas, lo que lo hace testeable sin infraestructura real.]
