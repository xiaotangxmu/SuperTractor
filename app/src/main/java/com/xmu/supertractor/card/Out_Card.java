package com.xmu.supertractor.card;




public class Out_Card extends Base_Card {
//	 public List<Integer> pokes=new ArrayList<Integer>(); // 牌面数组
//	 public void clear(){
//	 this.pokes.clear();
//	 }
//	 public int len(){
//	 return pokes.size();
//	 }

	public int color;
	public int type;
	public boolean kill;
	public int value;

	public Out_Card() {
		kill=false;
	}


}
