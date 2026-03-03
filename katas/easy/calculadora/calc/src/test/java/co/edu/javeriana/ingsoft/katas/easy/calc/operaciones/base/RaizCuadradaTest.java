package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RaizCuadradaTest {

    private final RaizCuadrada raizCuadrada = new RaizCuadrada();

    @Test
    void realizarOperacionRaizExitosa() {
        assertEquals(3.0, raizCuadrada.realizarOperacion(9.0));
    }

    @Test
    void realizarOperacionNumeroNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> raizCuadrada.realizarOperacion(null));
    }

    @Test
    void realizarOperacionNumeroNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> raizCuadrada.realizarOperacion(-4.0));
    }

    @Test
    void realizarOperacionNumeroCero() {
        assertEquals(0.0, raizCuadrada.realizarOperacion(0.0));
    }
}