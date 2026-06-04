# Plantilla de entrega — Parcial 2

> **Instrucción**: Copia esta plantilla para cada pregunta del parcial. Reemplaza `[Pregunta XX]` por el identificador correcto (ej: `P01_solid_srp`) y completa todas las secciones. Haz al menos 2 commits por pregunta: uno con el prompt + respuesta del LLM, y otro con el análisis.

---

## Pregunta [02]: [P02_Solid_refactor]

### Estudiante
- **Nombre completo**: [Diego Alejandro Torres Barragan]

---

Pregunta Contestada sin IA 

#### 1. Respuesta final

[El principio que se viola en esta pregunta , principalmente es la O (open/close) ya que la clase procesar pago tiene una condicion if/else encadenado para cada metodo de pago , y si en un futuro sale un nuevo metodo de pago, tocac modificar directamente esata clase agregando otro condicional if/else y eso es exactamente lo que este principio dice que no se debe de hacer, pues el codigo debe estar abierto para extenderce pero no cerrado para modificarse. 

Otro patron que violando es la S de los SOLID ya que como en el punto anterior se esta manejando la logica de tres procesos (PSE, Tarjetas y paypal) en una misma clase y pues eso se puede separar aplicando este principio. 

Ahora el patron que yo propondira para refactorizar este punto es Strategy 

pues este praton no ayuda a que en ves de tener un if/else por tipo de pago , se crea una interfaz metodoPago, asi cada tipo de pago tendria su propia clase. asi ProcesarPAgo solo tendria que recibir metodo de pago y ejecutarlo sin importar cual sea , asi solucionando el proble aya que si llega un metodo de pago nuevo , solo es crea una clase nueva sin hacerle cambios a lo que ya existe  ]

// Interfaz que deben implementar todos los métodos de pago
public interface MetodoPago {
    ResultadoPago procesar(Pago pago);
}

// Implementación para tarjeta
public class PagoTarjeta implements MetodoPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Validar CVV, fecha expiración, fondos
        // Conectar con API de franquicia
        return new ResultadoPago(true, "Pago con tarjeta procesado");
    }
}

// Implementación para PSE
public class PagoPSE implements MetodoPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Redirigir a portal bancario
        // Confirmar débito
        return new ResultadoPago(true, "Pago PSE procesado");
    }
}

// Implementación para PayPal
public class PagoPayPal implements MetodoPago {
    @Override
    public ResultadoPago procesar(Pago pago) {
        // Autenticar con OAuth
        // Ejecutar cargo
        return new ResultadoPago(true, "Pago PayPal procesado");
    }
}

// ProcesadorPago ya no sabe qué método de pago es, solo lo ejecuta
public class ProcesadorPago {

    private final MetodoPago metodoPago;

    public ProcesadorPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public ResultadoPago procesar(Pago pago) {
        return metodoPago.procesar(pago);
    }
}