package com.xmu.supertractor.connection.transmitunit;

import java.io.Serializable;


public class TransmitUnit implements Serializable {
    private static final long serialVersionUID = 1L;
    public int type;
    public int sour;
    public int dest;
    public Object obj;

    public TransmitUnit(int type, int sour, int dest, Object obj) {
        this.type = type;
        this.sour = sour;
        this.dest = dest;
        this.obj=obj;
    }
    

}


