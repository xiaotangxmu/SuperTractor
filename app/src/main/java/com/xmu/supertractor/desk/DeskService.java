package com.xmu.supertractor.desk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.xmu.supertractor.Tools.Tools;
import com.xmu.supertractor.card.Hand_Card;
import com.xmu.supertractor.card.Out_Card;
import com.xmu.supertractor.card.Pair;
import com.xmu.supertractor.card.Single;
import com.xmu.supertractor.card.TypeDefine;
import com.xmu.supertractor.connection.bluetooth.admin.BluetoothAdmin;
import com.xmu.supertractor.connection.transmitunit.TransmitUnit;
import com.xmu.supertractor.connection.transmitunit.Unit_Array_Info;
import com.xmu.supertractor.connection.transmitunit.Unit_Call_Info;
import com.xmu.supertractor.connection.transmitunit.Unit_Pos_Info;
import com.xmu.supertractor.connection.transmitunit.Unit_Push_Broadcast_Info;
import com.xmu.supertractor.connection.transmitunit.Unit_Round_Info;
import com.xmu.supertractor.connection.transmitunit.Unit_Round_Over_Info;
import com.xmu.supertractor.connection.transmitunit.Unit_Throw_Error_Info;
import com.xmu.supertractor.connection.transmitunit.Unit_Who_To_Push_Info;
import com.xmu.supertractor.connection.wifi.admin.WifiAdmin;
import com.xmu.supertractor.parameter.Setting;
import com.xmu.supertractor.parameter.Status;
import com.xmu.supertractor.pokegame.AnalyzeHandPokes;
import com.xmu.supertractor.pokegame.AnalyzeOutCard;
import com.xmu.supertractor.pokegame.Check;
import com.xmu.supertractor.pokegame.Compare;
import com.xmu.supertractor.pokegame.Logic;
import com.xmu.supertractor.pokegame.PokeGameTools;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;

import static com.xmu.supertractor.Tools.PrintLog.log;


public class DeskService extends Service {

    public Context dContext = null;
    private LocalBinder mLocBin = new LocalBinder();
    public Handler playerHandler = null;
    public  Handler deskHandler = null;
    private int prepared_num = 1;
    private Desk desk = null;
    private int turn_count;
    private int turn_score;
    private boolean push_flag;
    private Out_Card pushoc;
    private String tag = "DeskService";
    private int pushplayer;
    private ArrayList<Integer> out_card_miniimun;
    private ArrayList<Integer> send_back_card;


    public void send_message_to_client(TransmitUnit tu) {
        // 发送数据
        int sour = tu.sour;
        int dest = tu.dest;
        log(tag, "DeskService Send from:" + sour + "  to:" + dest + ".   type:" + Status.type_to_s(tu.type));
        switch (dest) {
            case 0:
                if (Status.wifi_or_bluetooth) {
                    WifiAdmin.CommunThread_map.get(2).writeObject(tu);
                    WifiAdmin.CommunThread_map.get(3).writeObject(tu);
                    WifiAdmin.CommunThread_map.get(4).writeObject(tu);
                } else {
                    BluetoothAdmin.CommunThread_map.get(2).writeObject(tu);
                    BluetoothAdmin.CommunThread_map.get(3).writeObject(tu);
                    BluetoothAdmin.CommunThread_map.get(4).writeObject(tu);
                }
            case 1:
                Message msg = playerHandler.obtainMessage();
                msg.what = BluetoothAdmin.MESSAGE_READ_OBJECT;
                msg.obj = tu;
                msg.sendToTarget();
                break;
            case 2:
                if (Status.wifi_or_bluetooth) {
                    WifiAdmin.CommunThread_map.get(2).writeObject(tu);
                } else {
                    BluetoothAdmin.CommunThread_map.get(2).writeObject(tu);
                }
                break;
            case 3:
                if (Status.wifi_or_bluetooth) {
                    WifiAdmin.CommunThread_map.get(3).writeObject(tu);
                } else {
                    BluetoothAdmin.CommunThread_map.get(3).writeObject(tu);
                }
                break;
            case 4:
                if (Status.wifi_or_bluetooth) {
                    WifiAdmin.CommunThread_map.get(4).writeObject(tu);
                } else {
                    BluetoothAdmin.CommunThread_map.get(4).writeObject(tu);
                }
                break;
            default:
                break;
        }
    }


    static class DeskHandler extends Handler {
        WeakReference<DeskService> deskServiceWeakReference;
        WeakReference<String> tagWeakReference;
        String tag = null;
        DeskService deskService = null;

        DeskHandler(String s, DeskService d) {
            tagWeakReference = new WeakReference<>(s);
            tag = tagWeakReference.get();
            deskServiceWeakReference = new WeakReference<>(d);
            deskService = deskServiceWeakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothAdmin.MESSAGE_READ_OBJECT:
                    TransmitUnit recdata = (TransmitUnit) (msg.obj);
                    int recsour = recdata.sour;
                    int rectype = recdata.type;
                    log(tag, "Desk Receive_Data from " + recsour + ":" + " type:" + Status.type_to_s(recdata.type));
                    switch (msg.what) {
                        case BluetoothAdmin.MESSAGE_READ_OBJECT:
                            switch (rectype) {
                                case Status.GAME_ACTIVITY_PREPARED:
                                    deskService.game_activity_prepared();
                                    break;
                                case Status.ACK_GAME_INIT:
                                    deskService.ack_game_init();
                                    break;
                                case Status.CALL:
                                    deskService.call(recdata);
                                    break;
                                case Status.NEW_TURN:
                                    deskService.start_turn();
                                    break;
                                case Status.CALL_OVER:
                                    deskService.call_over();
                                    break;
                                case Status.PUSH_EIGHT_CARDS:
                                    deskService.push_eight_cards(recdata);
                                    break;
                                case Status.ACK_STARG_GAME:
                                    deskService.ack_start_game();
                                    break;
                                case Status.PUSH:
                                    deskService.push(recdata);
                                    break;
                                case Status.ACK_PUSH_BROADCAST:
                                    deskService.ack_push_broadcast();
                                default:
                                    break;
                            }
                            break;

                    }
                    super.handleMessage(msg);
                    break;
                case Status.TIME:
                    if (Status.level_a < 13 && Status.level_b < 13)
                        deskService.game_init();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }


    }


    private void push(TransmitUnit recdata) {
        push_flag = true;
        pushplayer = recdata.sour;
        Unit_Array_Info uai = (Unit_Array_Info) recdata.obj;
        ArrayList<Integer> out_pokes = Tools.cast(uai.arr);
        log(tag, "push Receive Push from:" + pushplayer + PokeGameTools.newLine + PokeGameTools.array_to_String(out_pokes));
        if (turn_count == 0) {
            Status.first.pokes.clear();
            Status.first.pokes.addAll(out_pokes);
        }
        pushoc = new Out_Card();
        pushoc.pokes.addAll(out_pokes);
        Unit_Push_Broadcast_Info ub = new Unit_Push_Broadcast_Info(out_pokes, Status.first.pokes);
        send_message_to_client(new TransmitUnit(Status.PUSH_BROADCAST, 0, 0, ub));
        pushoc = AnalyzeOutCard.analyze_out_card(pushoc);
        if (pushoc.type == TypeDefine.shuai && (Status.first_out_player == pushplayer)) {
            out_card_miniimun = new ArrayList<>();
            if (!checkthrow(pushoc, pushplayer, out_card_miniimun)) {
                push_flag = false;
                send_back_card = new ArrayList<>();
                pushoc.pokes.removeAll(out_card_miniimun);
                send_back_card.addAll(pushoc.pokes);
                log(tag, "Throw False!----min:" + PokeGameTools.array_to_String(out_card_miniimun) + "   send_back:" + PokeGameTools.array_to_String(send_back_card));
            }
        }
        ack_push_broadcast();
    }


    private void push_eight_cards(TransmitUnit recdata) {
        ArrayList<Integer> al = Tools.cast(recdata.obj);
        ArrayList<Integer> lal = desk.getMember(Status.lord_number).hc.pokes;
        lal.addAll(desk.getEightPokes());
        for (Integer ig : al)
            lal.remove(ig);
        for (int i = 1; i <= Setting.num_player; ++i)
            PokeGameTools.cardsort(desk.getMember(i).hc.pokes);
        desk.getEightPokes().clear();
        desk.getEightPokes().addAll(al);
        PokeGameTools.cardsort(desk.getEightPokes());
        send_message_to_client(new TransmitUnit(Status.STARG_GAME, 0, 0, null));
    }

    private void call_over() {
        if (++prepared_num == 4) {
            ArrayList<Integer> backup = new ArrayList<>();
            backup.addAll(desk.getEightPokes());
            if (!Setting.user_level) {
                if (0 == Status.call_player) {
                    if (Status.first_round) {
                        Random rand = new Random();
                        Status.lord_number = rand.nextInt(3) + 1;
                    }
                    int color = desk.getEightPokes().get(2);
                    Status.main_color = color >= 151 ? 0 : color % 10;
                }
            }
            backup.add(Status.lord_number);
            backup.add(Status.main_color);
            PokeGameTools.computeval();
            Log.d("my", "send lord:" + Status.lord_number);
            send_message_to_client(new TransmitUnit(Status.DELIVER_EIGHT_CARDS, 0, 0, new Unit_Array_Info(backup)));
            prepared_num = 0;
        }
    }

    private void call(TransmitUnit recdata) {
        int callcard = ((Unit_Call_Info) recdata.obj).card;
        int calltype = ((Unit_Call_Info) recdata.obj).call_type;
        int caller = ((Unit_Call_Info) recdata.obj).caller;
        if (calltype == 1) {
            Status.call_card = callcard;
            Status.call_player = caller;
        } else if (calltype == 2) {
            Status.recall_card = callcard;
            Status.re_call_player = caller;
        } else if (calltype == 3) {
            Status.insurance_card = callcard;
            Status.insurance_player = caller;
        }
        if (callcard < 151) {
            Status.main_color = callcard % 10;           //这个改变主花色
        } else {
            Status.main_color = 0;     //无主
        }

        if (Status.first_round) {
            Status.lord_number = caller;        //是第一局游戏的话谁叫主，反主谁就是主
        }
        //不是第一局游戏的话Status.lord_number，谁是主将在上一局游戏结束后改变，不是叫主的时候
        //不是第一局游戏的话Status main number，哪一张牌是主牌在上一局游戏结束后改变，不是叫主的时候
        Unit_Call_Info unit_call_info = new Unit_Call_Info(callcard, calltype, caller);
        Inform_Client_Change(unit_call_info);
    }

    private void ack_game_init() {
        if (++prepared_num == 4) {
            Status.main_color = 0;
            start_call();
            prepared_num = 0;
        }
    }


    private void ack_start_game() {
        if (++prepared_num == 4) {
            prepared_num = 0;
            Status.first_out_player = Status.lord_number;
            Status.player_score = 0;
            start_turn();
        }
    }

    private void game_activity_prepared() {
        Log.d("call", "prepared_num:" + prepared_num);
        if (++prepared_num == 4) {
            game_init();
            prepared_num = 0;
        }
    }

    private void ack_push_broadcast() {
        if (++prepared_num == 5) {
            prepared_num = 0;
            if (!push_flag) {
                Unit_Throw_Error_Info utri = new Unit_Throw_Error_Info(out_card_miniimun, send_back_card, pushplayer);
                send_message_to_client(new TransmitUnit(Status.THROW_ERROR, 0, 0, utri));
                out_card_miniimun = null;
                return;
            }
            if (++turn_count == 1) {
                Status.first = pushoc;
                Status.biggest_out = pushoc;
                Status.biggest_out_player = pushplayer;
            } else {
                pushoc.kill = Check.kill_or_not(Status.first, pushoc, pushplayer);
                if (Compare.Compare_Card(pushoc, Status.biggest_out)) {
                    Status.biggest_out = pushoc;
                    Status.biggest_out_player = pushplayer;
                }
            }
            Log.d("push", "Biggest_out:" + Status.biggest_out_player + ".  " + PokeGameTools.array_to_String(Status.biggest_out.pokes));
            turn_score += acceptCard(pushoc, pushplayer);
            if (4 == turn_count) {
                int biggest_pos = PokeGameTools.get_player_pos(Status.biggest_out_player);
                Log.d("push", "biggest_out_player:" + Status.biggest_out_player + "   pos:" + biggest_pos);
                int next_one_pos = PokeGameTools.next_player(Status.lord_number, 1);
                int next_three_pos = PokeGameTools.next_player(Status.lord_number, 3);
                if (Status.biggest_out_player == next_one_pos || Status.biggest_out_player == next_three_pos) {
                    Status.player_score += turn_score;
                    Log.d("push", "update score:" + Status.player_score);
                }
                if (!Setting.debug_mode) {
                    if (desk.getMember(1).hc.pokes.isEmpty())
                        round_over();
                    else {
                        Status.first_out_player = Status.biggest_out_player;
                        send_message_to_client(new TransmitUnit(Status.TURN_OVER, 0, 0, Status.biggest_out_player));
                    }
                } else {
                    if (desk.getMember(1).hc.pokes.size() <= Setting.debug_number)
                        round_over();
                    else {
                        Status.first_out_player = Status.biggest_out_player;
                        send_message_to_client(new TransmitUnit(Status.TURN_OVER, 0, 0, Status.biggest_out_player));
                    }
                }
            } else {
                desk.out_player = PokeGameTools.next_player(pushplayer, 1);
                Unit_Who_To_Push_Info ui = new Unit_Who_To_Push_Info(desk.out_player, false);
                send_message_to_client(new TransmitUnit(Status.WHO_TO_PUSH, 0, 0, ui));
            }
        }
    }

    private void round_over() {
        Setting.user_level = false;
        Status.first_round = false;
        if (Status.biggest_out_player == PokeGameTools.next_player(Status.lord_number, 1) || Status.biggest_out_player == PokeGameTools.next_player(Status.lord_number, 3)) {
            int bottom_score = 0;
            for (Integer ig : desk.getEightPokes()) {
                if (5 == ig / 10)
                    bottom_score += 5;
                if (10 == ig / 10 || 13 == ig / 10)
                    bottom_score += 10;
            }
            Status.player_score += (bottom_score * 2);
        }
        if (Status.player_score >= 80) {
            Status.lord_number = PokeGameTools.next_player(Status.lord_number, 1);
            if (Logic.main_level_a_or_b())
                Status.main_level = Status.level_b;
            else
                Status.main_level = Status.level_a;
        } else {
            if (PokeGameTools.get_player_pos(Status.lord_number) == 1 || PokeGameTools.get_player_pos(Status.lord_number) == 3) {
                Status.level_a++;
                Status.main_level = Status.level_a;
            } else {
                Status.level_b++;
                Status.main_level = Status.level_b;
            }
            Status.lord_number = PokeGameTools.next_player(Status.lord_number, 2);

        }
        send_message_to_client(new TransmitUnit(Status.ROUND_OVER, 0, 0, new Unit_Round_Over_Info(desk.getEightPokes(), Status.player_score)));
        new TimeThread().start();
    }


    private void start_turn() {
        turn_score = 0;
        turn_count = 0;
        for (int i = 1; i <= 4; ++i)
            AnalyzeHandPokes.analyze_hand_pokes(desk.getMember(i).hc);
        desk.out_player = Status.first_out_player;
        Log.d("push", "First_out:" + Status.first_out_player);
        Unit_Who_To_Push_Info ui = new Unit_Who_To_Push_Info(desk.out_player, true);
        TransmitUnit u = new TransmitUnit(Status.WHO_TO_PUSH, 0, 0, ui);
        send_message_to_client(u);
    }

    public void game_init() {

        desk.round_init();

        TransmitUnit cardInfo = new TransmitUnit(Status.ROUND_INIT, 0, 1, new Unit_Round_Info(desk.deskplayer_map.get(1).hc.pokes));
        send_message_to_client(cardInfo);

        cardInfo = new TransmitUnit(Status.ROUND_INIT, 0, 2, new Unit_Round_Info(desk.deskplayer_map.get(2).hc.pokes));
        send_message_to_client(cardInfo);

        cardInfo = new TransmitUnit(Status.ROUND_INIT, 0, 3, new Unit_Round_Info(desk.deskplayer_map.get(3).hc.pokes));
        send_message_to_client(cardInfo);

        cardInfo = new TransmitUnit(Status.ROUND_INIT, 0, 4, new Unit_Round_Info(desk.deskplayer_map.get(4).hc.pokes));
        send_message_to_client(cardInfo);
    }

    private void start_call() { //通知用户地显示25张手牌里的
        Status.call_player = 0;
        Status.call_card = 0;
        Status.re_call_player = 0;
        Status.recall_card = 0;
        Status.insurance_player = 0;
        Status.insurance_card = 0;
        TransmitUnit ShowOne = new TransmitUnit(Status.START_CAll, 0, 0, null);
        send_message_to_client(ShowOne);
    }

    private void Inform_Client_Change(Unit_Call_Info unit_call_info) {
        TransmitUnit Inform_Lord_Change = new TransmitUnit(Status.BROADCAST_CALL, 0, 0, unit_call_info);
        send_message_to_client(Inform_Lord_Change);
    }

    public boolean checkthrow(Out_Card out_card, int recsour, ArrayList<Integer> al) {
        boolean valid_shuai = true;
        Hand_Card First_player_card = desk.deskplayer_map.get(PokeGameTools.next_player(recsour, 1)).hc;
        Hand_Card Second_player_card = desk.deskplayer_map.get(PokeGameTools.next_player(recsour, 2)).hc;
        Hand_Card Third_player_card = desk.deskplayer_map.get(PokeGameTools.next_player(recsour, 3)).hc;
        int color = out_card.color;
        ArrayList<Hand_Card> hc_list = new ArrayList<>();
        hc_list.add(First_player_card);
        hc_list.add(Second_player_card);
        hc_list.add(Third_player_card);
        al.clear();
        if (out_card.pokes.get(0).intValue() != out_card.pokes.get(1).intValue()) {
            Single out_single = new Single(out_card.pokes.get(0));
            int i = 1;
            for (Hand_Card hd : hc_list) {
                if (hd.single_map.get(color).isEmpty()) {
                    ++i;
                    continue;
                }
                ArrayList<Single> als = hd.single_map.get(color);
                for (Single s : als)
                    if (out_single.value < s.value) {
                        valid_shuai = false;
                        log(tag, "Throw fail!!!!!    player:" + Status.out_player + " Single:" + out_single.pokes.get(0) + " < " + desk.getMember(PokeGameTools.next_player(recsour, i)).name + " Single:" + s.pokes);
                        al.addAll(out_single.pokes);
                        break;
                    }
                if (!valid_shuai)
                    break;
                i++;
            }
        } else {
            Pair out_pair = new Pair(out_card.pokes.get(0));
            int i = 1;
            for (Hand_Card hd : hc_list) {
                if (hd.pair_map.get(color).isEmpty()) {
                    ++i;
                    continue;
                }
                ArrayList<Pair> alp = hd.pair_map.get(color);
                for (Pair p : alp)
                    if (out_pair.value < p.value) {
                        log(tag, "Throw fail!!!!!    player:" + Status.out_player + " Pair:" + out_pair.pokes.get(0) + " < " + desk.getMember(PokeGameTools.next_player(recsour, i)).name + " Single:" + p.pokes);
                        valid_shuai = false;
                        al.addAll(out_pair.pokes);
                        break;
                    }
                if (!valid_shuai)
                    break;
                ++i;
            }
        }
        return valid_shuai;
    }

    public int acceptCard(Out_Card out_card, int recsour) {       //接受某个用户出的牌，同时判断是否成功甩牌,在扣除desk中的牌之后，计算牌的分数
        int score = 0;
        AnalyzeOutCard.analyze_out_card(out_card);
        for (Integer ig : out_card.pokes)
            desk.getMember(recsour).hc.pokes.remove(ig);
        AnalyzeHandPokes.analyze_hand_pokes(desk.getMember(recsour).hc);
        //找用户发来的牌里的分数，包括5，10，K。
        for (int i = 0; i < out_card.pokes.size(); i++) {
            int card = out_card.pokes.get(i);
            if (card / 10 == 5) {
                score = score + 5;
            }
            if (card / 10 == 10 || card / 10 == 13) {
                score = score + 10;
            }
        }
        Log.d("push", "turnscore:" + score);
        return score;
    }


    @Override
    public void onCreate() {
        Log.d("desk", "DeskService onCreate.");
        dContext = this;
        deskHandler=new DeskHandler(tag,DeskService.this);
        desk = Desk.dk_getInstance();
        if (Status.wifi_or_bluetooth) {
            WifiAdmin.CommunThread_map.get(2).setHandler(deskHandler);
            WifiAdmin.CommunThread_map.get(3).setHandler(deskHandler);
            WifiAdmin.CommunThread_map.get(4).setHandler(deskHandler);
        } else {
            BluetoothAdmin.CommunThread_map.get(2).SetHandler(deskHandler);
            BluetoothAdmin.CommunThread_map.get(3).SetHandler(deskHandler);
            BluetoothAdmin.CommunThread_map.get(4).SetHandler(deskHandler);
        }

        super.onCreate();
    }

    public void onDestroy() {
        Log.d("desk", "DeskService onDestory.");
        deskHandler.removeCallbacksAndMessages(null);
        deskHandler=null;
        System.gc();
        super.onDestroy();
    }


    public void init() {
        TransmitUnit tu = new TransmitUnit(Status.START_GAME_ACTIVITY, 0, 0, new Unit_Pos_Info(desk.pos_list, desk.list_a_or_b));
        send_message_to_client(tu);
    }


    @Override
    public IBinder onBind(Intent intent) {
        Log.d("desk", "Desk OnBind");
        return mLocBin;
    }


    public class LocalBinder extends Binder {
        public DeskService getService() {
            Log.d("desk", "return DeskService.this;");
            return DeskService.this;
        }
    }

    class TimeThread extends Thread {

        @Override
        public void run() {
            try {
                Thread.sleep(1500);
                Message msg = new Message();
                msg.what = Status.TIME;  //消息(一个整型值)
                deskHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

