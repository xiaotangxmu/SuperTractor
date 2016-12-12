package com.xmu.supertractor.parameter;

import com.xmu.supertractor.card.Out_Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Status {
    public static int connected_num =0;
    public static boolean wifi_or_bluetooth=true;
    public static String error_str;
    public static int main_level;                 //主级牌
    public static int main_color;                //主花色
    public static int level_a;
    public static int level_b;
    public static int lord_number;                //谁是主（1，2，3，4？） 第一局中在收到Call_Lord_First改动，在第一局之后，每局游戏胜利应该会对他进行改动
    public static boolean first_round;          //是否为第一局
    public static boolean first_out_or_not;
    public static boolean check_or_not;
    public static int first_out_player;         //此轮最早出牌人
    public static int out_player;              //当前出牌人
    public static int biggest_out_player;               //牌最大的玩家
    public static int call_card;
    public static int call_player;
    public static int recall_card;
    public static int re_call_player;
    public static int insurance_player;
    public static int insurance_card;
    public static int status;
    public static Out_Card first = new Out_Card();
    public static Out_Card biggest_out = new Out_Card();
    public static ArrayList<Integer> eight_pokes = new ArrayList<>();
    public static Integer player_score;//用户得分
    public static int server_partner;                    //对家
    public static ArrayList<Integer> pos_list = new ArrayList<>(); //用户位置
    public static ArrayList<Integer> push_card = new ArrayList<>();
    public static ArrayList<Integer> level_list = new ArrayList<>();
    private static Map<Integer,String> type;
    public static final int CALLING = 1;
    public static final int GAMING = 2;

    public static final int START_GAME_ACTIVITY = 10;
    public static final int GAME_ACTIVITY_PREPARED = 11;
    public static final int ROUND_INIT = 13; //分发25张牌的tag
    public static final int ACK_GAME_INIT = 14;   //客户端收到分发的25张牌后的tag
    public static final int START_CAll = 15;            //服务器要求客户端一张一张显示牌
    public static final int CALL = 16;
    public static final int BROADCAST_CALL = 17;
    public static final int CALL_OVER = 18;
    public static final int DELIVER_EIGHT_CARDS = 19;
    public static final int PUSH_EIGHT_CARDS = 20;
    public static final int STARG_GAME = 21;
    public static final int ACK_STARG_GAME = 22;

    public static final int WHO_TO_PUSH = 30;
    public static final int PUSH = 31;
    public static final int PUSH_BROADCAST = 32;
    public static final int ACK_PUSH_BROADCAST = 33;
    public static final int THROW_ERROR = 34;
    public static final int TURN_OVER = 35;
    public static final int NEW_TURN = 36;
    public static final int ROUND_OVER = 40;

    public static final int TIME = 80;

    public static final String CALL_INFO = "b";



    static{
        type=new HashMap<>();
        type.put(START_GAME_ACTIVITY,"START_GAME_ACTIVITY");
        type.put(GAME_ACTIVITY_PREPARED,"GAME_ACTIVITY_PREPARED");
        type.put(ROUND_INIT,"ROUND_INIT");
        type.put(ACK_GAME_INIT,"ACK_GAME_INIT");
        type.put(START_CAll,"START_CAll");
        type.put(CALL,"CALL");
        type.put(BROADCAST_CALL,"BROADCAST_CALL");
        type.put(CALL_OVER,"CALL_OVER");
        type.put(DELIVER_EIGHT_CARDS,"DELIVER_EIGHT_CARDS");
        type.put(PUSH_EIGHT_CARDS,"PUSH_EIGHT_CARDS");
        type.put(ACK_STARG_GAME,"ACK_STARG_GAME");
        type.put(WHO_TO_PUSH,"WHO_TO_PUSH");
        type.put(PUSH,"PUSH");
        type.put(PUSH_BROADCAST,"PUSH_BROADCAST");
        type.put(ACK_PUSH_BROADCAST,"ACK_PUSH_BROADCAST");
        type.put(THROW_ERROR,"THROW_ERROR");
        type.put(TURN_OVER,"TURN_OVER");
        type.put(NEW_TURN,"NEW_TURN");
        type.put(ROUND_OVER,"ROUND_OVER");
        type.put(TIME,"TIME");
    }

    public static String type_to_s(int i){
        return type.get(i);
    }
}

