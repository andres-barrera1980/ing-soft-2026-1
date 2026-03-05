package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

public class Potenciacion extends OperacionBinaria {

    @Override
    public Integer realizarOperacion(Integer base, Integer exp) {

        if (base == null || exp == null) {
            throw new IllegalArgumentException("Los números no pueden ser nulos");
        }

        if (base == 0 && exp == 0) {
            throw new ArithmeticException("0^0 es indeterminado");
        }

        if (exp < 0) {
            throw new ArithmeticException("Exponente negativo no soportado en enteros");
        }

        int resultado = 1;

        for (int i = 0; i < exp; i++) {

            int r = resultado * base;

            // Lógica tipo Math.multiplyExact para detectar overflow
            if (base != 0 && r / base != resultado) {
                throw new ArithmeticException("integer overflow");
            }

            resultado = r;
        }

        return resultado;
    }
}