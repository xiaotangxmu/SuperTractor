package com.xmu.supertractor.card;


import com.xmu.supertractor.pokegame.PokeGameTools;

import java.util.Objects;

public class Pair extends Out_Card {
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


    public Pair(Integer p) {
        type = TypeDefine.pair;
        pokes.add(p);
        pokes.add(p);
        value = PokeGameTools.getvalue(p);
        color = PokeGameTools.getcolor(p);
    }

    public Pair(Out_Card oc) {
        type = TypeDefine.pair;
        pokes.addAll(oc.pokes);
        value = PokeGameTools.getvalue(oc.pokes.get(0).intValue());
        color = oc.color;
    }

    @Override
    public boolean equals(Object o) {
        boolean flag = o instanceof Pair;
        if(!flag)
            return false;
        Pair p=(Pair)o;
        return this.pokes.get(0)== p.pokes.get(0);
    }
}
