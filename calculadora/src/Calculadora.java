public class Calculadora {

    public static double log10(double x) {

        if (x <= 0) {
            throw new IllegalArgumentException("Numero invalido");
        }

        return Math.log10(x);
    }

}
