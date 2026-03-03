package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

public class Logaritmo extends OperacionUnaria{

    public Integer realizarOperacion(Integer numero) {

        if (numero == null) {
            throw new IllegalArgumentException("El número no puede ser nulo");
        }

        if (numero <= 0) {
            throw new ArithmeticException("El logaritmo solo está definido para números positivos");
        }

        return (int) Math.log10(numero);
    }
}