package com.xmu.supertractor.parameter;

public class ViewControl {
	public static int hei;
	public static int wid;
	public static double hei_my_card;
	public static double hei_my_card_move;
	public final static double card_hei_div_wid =1.3;
	public static double wid_my_card;
	public static double hei_player_card;
	public static double wid_player_card;
	public static double hei_center_card;
	public static double hei_center_card_move;
	public static double wid_center_card;
	public final static double card_margin =0.28;
	public static double call_button_hei;

	public static final @android.support.annotation.IdRes int id_mv_south=105;
	public static final @android.support.annotation.IdRes int id_mv_north=106;
	public static final @android.support.annotation.IdRes int id_mv_east=102;
	public static final @android.support.annotation.IdRes int id_al_south=103;
	public static final @android.support.annotation.IdRes int id_al_north=104;
	public static final @android.support.annotation.IdRes int id_mv_west=101;
	public static final @android.support.annotation.IdRes int id_mv_main_card=66;


	public static void compute(int ihei,int iwid){
		hei=ihei;
		wid=iwid;
		hei_my_card=0.27*hei;
		hei_my_card_move=0.07*hei;
		wid_my_card=(hei_my_card)/(card_hei_div_wid);
		hei_player_card=0.14*hei;
		hei_center_card=0.20*hei;
		hei_center_card_move=0.052*hei;
		wid_center_card=hei_center_card/card_hei_div_wid;
		wid_player_card=hei_player_card/card_hei_div_wid;
		call_button_hei=hei*0.09;
	}
}
