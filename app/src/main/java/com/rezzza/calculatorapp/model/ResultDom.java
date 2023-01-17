package com.rezzza.calculatorapp.model;

import java.io.Serializable;

public class ResultDom implements Serializable {

    private int numbA;
    private int numbB;
    private int value;
    private int id;
    private String expresion;


    public int getNumbA() {
        return numbA;
    }

    public void setNumbA(int numbA) {
        this.numbA = numbA;
    }

    public int getNumbB() {
        return numbB;
    }

    public void setNumbB(int numbB) {
        this.numbB = numbB;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
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
        setNumbA(Integer.parseInt(arrText[0]));
        setExpresion(arrText[1]);
        setNumbB(Integer.parseInt(arrText[2]));
        setValue(Integer.parseInt(arrText[3]));
    }
}
