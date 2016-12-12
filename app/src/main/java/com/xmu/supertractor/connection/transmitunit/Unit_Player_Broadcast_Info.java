package com.xmu.supertractor.connection.transmitunit;

import java.io.Serializable;
import java.util.HashMap;



public class Unit_Player_Broadcast_Info implements Serializable {
    private static final long serialVersionUID = 1L;
    public int num;
    public HashMap<Integer,String> player_map;
    public Unit_Player_Broadcast_Info(){
        num=0;
        player_map=new HashMap<>();
    }
}
