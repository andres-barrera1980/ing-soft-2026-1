package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LogaritmoTest {

    private final Logaritmo logaritmo = new Logaritmo();

    // Verifica que el logaritmo base 10 se calcule correctamente para Integer
    @Test
    void realizarOperacionLogaritmoExitosa() {
        // log10(100) = 2 => método devuelve Integer 2
        assertEquals(Integer.valueOf(2), logaritmo.realizarOperacion(100));
    }

    // Verifica que no se permita un número nulo
    @Test
    void realizarOperacionNumeroNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> logaritmo.realizarOperacion((Integer) null));
    }

    // Verifica que no se permita calcular logaritmo de cero
    @Test
    void realizarOperacionNumeroCero() {
        assertThrows(ArithmeticException.class,
                () -> logaritmo.realizarOperacion(0));
    }

    // Verifica que no se permita calcular logaritmo de un número negativo
    @Test
    void realizarOperacionNumeroNegativo() {
        assertThrows(ArithmeticException.class,
                () -> logaritmo.realizarOperacion(-5));
    }
}