package com.calculadora;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class CalculadoraTest {

    @Test
    public void logaritmoDe100() {
        assertEquals(2, Calculadora.log10(100));
    }

    @Test
    public void logaritmoDe10() {
        assertEquals(1, Calculadora.log10(10));
    }

    @Test
    public void logaritmoDe1() {
        assertEquals(0, Calculadora.log10(1));
    }

    @Test
    public void logaritmoDeCero() {
        assertThrows(IllegalArgumentException.class, () -> {
            Calculadora.log10(0);
        });
    }

    @Test
    public void logaritmoNegativo() {
        assertThrows(IllegalArgumentException.class, () -> {
            Calculadora.log10(-5);
        });
    }

}
