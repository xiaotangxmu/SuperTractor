package com.xmu.supertractor.connection.transmitunit;

import java.io.Serializable;


public class Unit_Player_Info implements Serializable{
    private static final long serialVersionUID = 1L;
   public int seq;
    public String name;


    public Unit_Player_Info(int seq, String name) {
        this.seq=seq;
        this.name=name;
    }


}
