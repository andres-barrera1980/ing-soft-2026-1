package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

public class Potenciacion {
    public static long potencia(long base, int exp) {
        if (base == 0 && exp == 0) throw new ArithmeticException("0^0 es indeterminado");
        if (exp < 0) throw new ArithmeticException("Exponente negativo no soportado en enteros");

        long resultado = 1;
        for (int i = 0; i < exp; i++) {
            // Verificar overflow ANTES de multiplicar
            if (resultado != 0 && Math.abs(resultado) > Long.MAX_VALUE / Math.abs(base)) {
                throw new ArithmeticException("Desbordamiento: el resultado supera el límite de long");
            }
            resultado *= base;
        }
        return resultado;
    }
}