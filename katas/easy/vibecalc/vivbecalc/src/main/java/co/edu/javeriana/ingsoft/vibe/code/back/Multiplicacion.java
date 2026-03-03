package co.edu.javeriana.ingsoft.vibe.code.back;

public class Multiplicacion implements OperacionBinaria {
    @Override
    public Integer aplicar(Integer a, Integer b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        
        // Verificar overflow
        if (a > 0 && b > 0 && a > Integer.MAX_VALUE / b) {
            throw new ArithmeticException("Overflow: la multiplicación excede Integer.MAX_VALUE");
        }
        if (a < 0 && b < 0 && a < Integer.MAX_VALUE / b) {
            throw new ArithmeticException("Overflow: la multiplicación excede Integer.MAX_VALUE");
        }
        if (a > 0 && b < 0 && b < Integer.MIN_VALUE / a) {
            throw new ArithmeticException("Underflow: la multiplicación es menor que Integer.MIN_VALUE");
        }
        if (a < 0 && b > 0 && a < Integer.MIN_VALUE / b) {
            throw new ArithmeticException("Underflow: la multiplicación es menor que Integer.MIN_VALUE");
        }
        
        return a * b;
    }

    @Override
    public String getNombre() {
        return "Multiplicación";
    }
}
