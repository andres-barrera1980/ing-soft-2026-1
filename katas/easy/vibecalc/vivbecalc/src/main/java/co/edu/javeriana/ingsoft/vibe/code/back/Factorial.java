package co.edu.javeriana.ingsoft.vibe.code.back;

public class Factorial implements OperacionUnaria {
    @Override
    public Integer aplicar(Integer a) {
        if (a < 0) {
            throw new ArithmeticException("No se puede calcular factorial de números negativos");
        }
        
        long resultado = 1;
        for (int i = 2; i <= a; i++) {
            resultado *= i;
            
            // Validar overflow
            if (resultado > Integer.MAX_VALUE) {
                throw new ArithmeticException("Overflow: factorial excede Integer.MAX_VALUE");
            }
        }
        
        return (int) resultado;
    }

    @Override
    public String getNombre() {
        return "Factorial";
    }
}
