package com.xmu.supertractor.connection.transmitunit;

import com.xmu.supertractor.parameter.Status;

import java.io.Serializable;
import java.util.ArrayList;


public class Unit_Round_Info implements Serializable {
    private static final long serialVersionUID = 1L;
    public ArrayList<Integer> al;
    public int dest_player;
    public int main_num;                 //主级牌
    public int main_color;                //主花色
    public int level_a;
    public int level_b;
    public int lord_number;                //谁是主（1，2，3，4？） 第一局中在收到Call_Lord_First改动，在第一局之后，每局游戏胜利应该会对他进行改动
    public boolean first_round;          //是否为第一局
    public int desknumber;
    public boolean main_level_a_or_b;

    public Unit_Round_Info(ArrayList<Integer> al, int i,boolean b,int j) {
        this.al = new ArrayList<>(al);
        this.main_num = Status.main_level;
        this.main_color = Status.main_color;
        this.level_a = Status.level_a;
        this.level_b = Status.level_b;
        this.lord_number = Status.lord_number;
        this.first_round = Status.first_round;
        this.desknumber = i;
        this.dest_player=j;
        this.main_level_a_or_b=b;
    }
}
