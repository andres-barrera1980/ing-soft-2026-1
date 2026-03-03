package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LogaritmoTest {

    private final Logaritmo logaritmo = new Logaritmo();

    // Verifica que el logaritmo base 10 se calcule correctamente
    @Test
    void realizarOperacionLogaritmoExitosa() {
        // log10(100) = 2
        assertEquals(2.0, logaritmo.realizarOperacion(100.0));
    }

    // Verifica que no se permita un número nulo
    @Test
    void realizarOperacionNumeroNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> logaritmo.realizarOperacion(null));
    }

    // Verifica que no se permita calcular logaritmo de cero
    @Test
    void realizarOperacionNumeroCero() {
        assertThrows(ArithmeticException.class,
                () -> logaritmo.realizarOperacion(0.0));
    }

    // Verifica que no se permita calcular logaritmo de un número negativo
    @Test
    void realizarOperacionNumeroNegativo() {
        assertThrows(ArithmeticException.class,
                () -> logaritmo.realizarOperacion(-5.0));
    }
}