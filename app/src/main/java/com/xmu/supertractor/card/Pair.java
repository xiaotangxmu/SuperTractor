package com.xmu.supertractor.card;


import com.xmu.supertractor.pokegame.PokeGameTools;

public class Pair extends Out_Card{
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

	

	public Pair(Integer p){
		type=TypeDefine.pair;
		pokes.add(p);
		pokes.add(p);
		value= PokeGameTools.getvalue(p);
		color= PokeGameTools.getcolor(p);
	}
	public Pair(Out_Card oc){
		type=TypeDefine.pair;
		pokes.addAll(oc.pokes);
		value= PokeGameTools.getvalue(oc.pokes.get(0).intValue());
		color=oc.color;
	}
}
