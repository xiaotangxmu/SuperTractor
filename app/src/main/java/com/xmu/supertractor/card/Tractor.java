package com.xmu.supertractor.card;

import com.xmu.supertractor.pokegame.PokeGameTools;

import java.util.List;


public class Tractor extends Out_Card {
	
//	 public List<Integer> pokes=new ArrayList<Integer>(); // 牌面数组
//	 public void clear(){
//	 this.pokes.clear();
//	 }
//	 public int len(){
//	 return pokes.size();
//	 }
//
//	public int color;
//	public int type;
	public int len;


	public Tractor(Out_Card oc){
		type=TypeDefine.tractor;
		pokes.addAll(oc.pokes);
		len=pokes.size()/2;
		color=oc.color;
		value= PokeGameTools.getvalue(pokes.get(0));
	}	
	
	public Tractor(List<Pair> l) {
		type = TypeDefine.tractor;
		len=l.size();
		for(Pair p:l){
			this.pokes.addAll(p.pokes);
		}
		value= PokeGameTools.getvalue(pokes.get(0));
		color=l.get(0).color;
	}
	
}
