package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

public class OperacionValorAbsoluto  {
    public Integer realizarOperacion(Integer num) {
        if (num == null) {
            throw new IllegalArgumentException("El número no puede ser nulo");
        }

        // Caso especial: overflow
        if (num.equals(Integer.MIN_VALUE)) {
            throw new ArithmeticException("integer overflow");
        }

        return (num >= 0) ? num : -num;
    }
}
