package com.xmu.supertractor.connection.transmitunit;

import com.xmu.supertractor.parameter.Status;

import java.io.Serializable;


public class Unit_Who_To_Push_Info implements Serializable {
    private static final long serialVersionUID = 1L;
    public int who_to_push;
    public boolean first_out_or_not;
    public int score;
    public int biggest_out_player;
    public int turns;

    public Unit_Who_To_Push_Info(int who_to_push, boolean first_out_or_not,int turns) {
        this.who_to_push = who_to_push;
        this.first_out_or_not = first_out_or_not;
        this.score = Status.player_score;
        this.biggest_out_player=Status.biggest_out_player;
        this.turns=turns;
    }
}
