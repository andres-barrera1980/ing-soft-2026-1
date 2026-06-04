# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [01]: [Solid principio]

### Estudiante
- **Marlon Garcia**: [Tu nombre y apellido]

---


### Respuesta sin IA 

La clase viola el principio de abierto-cerrado, porque el método "procesar" está construido con una estructura de if, else if y else if para decidir qué tipo de pago ejecutar. Esto significa que, si en el futuro se quiere agregar un nuevo método de pago, como Nequi, habría que modificar directamente el método `procesar` para añadir una nueva condición.

Además, el uso de strings como "TARJETA", "PSE" o "PAYPAL" hace que el código sea frágil, ya que un error de escritura como "Nequi", "NEQUI " o "nequi" podría hacer que el sistema no reconozca el método de pago y lance un error al usuario.



### Refactor del codigo 
```java
 public class ProcesadorPago {

    // Ahora puede recibir cualquier método de pago que implemente la interfaz MetodoPago.
    private final MetodoPago metodoPago;

    // Esto permite inyectar diferentes formas de pago como Tarjeta, PSE, PayPal, Nequi, etc.
    public ProcesadorPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    // Procesa el pago usando el método de pago recibido.
    // ProcesadorPago no necesita saber cómo funciona internamente cada método de pago.
    public ResultadoPago procesar(Pago pago) {
        return metodoPago.procesar(pago);
    }
}
```
