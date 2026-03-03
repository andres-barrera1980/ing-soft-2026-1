package co.edu.javeriana.ingsoft.vibe.code.back;

public class RaizCuadrada implements OperacionUnaria {
    @Override
    public Integer aplicar(Integer a) {
        if (a < 0) {
            throw new ArithmeticException("No se puede calcular raíz cuadrada de números negativos");
        }
        
        if (a == 0) {
            return 0;
        }
        
        return (int) Math.sqrt(a);
    }

    @Override
    public String getNombre() {
        return "Raíz Cuadrada";
    }
}
