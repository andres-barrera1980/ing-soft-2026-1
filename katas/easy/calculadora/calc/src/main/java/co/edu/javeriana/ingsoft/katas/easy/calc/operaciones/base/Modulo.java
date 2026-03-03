package co.edu.javeriana.ingsoft.katas.easy.calc.operaciones.base;

public  class Modulo extends OperacionBinaria{
    @Override
    public  Integer realizarOperacion(Integer num1, Integer num2){
        return num1%num2;
    }

}
