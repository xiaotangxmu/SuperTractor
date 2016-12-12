package com.xmu.supertractor.player;


import com.xmu.supertractor.card.Hand_Card;

public class Player {

	public String name; // 玩家昵称
	public int seq; // 玩家所处位置，<1,2,3>
	private boolean landlord_flag; // 地主标记
	private boolean npc_flag; // 托管标记
	public Hand_Card hand_card;

	Player(String name) {
		this.name=name;
		this.seq=0;
		this.hand_card=new Hand_Card();
		this.landlord_flag=false;
		this.npc_flag=false;
	}
	Player(int seq, String name) {
		this.name=name;
		this.seq=seq;
		this.hand_card=new Hand_Card();
		this.landlord_flag=false;
		this.npc_flag=false;
	}
	@Override
	public String toString() {
		return "Player [name=" + name +  "seq=" + seq + ", landlord_flag=" + landlord_flag
				+ ", npc_flag=" + npc_flag + ", hand_card=" + hand_card + "]";
	}
	
}
