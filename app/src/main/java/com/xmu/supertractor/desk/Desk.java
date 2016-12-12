package com.xmu.supertractor.desk;


import android.util.Log;
import android.util.SparseArray;

import com.xmu.supertractor.connection.bluetooth.BluetoothComThread;
import com.xmu.supertractor.parameter.Setting;
import com.xmu.supertractor.parameter.Status;
import com.xmu.supertractor.pokegame.PokeGameTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.xmu.supertractor.Tools.PrintLog.log;

public class Desk {
    private Random rand;
    private ArrayList<Integer> deskPokes;// 108张牌
    private ArrayList<Integer> eightPokes;// 8张底牌
    SparseArray<Member> deskplayer_map;
    public ArrayList<Integer> pos_list;
    int out_player;
    private String tag = "Desk";
    private static Desk desk = null;
    public ArrayList<Integer> list_a_or_b;

    private Desk() {
        deskplayer_map = new SparseArray<>();
        deskPokes = new ArrayList<>();
        eightPokes = new ArrayList<>();
        pos_list = new ArrayList<>();
        list_a_or_b = new ArrayList<>();
        list_a_or_b.add(0, 0);
        list_a_or_b.add(1, 1);
        list_a_or_b.add(2, 2);
        list_a_or_b.add(3, 2);
        list_a_or_b.add(4, 2);
        rand = new Random();
    }

    private static void status_init() {
        Status.main_color = 0;
        Status.player_score = 0;

        if (Setting.user_level) {
            Status.first_round = false;
            Log.d("push", "Status:" + Status.level_a + ":" + Status.level_b + ",user_level:true");
        } else {
            if (Status.first_round) {
                Status.main_level = 2;
                Status.level_a = 2;
                Status.level_b = 2;
                Status.lord_number = 0;
            }
            Log.d("push", "Status:" + Status.level_a + ":" + Status.level_b + ",user_level:false");
        }
    }

    public static void init_desk() {
        log("Desk", "init_desk.");
        desk = null;
        desk = new Desk();
        Status.connected_num = 0;
    }

    public static Desk dk_getInstance() {
        if (desk == null) {
            desk = new Desk();
        }
        return desk;
    }

    public Member getMember(int i) {
        return deskplayer_map.get(i);
    }

    void round_init() {
        status_init();
        PokeGameTools.computeval();
        initpokes(deskPokes);
        shuffle(deskPokes);
        takeeight(deskPokes, eightPokes);
        distribute(deskPokes, deskplayer_map);
    }

    ArrayList<Integer> getEightPokes() {
        return eightPokes;
    }

    public void compute_pos(int num) {
        switch (num) {
            case 2:
                pos_list.add(2);
                pos_list.add(2);
                pos_list.add(3);
                pos_list.add(1);
                pos_list.add(4);
                break;
            case 3:
                pos_list.add(3);
                pos_list.add(3);
                pos_list.add(4);
                pos_list.add(1);
                pos_list.add(2);
                break;
            case 4:
                pos_list.add(4);
                pos_list.add(4);
                pos_list.add(2);
                pos_list.add(1);
                pos_list.add(3);
                break;
            default:
                break;
        }
    }

    private void initpokes(List<Integer> list) {
        list.clear();
        for (int j = 0; j < 2; ++j) {
            for (int i = 0; i < 52; ++i)
                list.add(20 + i / 4 * 10 + i % 4 + 1);
            list.add(151);
            list.add(161);
        }
    }

    private void shuffle(List<Integer> list) {
        for (int l = 0; l < list.size(); l++) {
            int des = rand.nextInt(list.size());
            int temp = list.get(l);
            list.set(l, list.get(des));
            list.set(des, temp);
        }
    }

    private void takeeight(List<Integer> list, List<Integer> list2) {
        list2.clear();
        for (int i = 0; i < 8; ++i) {
            list2.add(list.get(i + 100));
        }
    }

    private void distribute(List<Integer> list, SparseArray<Member> map) {
        for (int i = 1; i <= 4; ++i) {
            map.get(i).hc.clear();
            for (int j = (i - 1) * 25; j < i * 25; ++j) {
                map.get(i).hc.pokes.add(list.get(j));
            }

        }
    }

    public void add_player(int seq, String name, BluetoothComThread ComThread) {
        if (!exist_or_not(seq)) {
            Member dp = new Member(seq, name, ComThread);
            log(tag, "deskmember add seq:" + seq + "," + name + ".thread:" + (ComThread == null ? ("null") : ComThread.toString()));
            deskplayer_map.put(seq, dp);
        } else {
            Log.d("my", seq + " player already exist");
        }
    }

    public void add_player(int seq, String name) {
        if (!exist_or_not(seq)) {
            Member dp = new Member(seq, name);
            log(tag, "deskmember add seq:" + seq + "," + name);
            deskplayer_map.put(seq, dp);
        } else {
            log(tag, seq + " player already exist");
        }
    }


    public boolean exist_or_not(int seq) {
        if (deskplayer_map.size() == 0)
            return false;
        boolean exist = false;
        for (int i = 0, nsize = deskplayer_map.size(); i < nsize; i++) {
            Member m = deskplayer_map.valueAt(i);
            if (m.seq == seq) {
                exist = true;
                break;
            }
        }
        return exist;
    }


}

