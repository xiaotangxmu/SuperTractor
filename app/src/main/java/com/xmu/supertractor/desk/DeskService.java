package com.xmu.supertractor.desk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.tencent.bugly.crashreport.CrashReport;
import com.xmu.supertractor.Tools.Tools;
import com.xmu.supertractor.card.Hand_Card;
import com.xmu.supertractor.card.Out_Card;
import com.xmu.supertractor.card.Pair;
import com.xmu.supertractor.card.Single;
import com.xmu.supertractor.card.Tractor;
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
import java.util.Collections;
import java.util.Random;

import static com.xmu.supertractor.Tools.PrintLog.log;

public class DeskService extends Service {
    private String tag = "DeskService";
    public Context dContext = null;
    private LocalBinder mLocBin = new LocalBinder();
    public Handler playerHandler = null;
    public Handler deskHandler = null;
    private Desk desk = null;


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
                                    CrashReport.setUserSceneTag(deskService.dContext, 32879);
                                    deskService.game_activity_prepared();
                                    break;
                                case Status.ACK_GAME_INIT:
                                    CrashReport.setUserSceneTag(deskService.dContext, 32879);
                                    deskService.ack_game_init();
                                    break;
                                case Status.CALL:
                                    CrashReport.setUserSceneTag(deskService.dContext, 32881);
                                    deskService.call(recdata);
                                    break;
                                case Status.NEW_TURN:
                                    CrashReport.setUserSceneTag(deskService.dContext, 32882);
                                    deskService.start_turn();
                                    break;
                                case Status.CALL_OVER:
                                    CrashReport.setUserSceneTag(deskService.dContext, 32883);
                                    deskService.call_over();
                                    break;
                                case Status.PUSH_EIGHT_CARDS:
                                    CrashReport.setUserSceneTag(deskService.dContext, 32884);
                                    deskService.push_eight_cards(recdata);
                                    break;
                                case Status.ACK_STARG_GAME:
                                    CrashReport.setUserSceneTag(deskService.dContext, 32880);
                                    deskService.ack_start_game();
                                    break;
                                case Status.PUSH:
                                    CrashReport.setUserSceneTag(deskService.dContext, 32886);
                                    deskService.push(recdata);
                                    break;
                                case Status.ACK_PUSH_BROADCAST:
                                    CrashReport.setUserSceneTag(deskService.dContext, 32887);
                                    deskService.ack_push_broadcast();
                                    break;
                                case Status.READY_NEXT_ROUND:
                                    deskService.ready_next_round(recdata);
                                default:
                                    break;
                            }
                            break;

                    }
                    super.handleMessage(msg);
                    break;
                case Status.TIME:
                    deskService.game_init();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }


    }

    private void ready_next_round(TransmitUnit recdata) {
        if (++desk.prepared_num == 4) {
            desk.prepared_num = 0;
            game_init();
        }
    }


    private void push(TransmitUnit recdata) {
        desk.push_flag = true;
        desk.pushplayer = recdata.sour;
        Unit_Array_Info uai = (Unit_Array_Info) recdata.obj;
        ArrayList<Integer> out_pokes = Tools.cast(uai.arr);
        log(tag, "push Receive Push from:" + desk.pushplayer + PokeGameTools.newLine + PokeGameTools.array_to_String(out_pokes));
        if (desk.out_order_count == 0) {
            Status.first.pokes.clear();
            Status.first.pokes.addAll(out_pokes);
        }
        desk.pushoc = new Out_Card();
        desk.pushoc.pokes.addAll(out_pokes);
        Unit_Push_Broadcast_Info ub = new Unit_Push_Broadcast_Info(out_pokes, Status.first.pokes);
        send_message_to_client(new TransmitUnit(Status.PUSH_BROADCAST, 0, 0, ub));
        desk.pushoc = AnalyzeOutCard.analyze_out_card(desk.pushoc);
        if (desk.pushoc.type == TypeDefine.shuai && (Status.first_out_player == desk.pushplayer)) {
            desk.out_card_miniimun = new ArrayList<>();
            if (!Check.checkthrow(desk.pushoc, desk.pushplayer, desk.out_card_miniimun)) {
                desk.push_flag = false;
                desk.send_back_card = new ArrayList<>();
                desk.pushoc.pokes.removeAll(desk.out_card_miniimun);
                desk.send_back_card.addAll(desk.pushoc.pokes);
                log(tag, "Throw False!----min:" + PokeGameTools.array_to_String(desk.out_card_miniimun) + "   send_back:" + PokeGameTools.array_to_String(desk.send_back_card));
            }
        }
        ack_push_broadcast();
    }


    private void push_eight_cards(TransmitUnit recdata) {
        Unit_Array_Info ui = Tools.cast(recdata.obj);
        ArrayList<Integer> al = Tools.cast(ui.arr);
        log(tag, "receive eight pokes:" + PokeGameTools.array_to_String(al));
        ArrayList<Integer> lal = desk.getMember(Status.lord_number).hc.pokes;
        lal.addAll(desk.getEightPokes());
        for (Integer ig : al)
            lal.remove(ig);
        for (int i = 1; i <= Setting.num_player; ++i)
            PokeGameTools.cardsort(desk.getMember(i).hc.pokes);
        desk.getEightPokes().clear();
        desk.getEightPokes().addAll(al);
        PokeGameTools.cardsort(desk.getEightPokes());
        ArrayList<Integer> ai = new ArrayList<>();
        ai.addAll(al);
        send_message_to_client(new TransmitUnit(Status.STARG_GAME, 0, 0, new Unit_Array_Info(ai)));
    }

    private void call_over() {
        if (++desk.prepared_num == 4) {
            desk.prepared_num = 0;
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
                } else {
                    if (Status.first_round) {
                        desk.setMian_level_a_or_b(Logic.player_level_a_or_b(Status.lord_number));
                    }
                }
            }
            backup.add(Status.lord_number);
            backup.add(Status.main_color);
            backup.add(desk.mian_level_a_or_b ? 1 : 2);
            PokeGameTools.computeval();
            log(tag, "send lord:" + Status.lord_number);
            send_message_to_client(new TransmitUnit(Status.DELIVER_EIGHT_CARDS, 0, 0, new Unit_Array_Info(backup)));
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
            Status.main_color = callcard % 10;
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
        if (++desk.prepared_num == 4) {
            Status.main_color = 0;
            start_call();
            desk.prepared_num = 0;
        }
    }


    private void ack_start_game() {
        if (++desk.prepared_num == 4) {
            desk.prepared_num = 0;
            Status.first_out_player = Status.lord_number;
            Status.player_score = 0;
            start_turn();
        }
    }

    private void game_activity_prepared() {
        log(tag, "prepared_num:" + desk.prepared_num);
        if (++desk.prepared_num == 4) {
            game_init();
            desk.prepared_num = 0;
        }
    }

    private void ack_push_broadcast() {
        if (++desk.prepared_num == 5) {
            desk.prepared_num = 0;
            if (!desk.push_flag) {
                Unit_Throw_Error_Info utri = new Unit_Throw_Error_Info(desk.out_card_miniimun, desk.send_back_card, desk.pushplayer);
                send_message_to_client(new TransmitUnit(Status.THROW_ERROR, 0, 0, utri));
                desk.out_card_miniimun = null;
                return;
            }
            if (++desk.out_order_count == 1) {
                Status.first = desk.pushoc;
                Status.biggest_out = desk.pushoc;
                Status.biggest_out_player = desk.pushplayer;
            } else {
                desk.pushoc.kill = Check.kill_or_not(Status.first, desk.pushoc, desk.pushplayer);
                if (Compare.Compare_Card(desk.pushoc, Status.biggest_out)) {
                    Status.biggest_out = desk.pushoc;
                    Status.biggest_out_player = desk.pushplayer;
                }
            }
            log(tag, "Biggest_out:" + Status.biggest_out_player + ".  " + PokeGameTools.array_to_String(Status.biggest_out.pokes));
            desk.turn_score += acceptCard(desk.pushoc, desk.pushplayer);
            if (4 == desk.out_order_count) {
                int biggest_pos = PokeGameTools.get_player_pos(Status.biggest_out_player);
                log(tag, "biggest_out_player:" + Status.biggest_out_player + "   pos:" + biggest_pos);
                int next_one_pos = PokeGameTools.next_player(Status.lord_number, 1);
                int next_three_pos = PokeGameTools.next_player(Status.lord_number, 3);
                if (Status.biggest_out_player == next_one_pos || Status.biggest_out_player == next_three_pos) {
                    Status.player_score += desk.turn_score;
                    log(tag, "update score:" + Status.player_score);
                }
                if (!Setting.debug_mode) {
                    log(tag, "pokes number:" + desk.getMember(1).hc.pokes.size() + "");
                    if (desk.getMember(1).hc.pokes.isEmpty() || desk.getMember(2).hc.pokes.isEmpty())
                        round_over();
                    else {
                        Status.first_out_player = Status.biggest_out_player;
                        send_message_to_client(new TransmitUnit(Status.TURN_OVER, 0, 0, Status.biggest_out_player));
                    }
                } else {
                    if (desk.turns_count >= Setting.debug_turn_number)
                        round_over();
                    else {
                        Status.first_out_player = Status.biggest_out_player;
                        send_message_to_client(new TransmitUnit(Status.TURN_OVER, 0, 0, Status.biggest_out_player));
                    }
                }
            } else {
                desk.out_player = PokeGameTools.next_player(desk.pushplayer, 1);
                Unit_Who_To_Push_Info ui = new Unit_Who_To_Push_Info(desk.out_player, false, desk.turns_count);
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
        if ((Setting.debug_mode ? Status.player_score >= 5 : Status.player_score >= 80)) {
            Status.lord_number = PokeGameTools.next_player(Status.lord_number, 1);
            int add = 0;
            if (Status.player_score >= 120)
                add = 1;
            else if (Status.player_score >= 160)
                add = 2;
            else if (Status.player_score >= 200)
                add = 3;
            else if (Status.player_score >= 240)
                add = 4;
            else if (Status.player_score >= 280)
                add = 5;
            else if (Status.player_score >= 320)
                add = 6;
            if (desk.mian_level_a_or_b) {
                Status.level_b += add;
                Status.main_level = Status.level_b;
                desk.setMian_level_a_or_b(false);
            } else {
                Status.level_a += add;
                Status.main_level = Status.level_a;
                desk.setMian_level_a_or_b(true);
            }
        } else {
            int add = 0;
            if (Status.player_score == 0)
                add = 3;
            else if (Status.player_score >= 5 && Status.player_score <= 35)
                add = 2;
            else if (Status.player_score >= 40 && Status.player_score <= 75)
                add = 1;
            if (PokeGameTools.get_player_pos(Status.lord_number) == 1 || PokeGameTools.get_player_pos(Status.lord_number) == 3) {
                Status.level_a += add;
                Status.main_level = Status.level_a;
                desk.setMian_level_a_or_b(true);
            } else {
                Status.level_b += add;
                Status.main_level = Status.level_b;
                desk.setMian_level_a_or_b(false);
            }
            Status.lord_number = PokeGameTools.next_player(Status.lord_number, 2);
            if (Status.level_a > 14 || Status.level_b > 14) {
                Status.first_round = true;
                Setting.user_level = false;
            }
        }
        send_message_to_client(new TransmitUnit(Status.ROUND_OVER, 0, 0, new Unit_Round_Over_Info(desk.getEightPokes(), Status.player_score)));
     //   new TimeThread().start();
    }


    private void start_turn() {
        desk.turns_count++;
        desk.turn_score = 0;
        desk.out_order_count = 0;
        Status.biggest_out_player = 0;
        for (int i = 1; i <= 4; ++i)
            AnalyzeHandPokes.analyze_hand_pokes(desk.getMember(i).hc);
        desk.out_player = Status.first_out_player;
        log(tag, "Turn Number:" + desk.turns_count + ",First_out:" + Status.first_out_player);
        Unit_Who_To_Push_Info ui = new Unit_Who_To_Push_Info(desk.out_player, true, desk.turns_count);
        TransmitUnit u = new TransmitUnit(Status.WHO_TO_PUSH, 0, 0, ui);
        send_message_to_client(u);
    }

    public void game_init() {

        desk.round_init();
        desk.turns_count = 0;

        TransmitUnit cardInfo = new TransmitUnit(Status.ROUND_INIT, 0, 0, new Unit_Round_Info(desk.deskplayer_map.get(1).hc.pokes, desk.desknumbner, desk.mian_level_a_or_b, 1));
        send_message_to_client(cardInfo);

        cardInfo = new TransmitUnit(Status.ROUND_INIT, 0, 0, new Unit_Round_Info(desk.deskplayer_map.get(2).hc.pokes, desk.desknumbner, desk.mian_level_a_or_b, 2));
        send_message_to_client(cardInfo);

        cardInfo = new TransmitUnit(Status.ROUND_INIT, 0, 0, new Unit_Round_Info(desk.deskplayer_map.get(3).hc.pokes, desk.desknumbner, desk.mian_level_a_or_b, 3));
        send_message_to_client(cardInfo);

        cardInfo = new TransmitUnit(Status.ROUND_INIT, 0, 0, new Unit_Round_Info(desk.deskplayer_map.get(4).hc.pokes, desk.desknumbner, desk.mian_level_a_or_b, 4));
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


    public int acceptCard(Out_Card out_card, int recsour) {       //接受某个用户出的牌，同时判断是否成功甩牌,在扣除desk中的牌之后，计算牌的分数
        int score = 0;
        AnalyzeOutCard.analyze_out_card(out_card);
        for (Integer ig : out_card.pokes)
            desk.getMember(recsour).hc.pokes.remove(ig);
        AnalyzeHandPokes.analyze_hand_pokes(desk.getMember(recsour).hc);
        for (int i = 0; i < out_card.pokes.size(); i++) {
            int card = out_card.pokes.get(i);
            if (card / 10 == 5) {
                score = score + 5;
            }
            if (card / 10 == 10 || card / 10 == 13) {
                score = score + 10;
            }
        }
        log(tag, "turnscore:" + score);
        return score;
    }


    @Override
    public void onCreate() {
        log(tag, "onCreate");
        dContext = this;
        deskHandler = new DeskHandler(tag, DeskService.this);
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
        log(tag, "onDestory.");
        deskHandler.removeCallbacksAndMessages(null);
        deskHandler = null;
        System.gc();
        super.onDestroy();
    }


    public void init() {
        TransmitUnit tu = new TransmitUnit(Status.START_GAME_ACTIVITY, 0, 0, new Unit_Pos_Info(desk.pos_list, desk.list_a_or_b));
        send_message_to_client(tu);
    }


    @Override
    public IBinder onBind(Intent intent) {
        log(tag, "onBind");
        return mLocBin;
    }


    public class LocalBinder extends Binder {
        public DeskService getService() {
            log(tag, "return DeskService.this;");
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

