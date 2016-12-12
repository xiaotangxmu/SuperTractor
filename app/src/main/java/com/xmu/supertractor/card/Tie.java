package com.xmu.supertractor.card;

public class Tie extends Out_Card {
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
	public Tie(Out_Card oc){
		pokes.addAll(oc.pokes);
		color=TypeDefine.tie;
		type=TypeDefine.tie;
	}
}
