package com.xmu.supertractor.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmu.supertractor.R;

import com.xmu.supertractor.connection.transmitunit.TransmitUnit;
import com.xmu.supertractor.connection.transmitunit.Unit_Array_Info;
import com.xmu.supertractor.connection.transmitunit.Unit_Call_Info;
import com.xmu.supertractor.desk.Desk;
import com.xmu.supertractor.desk.DeskService;
import com.xmu.supertractor.parameter.Setting;
import com.xmu.supertractor.parameter.Status;
import com.xmu.supertractor.parameter.ViewControl;
import com.xmu.supertractor.player.Me;
import com.xmu.supertractor.player.PlayerList;
import com.xmu.supertractor.player.PlayerService;
import com.xmu.supertractor.pokegame.Logic;
import com.xmu.supertractor.pokegame.PokeGameTools;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.xmu.supertractor.Tools.PrintLog.log;

@SuppressLint("SetTextI18n")
@SuppressWarnings("deprecation")
public class GameActivity extends Activity {

    private String tag = "GameActivity";
    public Context gContext = null;
    private DeskService deskservice = null;
    private PlayerService playerservice = null;

    private CardOnClick l;
    private AbsoluteLayout al_mine;
    private AbsoluteLayout al_south;
    private AbsoluteLayout al_north;
    private AbsoluteLayout al_west;
    private AbsoluteLayout al_east;
    private RelativeLayout rl_cent;
    private MyView mv_main_card;
    private AbsoluteLayout al_center_card;
    private int count;
    private ArrayList<Integer> distribute_card_list = null;
    private ArrayList<Integer> temp_show = new ArrayList<>();
    private SparseArray<Bitmap> pic_map = new SparseArray<>();
    private Me me = null;

    private int daxiaowang;
    private AbsoluteLayout al_call;
    private MyView mv_east;
    private MyView mv_north;
    private MyView mv_south;
    private MyView mv_west;
    private AbsoluteLayout al_push;
    private MyView bt_push;
    private TextView tv_score;
    private TextView tv_level_my;
    private TextView tv_level_opponent;
    private AbsoluteLayout al_ready_push;
    private TextView tv_southname;
    private TextView tv_northname;
    private TextView tv_eastname;
    private TextView tv_westname;
    private BitmapDrawable bd;
    private SparseArray<MyView> call_butto_map;
    private TimeThread timeshowthread;
    private TextView tv_time;
    private TimeHandler timeHandler;

    private ServiceConnection connectiondesk = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            deskservice = ((DeskService.LocalBinder) service).getService();
            initService();
        }

    };
    private ServiceConnection connectionplayer = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            playerservice = ((PlayerService.LocalBinder) service).getService();
            playerservice.setUIListener(new MyUiListener());
            playerservice.init();
        }

    };


    private void initService() {
        deskservice.playerHandler = playerservice.playerHandler;
        playerservice.deskHandler = deskservice.deskHandler;
        deskservice.init();
    }

    class MyUiListener implements UIListener{

        @Override
        public void flush_biggest_out_player() {
            tv_eastname.setTextColor(Color.WHITE);
            tv_southname.setTextColor(Color.WHITE);
            tv_westname.setTextColor(Color.WHITE);
            tv_northname.setTextColor(Color.WHITE);
            switch (PokeGameTools.get_player_pos(Status.biggest_out_player)) {
                case 1:
                    tv_northname.setTextColor(Color.RED);
                    break;
                case 2:
                    tv_westname.setTextColor(Color.RED);
                    break;
                case 3:
                    tv_southname.setTextColor(Color.RED);
                    break;
                case 4:
                    tv_eastname.setTextColor(Color.RED);
                    break;
            }
        }

        @Override
        public void show_push_off(){
            al_push.setVisibility(View.GONE);
            al_push.setEnabled(false);
        }

        @Override
        public void ready_next_turn() {
            al_push.setEnabled(false);
            al_push.setVisibility(View.GONE);
            al_ready_push.setVisibility(View.VISIBLE);
            al_ready_push.setEnabled(true);
        }

        @Override
        public void clear_cards() {
            al_east.removeAllViews();
            al_north.removeAllViews();
            al_south.removeAllViews();
            al_west.removeAllViews();
            System.gc();
            tv_eastname.setTextColor(Color.WHITE);
            tv_southname.setTextColor(Color.WHITE);
            tv_westname.setTextColor(Color.WHITE);
            tv_northname.setTextColor(Color.WHITE);
        }


        @Override
        public void flush_score() {
            tv_score.setText("得分:" + Status.player_score);
            Log.d("push", "flush score:" + Status.player_score);
        }

        @Override
        public void show_eight_cards() {
            clear_cards();
            al_center_card.setVisibility(View.VISIBLE);
            add_card_center(Status.eight_pokes, true);
        }

        @Override
        public void push_on() {
            al_push.setVisibility(View.VISIBLE);
            bt_push.change_img(getbitmap(R.drawable.push));
            al_push.setEnabled(true);
        }

        @Override
        public void push_off() {
            al_push.setVisibility(View.VISIBLE);
            bt_push.change_img(getbitmap(R.drawable.push_gray));
            al_push.setEnabled(true);
        }

        @Override
        public void broadcast_push() {
            switch (PokeGameTools.get_player_pos(Status.out_player)) {
                case 1:
                    add_card_north(Status.push_card);
                    break;
                case 2:
                    add_card_west(Status.push_card);
                    break;
                case 3:
                    add_card_south(Status.push_card);
                    break;
                case 4:
                    add_card_east(Status.push_card);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void start_round() {
            al_push.setVisibility(View.GONE);
            al_north.setVisibility(View.VISIBLE);
            al_south.setVisibility(View.VISIBLE);
            al_east.setVisibility(View.VISIBLE);
            al_west.setVisibility(View.VISIBLE);
            add_card_mine(me.hand_card.pokes, true);
            al_center_card.setVisibility(View.GONE);
        }

        @Override
        public void flush_lord_color_img() {
            set_card_main();
            mv_north.change_img(getbitmap(R.drawable.player));
            mv_west.change_img(getbitmap(R.drawable.player));
            mv_south.change_img(getbitmap(R.drawable.player));
            mv_east.change_img(getbitmap(R.drawable.player));
            switch (PokeGameTools.get_player_pos(Status.lord_number)) {
                case 1:
                    mv_north.change_img(pic_map.get(1));
                    break;
                case 2:
                    mv_west.change_img(pic_map.get(1));
                    break;
                case 3:
                    mv_south.change_img(pic_map.get(1));
                    break;
                case 4:
                    mv_east.change_img(pic_map.get(1));
                    break;
            }
        }

        @Override
        public void others_gai_di_pai() {
            al_center_card.setVisibility(View.VISIBLE);
            add_card_center(Status.eight_pokes, false);
        }

        @Override
        public void gai_di_pai() {
            al_push.setVisibility(View.VISIBLE);
            al_center_card.setVisibility(View.VISIBLE);
            push_on();
            add_card_center(Status.eight_pokes, true);
            add_card_mine(me.hand_card.pokes, true);
        }

        @Override
        public void flush_status() {
            if (Logic.player_level_a_or_b(me.seq)) {
                tv_level_my.setText(Status.level_a + "");
                tv_level_opponent.setText(Status.level_b + "");
            } else {
                tv_level_my.setText(Status.level_b + "");
                tv_level_opponent.setText(Status.level_a + "");
            }
            if (0 != Status.lord_number) {
                if (Status.lord_number == me.seq || Status.lord_number == PokeGameTools.next_player(me.seq, 2)) {
                    tv_level_my.setTextColor(Color.RED);
                    tv_level_opponent.setTextColor(Color.WHITE);
                } else {
                    tv_level_opponent.setTextColor(Color.RED);
                    tv_level_my.setTextColor(Color.WHITE);
                }
            } else {
                tv_level_opponent.setTextColor(Color.WHITE);
                tv_level_my.setTextColor(Color.WHITE);
            }
            set_card_main();
        }

        @Override
        public void start_call() {
            al_center_card.removeAllViews();
            clear_cards();
            al_push.setVisibility(View.GONE);
            al_call.setVisibility(View.VISIBLE);
            log(tag, "--------------------show card.-------------------------");
            distribute_card_list = me.hand_card.pokes;
            count = 0;
            daxiaowang = 0;
            new DistributeThread(me.hand_card.pokes).start();
        }

        @Override
        public void call_info(int a,int b,int c) {
            ArrayList<Integer> temp = new ArrayList<>();
            if (1 == a)
                temp.add(b);
            else if (2 == a || 3 == a) {
                temp.add(b);
                temp.add(b);
            }
            set_card_main();
            switch (PokeGameTools.get_player_pos(c)) {
                case 1:
                    add_card_north(temp);
                    break;
                case 2:
                    add_card_west(temp);
                    break;
                case 3:
                    add_card_south(temp);
                    break;
                case 4:
                    add_card_east(temp);
                    break;
                default:
                    break;
            }
            update_call_button(temp_show);
        }

        @Override
        public void flush_my_card(){
            add_card_mine(me.hand_card.pokes, true);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log(tag, "GameActivity onCreate.");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);
        gContext = this;
        me = Me.get_me();
        timeHandler = new TimeHandler(GameActivity.this);
        if (me.server_flag) {
            Desk.dk_getInstance().compute_pos(Status.server_partner);
            Status.pos_list.clear();
            Status.pos_list.addAll(Desk.dk_getInstance().pos_list);
            Status.level_list.clear();
            Status.level_list.addAll(Desk.dk_getInstance().list_a_or_b);
        }
        view_init();
        Intent intent = new Intent();
        intent.setClass(gContext, PlayerService.class);
        bindService(intent, connectionplayer, BIND_AUTO_CREATE);
        if (me.server_flag) {
            intent.setClass(gContext, DeskService.class);
            bindService(intent, connectiondesk, BIND_AUTO_CREATE);
        }
    }

    private Bitmap getbitmap(int id) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = getResources().openRawResource(id);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    private void view_init() {
        l = new CardOnClick();
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        ViewControl.compute(dm.heightPixels, dm.widthPixels);
        LinearLayout rl = (LinearLayout) findViewById(R.id.rl);
        assert rl != null;
        bd = new BitmapDrawable(getResources(), getbitmap(R.raw.background));
        rl.setBackgroundDrawable(bd);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_level_my = (TextView) findViewById(R.id.tv_level_left);
        tv_level_opponent = (TextView) findViewById(R.id.tv_level_right);
        rl_cent = (RelativeLayout) findViewById(R.id.rl_cen);
        tv_score = (TextView) findViewById(R.id.tv_score);
        al_mine = (AbsoluteLayout) findViewById(R.id.al_card);
        timeshowthread = new TimeThread();
        timeshowthread.start();
        init_pic_map();
        init_al_main();
        init_south_card();
        init_north_card();
        init_mv_west();
        init_mv_east();
        init_west_card();
        init_east_card();
        init_mv_north();
        init_mv_south();
        init_center_card();
        init_rl_call();
        init_push();
        init_ready_push();
    }

    private void init_pic_map() {
        pic_map.clear();
        pic_map.put(0, getbitmap(R.drawable.poke_back));
        pic_map.put(1, getbitmap(R.drawable.lord));
        pic_map.put(21, getbitmap(R.drawable.p21));
        pic_map.put(22, getbitmap(R.drawable.p22));
        pic_map.put(23, getbitmap(R.drawable.p23));
        pic_map.put(24, getbitmap(R.drawable.p24));
        pic_map.put(31, getbitmap(R.drawable.p31));
        pic_map.put(32, getbitmap(R.drawable.p32));
        pic_map.put(33, getbitmap(R.drawable.p33));
        pic_map.put(34, getbitmap(R.drawable.p34));
        pic_map.put(41, getbitmap(R.drawable.p41));
        pic_map.put(42, getbitmap(R.drawable.p42));
        pic_map.put(43, getbitmap(R.drawable.p43));
        pic_map.put(44, getbitmap(R.drawable.p44));
        pic_map.put(51, getbitmap(R.drawable.p51));
        pic_map.put(52, getbitmap(R.drawable.p52));
        pic_map.put(53, getbitmap(R.drawable.p53));
        pic_map.put(54, getbitmap(R.drawable.p54));
        pic_map.put(61, getbitmap(R.drawable.p61));
        pic_map.put(62, getbitmap(R.drawable.p62));
        pic_map.put(63, getbitmap(R.drawable.p63));
        pic_map.put(64, getbitmap(R.drawable.p64));
        pic_map.put(71, getbitmap(R.drawable.p71));
        pic_map.put(72, getbitmap(R.drawable.p72));
        pic_map.put(73, getbitmap(R.drawable.p73));
        pic_map.put(74, getbitmap(R.drawable.p74));
        pic_map.put(81, getbitmap(R.drawable.p81));
        pic_map.put(82, getbitmap(R.drawable.p82));
        pic_map.put(83, getbitmap(R.drawable.p83));
        pic_map.put(84, getbitmap(R.drawable.p84));
        pic_map.put(91, getbitmap(R.drawable.p91));
        pic_map.put(92, getbitmap(R.drawable.p92));
        pic_map.put(93, getbitmap(R.drawable.p93));
        pic_map.put(94, getbitmap(R.drawable.p94));
        pic_map.put(101, getbitmap(R.drawable.p101));
        pic_map.put(102, getbitmap(R.drawable.p102));
        pic_map.put(103, getbitmap(R.drawable.p103));
        pic_map.put(104, getbitmap(R.drawable.p104));
        pic_map.put(111, getbitmap(R.drawable.p111));
        pic_map.put(112, getbitmap(R.drawable.p112));
        pic_map.put(113, getbitmap(R.drawable.p113));
        pic_map.put(114, getbitmap(R.drawable.p114));
        pic_map.put(121, getbitmap(R.drawable.p121));
        pic_map.put(122, getbitmap(R.drawable.p122));
        pic_map.put(123, getbitmap(R.drawable.p123));
        pic_map.put(124, getbitmap(R.drawable.p124));
        pic_map.put(131, getbitmap(R.drawable.p131));
        pic_map.put(132, getbitmap(R.drawable.p132));
        pic_map.put(133, getbitmap(R.drawable.p133));
        pic_map.put(134, getbitmap(R.drawable.p134));
        pic_map.put(141, getbitmap(R.drawable.p141));
        pic_map.put(142, getbitmap(R.drawable.p142));
        pic_map.put(143, getbitmap(R.drawable.p143));
        pic_map.put(144, getbitmap(R.drawable.p144));
        pic_map.put(151, getbitmap(R.drawable.p151));
        pic_map.put(161, getbitmap(R.drawable.p161));
        pic_map.put(200, getbitmap(R.drawable.no_lord));
        pic_map.put(201, getbitmap(R.drawable.have_lord));
        pic_map.put(202, getbitmap(R.drawable.push));
        pic_map.put(203, getbitmap(R.drawable.ready_push));
        pic_map.put(211, getbitmap(R.drawable.blackheart));
        pic_map.put(210, getbitmap(R.drawable.blackheart_no));
        pic_map.put(221, getbitmap(R.drawable.redheart));
        pic_map.put(220, getbitmap(R.drawable.redheart_no));
        pic_map.put(230, getbitmap(R.drawable.meihua_no));
        pic_map.put(231, getbitmap(R.drawable.meihua));
        pic_map.put(240, getbitmap(R.drawable.fangpian_no));
        pic_map.put(241, getbitmap(R.drawable.fangpian));
    }

    private void init_center_card() {
        al_center_card = new AbsoluteLayout(this);
        RelativeLayout.LayoutParams lp_al_center_card = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (ViewControl.hei_center_card + ViewControl.hei_center_card_move));
        lp_al_center_card.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp_al_center_card.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp_al_center_card.bottomMargin = (int) (ViewControl.hei * 0.0832);
        al_center_card.setLayoutParams(lp_al_center_card);
        rl_cent.addView(al_center_card);
    }

    private void init_al_main() {
        RelativeLayout.LayoutParams lp_al_main_card = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp_al_main_card.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp_al_main_card.addRule(RelativeLayout.RIGHT_OF, R.id.tv_mian_card);
        lp_al_main_card.leftMargin = (int) (ViewControl.wid * 0.01);
        mv_main_card = null;
    }

    private void init_mv_north() {
        RelativeLayout.LayoutParams lp_mv_north = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Bitmap b = getbitmap(R.drawable.player);
        mv_north = new MyView(this, b, 0, 0, ViewControl.hei_player_card, ViewControl.hei_player_card);
        mv_north.setId(ViewControl.id_mv_north);
        lp_mv_north.height = (int) (ViewControl.hei_player_card);
        lp_mv_north.width = (int) (ViewControl.hei_player_card);
        lp_mv_north.addRule(RelativeLayout.LEFT_OF, 104);
        lp_mv_north.addRule(RelativeLayout.ALIGN_TOP, 104);
        lp_mv_north.rightMargin = (int) (ViewControl.wid * 0.01);
        mv_north.setLayoutParams(lp_mv_north);
        rl_cent.addView(mv_north);
        tv_northname = new TextView(this);
        RelativeLayout.LayoutParams lp_tv_northname = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp_tv_northname.addRule(RelativeLayout.BELOW, 106);
        lp_tv_northname.addRule(RelativeLayout.ALIGN_LEFT, 106);
        tv_northname.setLayoutParams(lp_tv_northname);
        tv_northname.setText(PlayerList.getPlayerList().getPlayer(Status.pos_list.get(1)).name);
        tv_northname.setTextColor(Color.WHITE);
        rl_cent.addView(tv_northname);
    }

    private void init_mv_south() {
        RelativeLayout.LayoutParams lp_mv_south = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Bitmap b = getbitmap(R.drawable.player);
        mv_south = new MyView(this, b, 0, 0, ViewControl.hei_player_card, ViewControl.hei_player_card);
        mv_south.setId(ViewControl.id_mv_south);
        lp_mv_south.height = (int) (ViewControl.hei_player_card);
        lp_mv_south.width = (int) (ViewControl.hei_player_card);
        lp_mv_south.addRule(RelativeLayout.LEFT_OF, 103);
        lp_mv_south.addRule(RelativeLayout.ALIGN_TOP, 103);
        lp_mv_south.rightMargin = (int) (ViewControl.wid * 0.01);
        mv_south.setLayoutParams(lp_mv_south);
        rl_cent.addView(mv_south);
        tv_southname = new TextView(this);
        RelativeLayout.LayoutParams lp_tv_southname = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp_tv_southname.addRule(RelativeLayout.BELOW, 105);
        lp_tv_southname.addRule(RelativeLayout.ALIGN_LEFT, 105);
        tv_southname.setLayoutParams(lp_tv_southname);
        tv_southname.setText(PlayerList.getPlayerList().getPlayer(Status.pos_list.get(3)).name);
        tv_southname.setTextColor(Color.WHITE);
        rl_cent.addView(tv_southname);
    }

    private void init_east_card() {
        al_east = new AbsoluteLayout(this);
        RelativeLayout.LayoutParams lp_east = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (ViewControl.hei_player_card));
        lp_east.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp_east.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp_east.topMargin = (int) (ViewControl.hei * (0.14 + 0.05));
        lp_east.rightMargin = (int) (ViewControl.hei_player_card + ViewControl.wid * 0.01);
        al_east.setLayoutParams(lp_east);
        rl_cent.addView(al_east);
    }

    private void init_west_card() {
        al_west = new AbsoluteLayout(this);
        RelativeLayout.LayoutParams lp_west = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (ViewControl.hei_player_card));
        lp_west.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp_west.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp_west.topMargin = (int) (ViewControl.hei * (0.14 + 0.05));
        lp_west.leftMargin = (int) (ViewControl.hei_player_card + ViewControl.wid * 0.01);
        al_west.setLayoutParams(lp_west);
        rl_cent.addView(al_west);
    }

    private void init_mv_east() {
        RelativeLayout rl_east = new RelativeLayout(this);
        RelativeLayout.LayoutParams lp_rl_east = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp_rl_east.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp_rl_east.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp_rl_east.topMargin = (int) ((0.14 + 0.05) * ViewControl.hei);
        rl_east.setLayoutParams(lp_rl_east);
        RelativeLayout.LayoutParams lp_mv_east = new RelativeLayout.LayoutParams((int) (ViewControl.hei_player_card), (int) (ViewControl.hei_player_card));
        Bitmap b = getbitmap(R.drawable.player);
        mv_east = new MyView(this, b, 0, 0, ViewControl.hei_player_card, ViewControl.hei_player_card);
        mv_east.setId(ViewControl.id_mv_east);
        lp_mv_east.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mv_east.setLayoutParams(lp_mv_east);
        rl_east.addView(mv_east);
        tv_eastname = new TextView(this);
        tv_eastname.setTextColor(Color.WHITE);
        tv_eastname.setText(PlayerList.getPlayerList().getPlayer(Status.pos_list.get(4)).name);
        RelativeLayout.LayoutParams lp_tv_eastname = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp_tv_eastname.addRule(RelativeLayout.BELOW, 102);
        lp_tv_eastname.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        tv_eastname.setLayoutParams(lp_tv_eastname);
        rl_east.addView(tv_eastname);
        rl_cent.addView(rl_east);
    }

    private void init_south_card() {
        al_south = new AbsoluteLayout(this);
        al_south.setId(ViewControl.id_al_south);
        RelativeLayout.LayoutParams lp_south = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (ViewControl.hei_player_card));
        lp_south.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp_south.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp_south.bottomMargin = (int) (ViewControl.hei * 0.05);
        al_south.setLayoutParams(lp_south);
        rl_cent.addView(al_south);
    }

    private void init_north_card() {
        al_north = new AbsoluteLayout(this);
        al_north.setId(ViewControl.id_al_north);
        RelativeLayout.LayoutParams lp_north = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (ViewControl.hei_player_card));
        lp_north.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp_north.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        al_north.setLayoutParams(lp_north);
        rl_cent.addView(al_north);
    }

    private void init_mv_west() {
        RelativeLayout rl_west = new RelativeLayout(this);
        RelativeLayout.LayoutParams lp_rl_west = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp_rl_west.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp_rl_west.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp_rl_west.topMargin = (int) ((0.14 + 0.05) * ViewControl.hei);
        rl_west.setLayoutParams(lp_rl_west);
        RelativeLayout.LayoutParams lp_mv_west = new RelativeLayout.LayoutParams((int) (ViewControl.hei_player_card), (int) (ViewControl.hei_player_card));
        Bitmap b = getbitmap(R.drawable.player);
        mv_west = new MyView(this, b, 0, 0, ViewControl.hei_player_card, ViewControl.hei_player_card);
        mv_west.setId(ViewControl.id_mv_west);
        mv_west.setLayoutParams(lp_mv_west);
        rl_west.addView(mv_west);
        tv_westname = new TextView(this);
        tv_westname.setTextColor(Color.WHITE);
        tv_westname.setText(PlayerList.getPlayerList().getPlayer(Status.pos_list.get(2)).name);
        RelativeLayout.LayoutParams lp_tv_westname = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp_tv_westname.addRule(RelativeLayout.BELOW, 101);
        lp_tv_westname.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        tv_westname.setLayoutParams(lp_tv_westname);
        rl_west.addView(tv_westname);
        rl_cent.addView(rl_west);
    }

    private void init_push() {
        al_push = new AbsoluteLayout(this);
        RelativeLayout.LayoutParams lp_push = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp_push.addRule(RelativeLayout.ALIGN_BOTTOM, ViewControl.id_mv_south);
        lp_push.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp_push.rightMargin = (int) (ViewControl.wid * 0.1);
        al_push.setLayoutParams(lp_push);
        Bitmap b = getbitmap(R.drawable.push);
        bt_push = new MyView(this, 1, b, 0, 0, ViewControl.call_button_hei * 2.273, ViewControl.call_button_hei);
        bt_push.setOnClickListener(new PushClick());
        al_push.addView(bt_push);
        rl_cent.addView(al_push);
        al_push.setVisibility(View.GONE);
    }

    private void init_ready_push() {
        al_ready_push = new AbsoluteLayout(this);
        RelativeLayout.LayoutParams lp_ready_push = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp_ready_push.addRule(RelativeLayout.ALIGN_BOTTOM, ViewControl.id_mv_south);
        lp_ready_push.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp_ready_push.rightMargin = (int) (ViewControl.wid * 0.1);
        al_ready_push.setLayoutParams(lp_ready_push);
        MyView bt_ready_push = new MyView(this, 1, pic_map.get(203), 0, 0, ViewControl.call_button_hei * 2.273, ViewControl.call_button_hei);
        bt_ready_push.setOnClickListener(new ReadyPushOnClick());
        al_ready_push.addView(bt_ready_push);
        rl_cent.addView(al_ready_push);
        al_ready_push.setVisibility(View.GONE);
    }

    private void init_rl_call() {
        al_call = new AbsoluteLayout(this);
        RelativeLayout.LayoutParams lp_call = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp_call.addRule(RelativeLayout.ALIGN_BOTTOM, ViewControl.id_mv_south);
        lp_call.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp_call.rightMargin = (int) (ViewControl.wid * 0.02);
        al_call.setLayoutParams(lp_call);
        call_butto_map = new SparseArray<>();
        Bitmap b = getbitmap(R.drawable.blackheart_no);
        call_butto_map.put(1, new MyView(this, 1, b, 0, 0, ViewControl.call_button_hei, ViewControl.call_button_hei));
        b = getbitmap(R.drawable.redheart_no);
        call_butto_map.put(2, new MyView(this, 2, b, (int) (ViewControl.call_button_hei + ViewControl.wid * 0.01), 0, ViewControl.call_button_hei, ViewControl.call_button_hei));
        b = getbitmap(R.drawable.meihua_no);
        call_butto_map.put(3, new MyView(this, 3, b, (int) (ViewControl.call_button_hei + ViewControl.wid * 0.01) * 2, 0, ViewControl.call_button_hei, ViewControl.call_button_hei));
        b = getbitmap(R.drawable.fangpian_no);
        call_butto_map.put(4, new MyView(this, 4, b, (int) (ViewControl.call_button_hei + ViewControl.wid * 0.01) * 3, 0, ViewControl.call_button_hei, ViewControl.call_button_hei));
        b = getbitmap(R.drawable.no_lord);
        call_butto_map.put(0, new MyView(this, 0, b, (int) (ViewControl.call_button_hei + ViewControl.wid * 0.01) * 4, 0, ViewControl.call_button_hei, ViewControl.call_button_hei));
        al_call.addView(call_butto_map.get(1));
        al_call.addView(call_butto_map.get(2));
        al_call.addView(call_butto_map.get(3));
        al_call.addView(call_butto_map.get(4));
        al_call.addView(call_butto_map.get(0));
        MyOnClickButton bulisten = new MyOnClickButton();
        call_butto_map.get(0).setOnClickListener(bulisten);
        call_butto_map.get(1).setOnClickListener(bulisten);
        call_butto_map.get(2).setOnClickListener(bulisten);
        call_butto_map.get(3).setOnClickListener(bulisten);
        call_butto_map.get(4).setOnClickListener(bulisten);
        rl_cent.addView(al_call);
    }

    private void change_call_button(int i, int b) {
        Integer ig = 200 + i * 10 + b;
        call_butto_map.get(i).change_img(pic_map.get(ig));
        if (0 == b)
            call_butto_map.get(i).setEnabled(false);
        else if (1 == b)
            call_butto_map.get(i).setEnabled(true);

    }

    private void show_distribute(ArrayList<Integer> cardlist) {
        temp_show.clear();
        for (int i = 0; i <= count; ++i)
            temp_show.add(cardlist.get(i));
        Collections.sort(temp_show, PokeGameTools.cardcom);
        al_mine.removeAllViews();
        add_card_mine(temp_show, false);
    }

    private void show_single(ArrayList<Integer> al) {
        add_card_south(al);
    }

    private void update_call_button(ArrayList<Integer> temp_show) {
        for (int i = 0; i < 5; ++i)
            change_call_button(i, 0);
        if (0 == Status.call_player) {
            for (int i = 0; i < temp_show.size(); ++i) {
                int poke = temp_show.get(i);
                if (poke / 10 == Status.main_level)
                    change_call_button(poke % 10, 1);
            }
            return;
        }

        if (me.seq != Status.call_player && 0 == Status.insurance_player && temp_show.size() > 1) {
            for (int i = 1; i < temp_show.size(); ++i) {
                if (PokeGameTools.mainorno(temp_show.get(i))) {
                    if ((temp_show.get(i - 1).intValue() == temp_show.get(i).intValue())) {
                        if (PokeGameTools.comparecolor(temp_show.get(i), Status.recall_card)) {
                            if (temp_show.get(i) >= 151) {
                                daxiaowang = temp_show.get(i);
                                change_call_button(0, 1);
                            } else
                                change_call_button(temp_show.get(i) % 10, 1);
                        }
                    }
                }
            }
            return;
        }

        if (Status.call_player == me.seq && Status.re_call_player == 0 && Status.insurance_player == 0 && temp_show.size() > 1) {
            for (int i = 1; i < temp_show.size(); ++i) {
                if (temp_show.get(i) == Status.call_card && temp_show.get(i - 1).intValue() == temp_show.get(i).intValue())
                    change_call_button(temp_show.get(i) % 10, 1);
            }
        }
    }

    private void set_card_main() {
        if (!(mv_main_card == null))
            rl_cent.removeView(mv_main_card);
        RelativeLayout.LayoutParams lp_al_main_card = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp_al_main_card.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp_al_main_card.addRule(RelativeLayout.RIGHT_OF, R.id.tv_mian_card);
        lp_al_main_card.leftMargin = (int) (ViewControl.wid * 0.01);
        if (Status.main_color == 0)
            mv_main_card = new MyView(this, pic_map.get(161), 0, 0, ViewControl.hei * 0.1 / ViewControl.card_hei_div_wid, ViewControl.hei * 0.1);
        else
            mv_main_card = new MyView(this, pic_map.get(Status.main_level * 10 + Status.main_color), 0, 0, ViewControl.hei * 0.1 / ViewControl.card_hei_div_wid, ViewControl.hei * 0.1);
        mv_main_card.setId(ViewControl.id_mv_main_card);
        mv_main_card.setLayoutParams(lp_al_main_card);
        rl_cent.addView(mv_main_card);
    }

    private void add_card_mine(ArrayList<Integer> cardlist, boolean b) {
        al_mine.removeAllViews();
        for (int i = 0; i < cardlist.size(); ++i) {
            MyView v = new MyView(this, cardlist.get(i), pic_map.get(cardlist.get(i)), (ViewControl.wid_my_card * ViewControl.card_margin * i), ViewControl.hei_my_card_move, ViewControl.wid_my_card, ViewControl.hei_my_card);
            if (b)
                v.setOnClickListener(l);
            al_mine.addView(v);
        }
    }

    private void add_card_south(List<Integer> cardlist) {
        al_south.removeAllViews();
        for (int i = 0; i < cardlist.size(); ++i) {
            al_south.addView(new MyView(this, pic_map.get(cardlist.get(i)), ViewControl.wid_player_card * ViewControl.card_margin * i, 0, ViewControl.wid_player_card, ViewControl.hei_player_card));
        }
    }

    private void add_card_north(List<Integer> cardlist) {
        al_north.removeAllViews();
        for (int i = 0; i < cardlist.size(); ++i) {
            al_north.addView(new MyView(this, pic_map.get(cardlist.get(i)), ViewControl.wid_player_card * ViewControl.card_margin * i, 0, ViewControl.wid_player_card, ViewControl.hei_player_card));
        }
    }

    private void add_card_west(List<Integer> cardlist) {
        al_west.removeAllViews();
        for (int i = 0; i < cardlist.size(); ++i) {
            al_west.addView(new MyView(this, pic_map.get(cardlist.get(i)), ViewControl.wid_player_card * ViewControl.card_margin * i, 0, ViewControl.wid_player_card, ViewControl.hei_player_card));
        }
    }

    private void add_card_east(List<Integer> cardlist) {
        al_east.removeAllViews();
        for (int i = 0; i < cardlist.size(); ++i) {
            al_east.addView(new MyView(this, pic_map.get(cardlist.get(i)), ViewControl.wid_player_card * ViewControl.card_margin * i, 0, ViewControl.wid_player_card, ViewControl.hei_player_card));
        }
    }

    private void add_card_center(ArrayList<Integer> cardlist, boolean b) {
        al_center_card.removeAllViews();
        for (int i = 0; i < cardlist.size(); ++i) {
            MyView v = new MyView(this, cardlist.get(i), pic_map.get(cardlist.get(i)), ViewControl.wid_center_card * ViewControl.card_margin * i, ViewControl.hei_center_card_move, ViewControl.wid_center_card, ViewControl.hei_center_card);
            if (b)
                v.setOnClickListener(l);
            al_center_card.addView(v);
        }
    }

    class TimeThread extends Thread {

        private boolean flag = true;

        void stopthread() {
            this.flag = false;
            this.interrupt();
        }

        @Override
        public void run() {
            while (flag) {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;  //消息(一个整型值)
                    timeHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class DistributeThread extends Thread {

        DistributeThread(ArrayList<Integer> al) {
            distribute_card_list = al;
        }

        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(Setting.time);
                    Message msg = timeHandler.obtainMessage();
                    msg.what = 2;  //消息(一个整型值)
                    msg.sendToTarget();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (count <= distribute_card_list.size());
            Log.d("call", "----out-----");
            try {
                Thread.sleep(2000);
                Message msg = timeHandler.obtainMessage();
                msg.what = 3;  //停止叫主
                msg.sendToTarget();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    static class TimeHandler extends Handler{
        WeakReference<GameActivity> gameActivityWeakReference=null;
        GameActivity gameActivity=null;

        TimeHandler(GameActivity g){
            gameActivityWeakReference=new WeakReference<>(g);
            gameActivity=gameActivityWeakReference.get();
        }

        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    long sysTime = System.currentTimeMillis();
                    CharSequence sysTimeStr = DateFormat.format("hh:mm", sysTime);
                    gameActivity.tv_time.setText(sysTimeStr); //更新时间
                    break;
                case 2:
                    gameActivity.al_south.setVisibility(View.VISIBLE);
                    ArrayList<Integer> a = new ArrayList<>();
                    if (gameActivity.count < gameActivity.distribute_card_list.size()) {
                        a.clear();
                        Integer it = gameActivity.distribute_card_list.get(gameActivity.count);
                        a.add(it);
                        gameActivity.show_single(a);
                        gameActivity.show_distribute(gameActivity.distribute_card_list);
                    } else {
                        gameActivity.al_south.setVisibility(View.INVISIBLE);
                    }
                    gameActivity.count++;
                    gameActivity.update_call_button(gameActivity.temp_show);
                    break;
                case 3:
                    gameActivity.al_call.setVisibility(View.GONE);
                    gameActivity.al_east.removeAllViews();
                    gameActivity.al_north.removeAllViews();
                    gameActivity.al_south.removeAllViews();
                    gameActivity.al_west.removeAllViews();
                    gameActivity.playerservice.send_message_to_server(new TransmitUnit(Status.CALL_OVER, gameActivity.me.seq, 0, null));
                    break;
                default:
                    break;

            }
            super.handleMessage(msg);
        }
    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        log(tag, "GameActivity onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        log(tag, "GameActivity onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        log(tag, "GameActivity onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        log(tag, "GameActivity onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        log(tag, "GameActivity onDestroy");
        timeshowthread.stopthread();
        for (int i = 0, nsize = pic_map.size(); i < nsize; i++) {
            Bitmap b = pic_map.valueAt(i);
            if (b != null && !b.isRecycled()) {
                b.recycle();
            }
        }
        pic_map.clear();
        pic_map = null;
        com.xmu.supertractor.Tools.Tools.rceycleBitmapDrawable(bd);
        timeHandler.removeCallbacksAndMessages(null);
        timeHandler=null;
        System.gc();
        Intent stopIntent = new Intent(this, PlayerService.class);
        stopService(stopIntent);
        unbindService(connectionplayer);
        if (me.server_flag) {
            stopIntent = new Intent(this, DeskService.class);
            stopService(stopIntent);
            unbindService(connectiondesk);
        }
        setContentView(R.layout.acticity_null);
        super.onDestroy();
    }

    class MyOnClickButton implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v instanceof MyView) {
                MyView t = (MyView) v;
                for (int i = 0; i < 5; ++i)
                    change_call_button(i, 0);
                if (0 == Status.call_player)
                    playerservice.send_message_to_server(new TransmitUnit(Status.CALL, me.seq, 0, new Unit_Call_Info(Status.main_level * 10 + t.card, 1, me.seq)));
                else if (me.seq != Status.call_player && 0 == Status.insurance_player) {
                    if (t.card == 0 && daxiaowang != 0) {
                        playerservice.send_message_to_server(new TransmitUnit(Status.CALL, me.seq, 0, new Unit_Call_Info(daxiaowang, 2, me.seq)));
                    } else
                        playerservice.send_message_to_server(new TransmitUnit(Status.CALL, me.seq, 0, new Unit_Call_Info(Status.main_level * 10 + t.card, 2, me.seq)));
                } else if (Status.call_player == me.seq && Status.re_call_player == 0)
                    playerservice.send_message_to_server(new TransmitUnit(Status.CALL, me.seq, 0, new Unit_Call_Info(Status.main_level * 10 + t.card, 3, me.seq)));
            }
        }
    }

    class CardOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v instanceof MyView) {
                MyView t = (MyView) v;
                t.set_select_or_not();
            }
        }
    }

    class ReadyPushOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            al_ready_push.setEnabled(false);
            al_ready_push.setVisibility(View.GONE);
            TransmitUnit ui = new TransmitUnit(Status.NEW_TURN, me.seq, 0, null);
            playerservice.send_message_to_server(ui);
        }
    }

    class PushClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (Status.CALLING == Status.status) {
                if (8 == me.oc.len()) {
                    playerservice.send_message_to_server(new TransmitUnit(Status.PUSH_EIGHT_CARDS, me.seq, 0, me.oc.pokes));
                    for (int i = 0; i < 8; ++i)
                        me.hand_card.pokes.add(Status.eight_pokes.get(i));
                    for (Integer ig : me.oc.pokes)
                        me.hand_card.pokes.remove(ig);
                    PokeGameTools.cardsort(me.hand_card.pokes);
                    me.oc.clear();
                } else {
                    PokeGameTools.MyToast(gContext, "底牌数量不符");
                }
            } else if (Status.GAMING == Status.status) {
                if (Status.check_or_not) {
                    playerservice.send_message_to_server(new TransmitUnit(Status.PUSH, me.seq, 0, new Unit_Array_Info(me.oc.pokes)));
                    al_push.setVisibility(View.INVISIBLE);
                    bt_push.change_img(getbitmap(R.drawable.push_gray));
                    al_push.setEnabled(false);
                    for (Integer ig : me.oc.pokes)
                        me.hand_card.pokes.remove(ig);
                    me.oc.clear();
                    PokeGameTools.cardsort(me.hand_card.pokes);
                    add_card_mine(me.hand_card.pokes, true);
                } else {
                    PokeGameTools.MyToast(gContext, Status.error_str);
                }
            }
        }
    }

    class MyView extends View {
        int card = 0;
        Bitmap a;
        int x = 0, y = 0;
        int w = 0, h = 0;
        boolean flag = false;
        AbsoluteLayout.LayoutParams ap = new AbsoluteLayout.LayoutParams(0, 0, 0, 0);

        public void change_img(Bitmap b) {
            this.a = b;
            this.invalidate();
        }

        public MyView(Context context, int num, Bitmap b, double xt, double yt, double wt, double ht) {
            super(context);
            a = b;
            card = num;
            x = (int) xt;
            y = (int) yt;
            w = (int) wt;
            h = (int) ht;
            ap.x = (int) xt;
            ap.y = (int) yt;
            ap.width = (int) wt;
            ap.height = (int) ht;
            this.setLayoutParams(ap);
        }

        public MyView(Context context, Bitmap b, double xt, double yt, double wt, double ht) {
            super(context);
            a = b;
            x = (int) xt;
            y = (int) yt;
            w = (int) wt;
            h = (int) ht;
            ap.x = (int) xt;
            ap.y = (int) yt;
            ap.width = (int) wt;
            ap.height = (int) ht;
            this.setLayoutParams(ap);
        }

        public void set_select_or_not() {
            if (!flag) {
                ap.y = 0;
                Me.get_me().oc.pokes.add(card);
                flag = true;
            } else {
                Me.get_me().oc.pokes.remove(Integer.valueOf(card));
                ap.y = y;
                flag = false;
            }
            if (Status.status == Status.GAMING && Status.out_player == me.seq) {
                playerservice.check_out();
            } else if (Status.status == Status.CALLING)
                PokeGameTools.cardsort(Me.get_me().oc.pokes);
            Log.d(Status.GAMING == Status.status ? "game" : "call", PokeGameTools.array_to_String(Me.get_me().oc.pokes));
            this.setLayoutParams(ap);
            this.invalidate();
        }

        //在这里我们将测试canvas提供的绘制图形方法
        @Override
        protected void onDraw(Canvas canvas) {
            drawImage(canvas, a, 0, 0,
                    w, h, 100, 200);
        }

        public void drawImage(Canvas canvas, Bitmap blt, int x, int y,
                              int w, int h, int bx, int by) {
            Rect src = new Rect();// 图片 >>原矩形
            Rect dst = new Rect();// 屏幕 >>目标矩形

            src.left = bx;
            src.top = by;
            src.right = bx + w;
            src.bottom = by + h;

            dst.left = x;
            dst.top = y;
            dst.right = x + w;
            dst.bottom = y + h;
            // 画出指定的位图，位图将自动--》缩放/自动转换，以填补目标矩形
            // 这个方法的意思就像 将一个位图按照需求重画一遍，画后的位图就是我们需要的了
            canvas.drawBitmap(blt, null, dst, null);
        }
    }
}

