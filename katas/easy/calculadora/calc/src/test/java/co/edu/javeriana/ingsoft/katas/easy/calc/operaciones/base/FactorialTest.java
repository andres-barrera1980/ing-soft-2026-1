package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FactorialTest {

    private final Factorial factorial = new Factorial();

    @Test
    void realizarOperacionFactorialExitoso() {
        assertEquals(120, factorial.realizarOperacion(5));
    }

    @Test
    void realizarOperacionCero() {
        assertEquals(1, factorial.realizarOperacion(0));
    }

    @Test
    void realizarOperacionNumeroNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> factorial.realizarOperacion(null));
    }

    @Test
    void realizarOperacionNumeroNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> factorial.realizarOperacion(-3));
    }

    @Test
    void realizarOperacionOverflow() {
        // 13! produce overflow en Integer
        assertThrows(ArithmeticException.class,
                () -> factorial.realizarOperacion(13));
    }
}