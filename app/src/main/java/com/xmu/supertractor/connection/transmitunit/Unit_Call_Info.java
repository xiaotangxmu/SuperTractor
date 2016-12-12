package com.xmu.supertractor.connection.transmitunit;

import java.io.Serializable;


public class Unit_Call_Info implements Serializable {
    private static final long serialVersionUID = 1L;
    public Integer card;
    public int call_type;
    public int caller;


    public Unit_Call_Info(Integer card, int type, int caller) {
        this.card = card;
        this.call_type = type;
        this.caller = caller;
    }

}