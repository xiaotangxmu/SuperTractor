package com.xmu.supertractor.card;

import java.io.Serializable;
import java.util.ArrayList;

public class Base_Card implements Serializable{
	public ArrayList<Integer> pokes=new ArrayList<>(); // 牌面数组
	public void clear(){
		this.pokes.clear();
	}
	public int len(){
		return pokes.size();
	}


	Base_Card() {

	}
}



