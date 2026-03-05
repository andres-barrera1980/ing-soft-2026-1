package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

public class Resta {

    public Double realizarOperacion(Double a, Double b) {

        if (a == null || b == null) {
            throw new IllegalArgumentException("Los números no pueden ser nulos");
        }

        return a - b;
    }
}
