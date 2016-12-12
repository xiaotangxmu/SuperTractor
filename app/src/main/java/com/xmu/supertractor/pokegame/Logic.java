package com.xmu.supertractor.pokegame;

import com.xmu.supertractor.desk.Desk;
import com.xmu.supertractor.parameter.Setting;
import com.xmu.supertractor.parameter.Status;

import static com.xmu.supertractor.Tools.PrintLog.log;



public class Logic {
    private static String tag = "GameLogic";

    public static void setpartner(int i) {
        Status.server_partner = i;
        log(tag, "set partner:" + Status.server_partner + "," + Desk.dk_getInstance().getMember(Status.server_partner).name);
    }

    public static boolean main_level_a_or_b(){
        return Logic.player_level_a_or_b(Status.lord_number);
    }

    public static boolean player_level_a_or_b(int i) {
        return 1 == Status.level_list.get(i);
    }

    public static void seta_or_b(int i) {
        Desk.dk_getInstance().list_a_or_b.set(1, 1);
        Desk.dk_getInstance().list_a_or_b.set(2, 2);
        Desk.dk_getInstance().list_a_or_b.set(3, 2);
        Desk.dk_getInstance().list_a_or_b.set(4, 2);
        Desk.dk_getInstance().list_a_or_b.set(i, 1);
        log(tag, " " + Desk.dk_getInstance().list_a_or_b.get(1) + " " + Desk.dk_getInstance().list_a_or_b.get(2) + " " + Desk.dk_getInstance().list_a_or_b.get(3) + " " + Desk.dk_getInstance().list_a_or_b.get(4));
    }

    public static void setzhuang(int i) {
        Status.lord_number = i;
        log(tag, "set load:" + Status.lord_number + "," + Desk.dk_getInstance().getMember(Status.lord_number).name);
    }

    public static void setlevala(int i) {
        Status.level_a = i;
        log(tag, "set level a:" + Status.level_a);
    }

    public static void setlevalb(int i) {
        Status.level_b = i;
        log(tag, "set level b:" + Status.level_b);
    }

    public static void setmainlevel(int i) {
        Status.main_level = i;
        log(tag, "set main level:" + Status.main_level);
    }

    public static void init_new_game_status() {
        Setting.user_level = false;
        Status.first_round = true;
        Status.level_a = 2;
        Status.level_b = 2;
        Status.main_level = 2;

    }

    public static void init_custom_status() {
        Setting.user_level = true;
        Status.first_round = false;
        Status.lord_number = 0;
        Status.level_a = 0;
        Status.level_b = 0;
        Status.main_level = 0;
    }
}
