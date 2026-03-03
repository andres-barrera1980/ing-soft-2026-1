package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

public class Multiplicacion extends OperacionBinaria{
@Override
public Integer realizarOperacion(Integer num1, Integer num2) {      //Metodo para multiplicar numeros enteros.
    long resultado = (long) num1 * num2; //Hacemos que el resultado de la multiplicacion se guarde en la varible resultado.
        if (resultado < Integer.MIN_VALUE || resultado > Integer.MAX_VALUE) //Montamos la excepción, que consiste en: si el las entradas son mayores a los valores permitidos.
            throw new ArithmeticException("Hay desbordamiento en el codigo."); //Imprime la excepcion vista.
        return (int) resultado; /*Si todo esta en orden, se retorna el resultado de la operación.*/
        }
    }