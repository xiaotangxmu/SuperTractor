package com.xmu.supertractor.player;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.util.SparseIntArray;


import com.xmu.supertractor.R;
import com.xmu.supertractor.connection.bluetooth.admin.BluetoothAdmin;
import com.xmu.supertractor.connection.transmitunit.TransmitUnit;
import com.xmu.supertractor.connection.transmitunit.Unit_Array_Info;
import com.xmu.supertractor.connection.transmitunit.Unit_Call_Info;
import com.xmu.supertractor.connection.transmitunit.Unit_Push_Broadcast_Info;
import com.xmu.supertractor.connection.transmitunit.Unit_Round_Over_Info;
import com.xmu.supertractor.connection.transmitunit.Unit_Throw_Error_Info;
import com.xmu.supertractor.connection.transmitunit.Unit_Who_To_Push_Info;
import com.xmu.supertractor.connection.transmitunit.Unit_Round_Info;
import com.xmu.supertractor.connection.wifi.admin.WifiAdmin;
import com.xmu.supertractor.activity.UIListener;
import com.xmu.supertractor.parameter.Status;
import com.xmu.supertractor.pokegame.AnalyzeHandPokes;
import com.xmu.supertractor.pokegame.AnalyzeOutCard;
import com.xmu.supertractor.pokegame.Check;
import com.xmu.supertractor.pokegame.PokeGameTools;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.xmu.supertractor.Tools.PrintLog.log;
import static java.lang.Thread.sleep;

public class PlayerService extends Service {
    public Context pContext = null;
    private LocalBinder mLocBin = new LocalBinder();
    public Handler deskHandler = null;
    public Handler playerHandler = null;
    private Me me = null;
    private String tag = "PlayerService";
    private SoundPool soundPool;
    private SparseIntArray musicId;
    private UIListener uiListener = null;

    public void setUIListener(UIListener u) {
        uiListener = u;
    }

    public void check_out() {
        if (Status.out_player != me.seq) {
            Status.check_or_not = false;
            Status.error_str = "还没轮到您";
            uiListener.push_off();
            return;
        }
        if (me.oc.pokes.isEmpty()) {
            Status.check_or_not = false;
            Status.error_str = "出牌不能为空";
            uiListener.push_off();
            return;
        }
        PokeGameTools.cardsort(me.oc.pokes);
        me.oc = AnalyzeOutCard.analyze_out_card(me.oc);
        String res = Check.check_out_cards(Status.first, me.oc, me.hand_card);
        if (res.equals("t")) {
            Status.check_or_not = true;
            uiListener.push_on();
        } else {
            Status.check_or_not = false;
            Status.error_str = res;
            uiListener.push_off();
        }
    }

    static class PlayerServiceHandler extends Handler {
        WeakReference<UIListener> uiListenerWeakReference;
        WeakReference<PlayerService> playerServiceWeakReference;
        WeakReference<String> tagWeakReference;
        WeakReference<Me> meWeakReference;
        UIListener uilistener = null;
        String tag = null;
        Me me = null;
        PlayerService playerService = null;

        PlayerServiceHandler(PlayerService p, UIListener u, String s, Me m) {
            playerServiceWeakReference = new WeakReference<>(p);
            uiListenerWeakReference = new WeakReference<>(u);
            tagWeakReference = new WeakReference<>(s);
            meWeakReference = new WeakReference<>(m);
            uilistener = uiListenerWeakReference.get();
            tag = tagWeakReference.get();
            me = meWeakReference.get();
            playerService = playerServiceWeakReference.get();
        }

        public void handleMessage(Message msg) {

            switch (msg.what) {
                case BluetoothAdmin.MESSAGE_READ_OBJECT:
                    TransmitUnit recdata = (TransmitUnit) (msg.obj);
                    int rectype = recdata.type;
                    log(tag, "Player " + me.seq + " Receive_Data to " + recdata.dest + ",type:" + Status.type_to_s(recdata.type));
                    switch (rectype) {
                        case Status.PUSH_BROADCAST:
                            push_broadcase(recdata);
                            break;
                        case Status.WHO_TO_PUSH:
                            who_to_push(recdata);
                            break;
                        case Status.START_CAll:
                            start_call();
                            break;
                        case Status.THROW_ERROR:
                            throw_error(recdata);
                            break;
                        case Status.BROADCAST_CALL:
                            broadcast_call(recdata);
                            break;
                        case Status.TURN_OVER:
                            Status.biggest_out_player = (int) (recdata.obj);
                            if (Status.biggest_out_player == me.seq)
                                uilistener.ready_next_turn();
                            uilistener.flush_biggest_out_player();
                            break;
                        case Status.DELIVER_EIGHT_CARDS:
                            deliver_eight_cards(recdata);
                            break;
                        case Status.STARG_GAME:
                            Status.status = Status.GAMING;
                            AnalyzeHandPokes.analyze_hand_pokes(me.hand_card);
                            uilistener.start_round();
                            TransmitUnit ui = new TransmitUnit(Status.ACK_STARG_GAME, me.seq, 0, "f");
                            playerService.send_message_to_server(ui);
                            break;
                        case Status.ROUND_INIT:
                            round_init(recdata);
                            break;
                        case Status.ROUND_OVER:
                            round_over(recdata);
                            break;
                        default:
                            break;
                    }
                    break;
                case 98:
                    uilistener.flush_my_card();
                    AnalyzeHandPokes.analyze_hand_pokes(me.hand_card);
                    break;
            }
            super.handleMessage(msg);
        }


        private void round_over(TransmitUnit recdata) {
            Unit_Round_Over_Info ui = (Unit_Round_Over_Info) recdata.obj;
            Status.eight_pokes.clear();
            Status.eight_pokes.addAll(ui.eight_card);
            Status.player_score = ui.score;
            uilistener.flush_score();
            uilistener.show_eight_cards();
        }

        private void throw_error(final TransmitUnit recdata) {
            playerService.soundPool.play(playerService.musicId.get(1), 1, 1, 100, 0, 1);
            Unit_Throw_Error_Info utsei = (Unit_Throw_Error_Info) recdata.obj;
            if (me.seq == utsei.i) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Unit_Throw_Error_Info utei = (Unit_Throw_Error_Info) recdata.obj;
                        ArrayList<Integer> min = utei.out_card_miniimun;
                        ArrayList<Integer> back = utei.send_back_card;
                        me.hand_card.pokes.addAll(back);
                        playerService.send_message_to_server(new TransmitUnit(Status.PUSH, me.seq, 0, new Unit_Array_Info(min)));
                        log(tag, "add back:" + PokeGameTools.array_to_String(back) + "    push:" + PokeGameTools.array_to_String(min));
                        PokeGameTools.cardsort(me.hand_card.pokes);
                        PlayerServiceHandler.this.obtainMessage(98).sendToTarget();
                    }
                }).start();
            }
        }

        private void push_broadcase(TransmitUnit recdata) {
            Unit_Push_Broadcast_Info ui = (Unit_Push_Broadcast_Info) recdata.obj;
            log(tag, "push broadcast,out player :" + Status.out_player);
            Status.push_card.clear();
            Status.push_card.addAll(ui.outcard);
            playerService.send_message_to_server(new TransmitUnit(Status.ACK_PUSH_BROADCAST, me.seq, 0, null));
            uilistener.broadcast_push();
            if (Status.first_out_player == Status.out_player) {
                Status.first.pokes.clear();
                Status.first.pokes.addAll(ui.first);
                Status.first = AnalyzeOutCard.analyze_out_card(Status.first);
                Status.first_out_or_not = false;
            }
        }

        private void who_to_push(TransmitUnit recdata) {
            Unit_Who_To_Push_Info upi = (Unit_Who_To_Push_Info) recdata.obj;
            Status.out_player = upi.who_to_push;
            Status.player_score = upi.score;
            Status.biggest_out_player = upi.biggest_out_player;
            Status.first_out_or_not = upi.first_out_or_not;
            log(tag, "receive WHO_TO_PUSH:" + Status.out_player + ",first_out_player:" + Status.first_out_player + ",first_out_or_not:" + Status.first_out_or_not);
            if (Status.first_out_or_not)
                Status.first_out_player = Status.out_player;
            if (Status.first_out_or_not) {
                uilistener.clear_cards();
            }
            if (Status.out_player == me.seq) {
                Status.check_or_not = false;

                AnalyzeHandPokes.analyze_hand_pokes(me.hand_card);
                playerService.check_out();
            } else
                uilistener.show_push_off();
            uilistener.flush_score();
            uilistener.flush_biggest_out_player();
        }

        private void deliver_eight_cards(TransmitUnit recdata) {
            Unit_Array_Info uai = (Unit_Array_Info) recdata.obj;
            ArrayList<Integer> al = com.xmu.supertractor.Tools.Tools.cast(uai.arr);
            Status.lord_number = al.get(8);
            Status.main_color = al.get(9);
            Log.d("my", "Lord:" + Status.lord_number);
            PokeGameTools.computeval();
            PokeGameTools.cardsort(me.hand_card.pokes);
            uilistener.flush_lord_color_img();
            uilistener.flush_status();
            Status.eight_pokes.clear();
            if (me.seq == Status.lord_number) {
                for (int i = 0; i < 8; ++i)
                    Status.eight_pokes.add(al.get(i));
                uilistener.gai_di_pai();
            } else {
                for (int i = 0; i < 8; ++i)
                    Status.eight_pokes.add(0);
                if (0 == Status.call_player)
                    Status.eight_pokes.set(2, al.get(2));
                uilistener.others_gai_di_pai();
            }
        }

        private void broadcast_call(TransmitUnit recdata) {
            Unit_Call_Info uci = (Unit_Call_Info) recdata.obj;
            int caller = uci.caller;
            int callcard = uci.card;
            int calltype = uci.call_type;
            Log.d("call", "calltype:" + calltype + ",callcard:" + callcard + ",caller:" + caller);
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
            if (callcard < 151)
                Status.main_color = callcard % 10;
            else
                Status.main_color = 0;
            if (Status.first_round) {
                Status.lord_number = caller;        //是第一局游戏的话谁叫主，反主谁就是主
            }
            uilistener.call_info(calltype,callcard,caller);
        }

        private void start_call() {
            Status.call_player = 0;
            Status.call_card = 0;
            Status.re_call_player = 0;
            Status.recall_card = 0;
            Status.insurance_player = 0;
            Status.insurance_card = 0;
            Status.status = Status.CALLING;
            uilistener.start_call();
        }

        private void round_init(TransmitUnit recdata) {
            Unit_Round_Info ur = (Unit_Round_Info) recdata.obj;
            me.hand_card.clear();
            me.hand_card.pokes.addAll(ur.al);
            log(tag, "receive pokes:" + PokeGameTools.array_to_String(ur.al));
            Status.player_score = 0;
            Status.main_level = ur.main_num;
            Status.main_color = ur.main_color;
            Status.level_a = ur.level_a;
            Status.level_b = ur.level_b;
            Status.lord_number = ur.lord_number;
            Status.first_round = ur.first_round;
            PokeGameTools.computeval();
            uilistener.flush_status();
            uilistener.flush_score();
            TransmitUnit ui = new TransmitUnit(Status.ACK_GAME_INIT, me.seq, 0, "f");
            playerService.send_message_to_server(ui);
        }
    }


    public void send_message_to_server(TransmitUnit tu) {
        // 发送数据
        int sour = tu.sour;
        int dest = tu.dest;
        log(tag, "PlayerService Send from:" + sour + "  to:" + dest + ".   type:" + Status.type_to_s(tu.type));
        switch (dest) {
            case 0:
                if (me.server_flag) {
                    Message msg = deskHandler.obtainMessage();
                    msg.what = BluetoothAdmin.MESSAGE_READ_OBJECT;
                    msg.obj = tu;
                    msg.sendToTarget();
                } else if (Status.wifi_or_bluetooth) {
                    WifiAdmin.clientrhread.writeObject(tu);
                } else {
                    BluetoothAdmin.CommunThread_map.get(0).writeObject(tu);
                }
            default:
                break;
        }
    }


    @Override
    public void onCreate() {
        me = Me.get_me();
        me.hand_card.clear();
        Log.d("game", "PlayerService onCreate.");
        pContext = this;
        musicId = new SparseIntArray();

        //noinspection deprecation
        soundPool = new SoundPool(12, 0, 5);
        musicId.put(1, soundPool.load(pContext, R.raw.throw_fail, 1));

        super.onCreate();
    }


    @Override
    public void onDestroy() {
        Log.d("game", "PlayerService onDestory.");
        playerHandler.removeCallbacksAndMessages(null);
        playerHandler=null;
        System.gc();
        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        public PlayerService getService() {
            Log.d("game", "return PlayerService.this;");
            return PlayerService.this;
        }

    }

    public void init() {
        playerHandler = new PlayerServiceHandler(this, uiListener, tag, me);
        if (!me.server_flag) {
            if (Status.wifi_or_bluetooth) {
                WifiAdmin.clientrhread.setHandler(playerHandler);
            } else {
                me.blueComThread.SetHandler(playerHandler);
            }
            TransmitUnit tu = new TransmitUnit(Status.GAME_ACTIVITY_PREPARED, me.seq, 0, null);
            send_message_to_server(tu);
        }
    }


    public IBinder onBind(Intent intent) {
        Log.d("game", "Player OnBind");
        return mLocBin;
    }


}


