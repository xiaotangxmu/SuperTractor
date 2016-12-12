package com.xmu.supertractor.connection.transmitunit;


import java.io.Serializable;
import java.util.ArrayList;


public class Unit_Throw_Error_Info implements Serializable {
    private static final long serialVersionUID = 1L;
    public ArrayList<Integer> out_card_miniimun;
    public ArrayList<Integer> send_back_card;
    public int i;


    public Unit_Throw_Error_Info(ArrayList<Integer> out_card_miniimun, ArrayList<Integer> send_back_card,int j) {
        this.out_card_miniimun=new ArrayList<>(out_card_miniimun);
        this.send_back_card=new ArrayList<>(send_back_card);
        this.i=j;
    }
}