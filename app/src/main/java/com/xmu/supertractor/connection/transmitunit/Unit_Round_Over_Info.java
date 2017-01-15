package com.xmu.supertractor.connection.transmitunit;


import java.io.Serializable;
import java.util.ArrayList;


public class Unit_Round_Over_Info implements Serializable {
    private static final long serialVersionUID = 1L;
    public ArrayList<Integer> eight_card;
    public int score;

    public Unit_Round_Over_Info(ArrayList<Integer> eight_card, int score) {
        this.eight_card = new ArrayList<>(eight_card);
        this.score = score;
    }
}