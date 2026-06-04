# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [02]: Principio SOLID

### Estudiante
- **Nombre completo**: Juan Pablo Suarez Moreno

---

### LLM utilizado

| Campo | Valor |
|---|---|
| **Nombre del LLM** | Claude |
| **Modelo específico** | Sonnet 4.6 Bajo |
| **¿Por que elegiste este LLM?** | Se a elegido este modelo gracias a que destaca por su rendimiento a un costo menor de costo de tokens, ademas de ser reconocido por poder depurar y crear codigo eficientemente |

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

[CONTEXTO]
Proyecto: OpenLib Market (Plataforma transaccional E-commerce para libros Open Source).
Módulo actual: Buyer.
Stack Tecnologico: Java 25, Spring Boot 4.x, PostgreSQL/MongoDB, JUnit 5.

[PROBLEMA]
Necesito analizar el código adjunto para identificar fallas o principios SOLID son vulnerados.
Código a analizar:
----------------------------------------
public class ProcesadorPago {
    private String tipoPago; // "TARJETA", "PSE", "PAYPAL"
    
    public ProcesadorPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }
    
    public ResultadoPago procesar(Pago pago) {
        if (tipoPago.equals("TARJETA")) {
            // Validar CVV, fecha expiración, fondos
            // Conectar con API de franquicia (Visa/Mastercard)
            // Procesar cargo
            return new ResultadoPago(true, "Pago con tarjeta procesado");
        } else if (tipoPago.equals("PSE")) {
            // Redirigir a portal bancario
            // Validar retorno de PSE
            // Confirmar débito
            return new ResultadoPago(true, "Pago PSE procesado");
        } else if (tipoPago.equals("PAYPAL")) {
            // Autenticar con OAuth PayPal
            // Ejecutar cargo
            // Registrar webhook de confirmación
            return new ResultadoPago(true, "Pago PayPal procesado");
        }
        throw new IllegalArgumentException("Método de pago no soportado");
    }
}
----------------------------------------

[RESTRICCIONES]
1. Identificar al menos 2 principios SOLID que se estén violando en el diseño actual.
2. Proponer un refactoring completo aplicando un patron de diseño creacional, estructural o de comportamiento que pueda resuelver el problema.
3. El codigo refactorizado debe mantener la compatibilidad con Java 25 y Spring Boot 4.x.

[FORMATO DE SALIDA]
Solicito la respuesta estructurada de la siguiente manera en formato markdown:
1. Analisis de SOLID: Explicación clara de que principios se violan y por que.
2. Eleccion del Patron: Justifica de que patron de diseño se eligiste para la solucionarlo y como resuelve el problema.
3. Codigo Refactorizado: Bloques de codigo limpios, comentados y aplicando Clean Code.

[VALIDACIÓN / CRITERIOS DE ÉXITO]
La solucion es correcta si el nuevo diseño desacopla las responsabilidades y cumple correctamente con los principios SOLID que pueda estar asociado.
```

---

### Respuesta del LLM

> **Si respondiste sin IA, omite esta sección y ve directamente a Análisis crítico.**
Pega aquí la respuesta COMPLETA del LLM, sin editar, sin resumir.
Incluye TODO el texto, código, explicaciones que generó el LLM.

Si el LLM generó código, asegúrate de que esté correctamente formateado.
Si tuviste que hacer varias iteraciones, pega la MEJOR respuesta obtenida,
pero menciona cuántas iteraciones hiciste.]
```

---

### Análisis crítico de la respuesta

#### 1. ¿que hizo bien el prompt?

[Evalúa tu propio prompt, no la respuesta del LLM. ¿El contexto fue suficiente? ¿Las restricciones fueron claras? ¿El formato de salida que pediste ayudó a obtener una buena respuesta? ¿que parte de tu prompt fue más efectiva? Sé específico: menciona fragmentos concretos de tu prompt que funcionaron bien.]


#### 2. ¿que se puede mejorar?

[¿que le faltó a tu prompt? ¿que harías diferente si pudieras reformularlo? ¿El LLM entendió mal algo por falta de claridad en tu prompt? ¿La respuesta tiene errores u omisiones? ¿que no cubrió el LLM que tú sí sabes por lo visto en clase?]


#### 3. Respuesta final

[Escribe tu respuesta definitiva a la pregunta del parcial, integrando lo que aprendiste del LLM pero yendo más allá. Corrige errores, llena omisiones, conecta con conceptos vistos en clase. Esta es tu respuesta: demuestra que tú dominas el tema.]
