package com.xmu.supertractor.connection.transmitunit;

import java.io.Serializable;
import java.util.ArrayList;


public class Unit_Pos_Info implements Serializable {
    private static final long serialVersionUID = 1L;
    public ArrayList<Integer> pos_list;
    public ArrayList<Integer> a_or_b;


    public Unit_Pos_Info(ArrayList<Integer> a, ArrayList<Integer> b) {
        this.pos_list = new ArrayList<>(a);
        this.a_or_b = new ArrayList<>(b);
    }


}
