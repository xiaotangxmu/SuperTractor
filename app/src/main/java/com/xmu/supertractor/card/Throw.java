package com.xmu.supertractor.card;

import com.xmu.supertractor.pokegame.PokeGameTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Throw extends Out_Card {
    // public List<Integer> pokes=new ArrayList<Integer>(); // 牌面数组
    // public void clear(){
    // this.pokes.clear();
    // }
    // public int len(){
    // return pokes.size();
    // }
    //
    // public int color;
    // public int type;

    public ArrayList<Single> single_list = new ArrayList<>();
    public ArrayList<Pair> pair_list = new ArrayList<>();
    public ArrayList<Tractor> tractor_list = new ArrayList<>();
    public ArrayList<Integer> tractor_len_list = new ArrayList<>();
    public int pair_num = 0;


    public Throw(Out_Card oc) {
        pokes.addAll(oc.pokes);
        type = TypeDefine.shuai;
        color = oc.color;
        for (int i = 0; i < (pokes.size() - 1); ++i) {
            if (pokes.get(i).intValue() == pokes.get(i + 1).intValue()) {
                pair_list.add(new Pair(pokes.get(i)));
                ++i;
            } else {
                single_list.add(new Single(pokes.get(i)));
                if(pokes.size() - 2==i)
                    single_list.add(new Single(pokes.get(i+1)));
            }
        }
        if (pair_list.size() >= 2) {
            int i = 0, j;
            while (i < (pair_list.size() - 1)) {
                for (j = i; j < (pair_list.size() - 1); ++j) {
                    if (pair_list.get(j).value + 1 != pair_list.get(j + 1).value)
                        break;
                }
                if (i != j) {
                    List<Pair> temp = new ArrayList<>();
                    temp.clear();
                    for (int k = i; k <= j; ++k) {
                        temp.add(pair_list.get(k));
                        pair_list.get(k).value = -1;
                    }
                    tractor_list.add(new Tractor(temp));
                }
                i = j + 1;
            }
            if (!tractor_list.isEmpty()) {
                Collections.sort(tractor_list, PokeGameTools.tractor_down_com);
                for (Tractor t : tractor_list) {
                    pair_num += t.len;
                    tractor_len_list.add(t.len);
                }
            }
            i = 0;
            while (i < pair_list.size()) {
                if (-1 == pair_list.get(i).value)
                    pair_list.remove(i);
                else
                    ++i;
            }
            pair_num += pair_list.size();
        }
    }

//    public String to_string() {
//        StringBuilder sb = new StringBuilder();
//        String newLine = System.getProperty("line.separator");
//        sb.append("type:").append(PokeGameTools.type_to_string(type)).append(newLine).append("color:").append(color).append(newLine);
//        sb.append(PokeGameTools.array_to_String(pokes)).append(newLine);
//        sb.append("Single: ");
//        for (Single s : single_list)
//            sb.append(s.pokes.get(0)).append(" ");
//        sb.append(newLine);
//        sb.append("Pair: ");
//        for (Pair p : pair_list)
//            sb.append(p.pokes.get(0) + "-" + p.pokes.get(1) + " ");
//        sb.append(newLine);
//        sb.append("Tractor: ");
//        for (Tractor t : tractor_list) {
//            sb.append(PokeGameTools.array_to_String(t.pokes));
//            sb.append(newLine);
//        }
//        sb.append("len----map" + newLine);
//        for (Integer t : tractor_len_list)
//            sb.append(t.intValue() + " ");
//        sb.append(newLine + "pair_num:" + pair_num + newLine);
//        return sb.toString();
//    }

}
