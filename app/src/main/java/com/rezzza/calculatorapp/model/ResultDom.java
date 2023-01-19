package com.rezzza.calculatorapp.model;

import java.io.Serializable;

public class ResultDom implements Serializable {

    private double numbA;
    private double numbB;
    private double value;
    private int id;
    private String expresion;


    public double getNumbA() {
        return numbA;
    }

    public void setNumbA(double numbA) {
        this.numbA = numbA;
    }

    public double getNumbB() {
        return numbB;
    }

    public void setNumbB(double numbB) {
        this.numbB = numbB;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExpresion() {
        return expresion;
    }

    public void setExpresion(String expresion) {
        this.expresion = expresion;
    }

    public String pack(){
        return getNumbA()+"~"+getExpresion()+"~"+getNumbB()+"~"+getValue();
    }
    public void unpack(String text){
        String[] arrText = text.split("~");
        setNumbA(Double.parseDouble(arrText[0]));
        setExpresion(arrText[1]);
        setNumbB(Double.parseDouble(arrText[2]));
        setValue(Double.parseDouble(arrText[3]));
    }
}
