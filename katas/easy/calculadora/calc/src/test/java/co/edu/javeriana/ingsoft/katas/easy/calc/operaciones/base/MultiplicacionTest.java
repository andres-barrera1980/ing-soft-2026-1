package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

import com.sun.jdi.IntegerType;

public class MultiplicacionTest {
    public int Multiplicar (int a, int b){ //Metodo para multiplicar numeros enteros
        long resultado= (long) a*b;
        if (resultado< Integer.MIN_VALUE || resultado>Integer.MAX_VALUE)
            throw new ArithmeticException("Hay desbordamiento en el codigo.");
        return (int) resultado;
    }
}
