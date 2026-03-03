package co.edu.javeriana.ingsoft.vibe.code.back;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas para la operación Raíz Cuadrada")
class RaizCuadradaTest {

    @Test
    @DisplayName("Raíz cuadrada de un cuadrado perfecto")
    void testRaizCuadradaPerfecto() {
        RaizCuadrada raiz = new RaizCuadrada();
        assertEquals(3, raiz.aplicar(9));
    }

    @Test
    @DisplayName("Raíz cuadrada de 1")
    void testRaizCuadradaUno() {
        RaizCuadrada raiz = new RaizCuadrada();
        assertEquals(1, raiz.aplicar(1));
    }

    @Test
    @DisplayName("Raíz cuadrada de 0")
    void testRaizCuadradaCero() {
        RaizCuadrada raiz = new RaizCuadrada();
        assertEquals(0, raiz.aplicar(0));
    }

    @Test
    @DisplayName("Raíz cuadrada de número no perfecto (truncada)")
    void testRaizCuadradaNoPerf() {
        RaizCuadrada raiz = new RaizCuadrada();
        assertEquals(3, raiz.aplicar(10)); // sqrt(10) ≈ 3.16 -> 3
    }

    @Test
    @DisplayName("Raíz cuadrada de número grande")
    void testRaizCuadradaGrande() {
        RaizCuadrada raiz = new RaizCuadrada();
        assertEquals(1000, raiz.aplicar(1000000));
    }

    @Test
    @DisplayName("Raíz cuadrada de número negativo lanza excepción")
    void testRaizCuadradaNegativo() {
        RaizCuadrada raiz = new RaizCuadrada();
        assertThrows(ArithmeticException.class, () -> {
            raiz.aplicar(-4);
        });
    }

    @Test
    @DisplayName("Verificar nombre de operación")
    void testNombre() {
        RaizCuadrada raiz = new RaizCuadrada();
        assertEquals("Raíz Cuadrada", raiz.getNombre());
    }
}
