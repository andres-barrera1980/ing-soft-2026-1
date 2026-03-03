package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

public class Factorial extends OperacionUnaria {

    @Override
    public Integer realizarOperacion(Integer num) {
        if (num == null) {
            throw new IllegalArgumentException("El número no puede ser nulo");
        }

        if (num < 0) {
            throw new IllegalArgumentException("No existe factorial para números negativos");
        }

        int resultado = 1;

        for (int i = 1; i <= num; i++) {
            // Usamos multiplyExact para detectar overflow
            resultado = Math.multiplyExact(resultado, i);
        }

        return resultado;
    }
}