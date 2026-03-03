package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

public class Logaritmo {

    public Double realizarOperacion(Double numero) {

        if (numero == null) {
            throw new IllegalArgumentException("El número no puede ser nulo");
        }

        if (numero <= 0) {
            throw new ArithmeticException("El logaritmo solo está definido para números positivos");
        }

        return Math.log10(numero);
    }
}