package com.xmu.supertractor.connection.transmitunit;

import java.io.Serializable;
import java.util.ArrayList;


public class Unit_Push_Broadcast_Info implements Serializable {
    private static final long serialVersionUID = 1L;

    public ArrayList<Integer> first;
    public ArrayList<Integer> outcard;

    public Unit_Push_Broadcast_Info(ArrayList<Integer> pokes,ArrayList<Integer> first){
        this.first=new ArrayList<>(first);
        this.outcard=new ArrayList<>(pokes);
    }
}
