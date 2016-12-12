package com.xmu.supertractor.card;

import com.xmu.supertractor.pokegame.PokeGameTools;


public class Single extends Out_Card {

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
	


	public Single(Integer p){
		type=TypeDefine.single;
		pokes.add(p);
		value= PokeGameTools.getvalue(p);
		color= PokeGameTools.getcolor(p);
	}
	
	public Single(Out_Card oc){
		type=TypeDefine.single;
		pokes.addAll(oc.pokes);
		value= PokeGameTools.getvalue(oc.pokes.get(0));
		color=oc.color;		
	}
}
