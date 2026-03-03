package co.edu.javeriana.ingsoft.vibe.code.back;

import java.util.HashMap;
import java.util.Map;

public class Fibonacci implements OperacionUnaria {
    private Map<Integer, Integer> cache = new HashMap<>();

    public Fibonacci() {
        // Inicializar con valores base
        cache.put(0, 0);
        cache.put(1, 1);
    }

    @Override
    public Integer aplicar(Integer n) {
        if (n < 0) {
            throw new ArithmeticException("No se puede calcular Fibonacci de números negativos");
        }
        
        return fibonacciRecursivo(n);
    }

    private Integer fibonacciRecursivo(Integer n) {
        // Si está en cache, retornar directamente
        if (cache.containsKey(n)) {
            return cache.get(n);
        }

        // Calcular recursivamente
        long fib = (long) fibonacciRecursivo(n - 1) + (long) fibonacciRecursivo(n - 2);

        // Validar overflow
        if (fib > Integer.MAX_VALUE || fib < Integer.MIN_VALUE) {
            throw new ArithmeticException("Overflow: Fibonacci excede Integer.MAX_VALUE");
        }

        int resultado = (int) fib;
        // Guardar en cache
        cache.put(n, resultado);

        return resultado;
    }

    @Override
    public String getNombre() {
        return "Fibonacci";
    }

    /**
     * Limpia el cache. Útil para resetear en pruebas.
     */
    public void limpiarCache() {
        cache.clear();
        cache.put(0, 0);
        cache.put(1, 1);
    }
}
