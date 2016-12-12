package com.xmu.supertractor.connection.transmitunit;

import java.io.Serializable;
import java.util.ArrayList;



public class Unit_Array_Info implements Serializable {
    private static final long serialVersionUID = 1L;
    public ArrayList<?> arr;
    public Unit_Array_Info(ArrayList a){
        arr=new ArrayList<>();
        arr.addAll(a);
    }
}
