package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

public  class Modulo extends OperacionBinaria{
   @Override
    public Integer realizarOperacion(Integer num1, Integer num2) {

        if (num1 == null || num2 == null) {
            throw new IllegalArgumentException("Los valores no pueden ser null.");
        }

        if (num2 == 0) {
            throw new ArithmeticException("No se puede dividir por 0.");
        }

        return num1 % num2;
    }

}
