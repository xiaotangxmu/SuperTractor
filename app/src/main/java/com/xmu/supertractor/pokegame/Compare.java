package com.xmu.supertractor.pokegame;

import com.xmu.supertractor.card.Out_Card;
import com.xmu.supertractor.card.Throw;
import com.xmu.supertractor.card.TypeDefine;


public class Compare {
    public static boolean Compare_Card(Out_Card oc, Out_Card pre) {
        if (oc.type == TypeDefine.tie)
            return false;
        else {
            if (pre.kill) {
                if (!oc.kill)
                    return false;
                else {
                    if (pre.type != TypeDefine.shuai)
                        return PokeGameTools.getvalue(oc.pokes.get(0)) > PokeGameTools.getvalue(pre.pokes.get(0));
                    else {
                        Throw oct;
                        if (oc.type == TypeDefine.shuai)
                            oct = (Throw) oc;
                        else
                            oct = new Throw(oc);
                        Throw pret = (Throw) pre;
                        if (!pret.tractor_list.isEmpty()) {
                            return oct.tractor_list.get(0).value > pret.tractor_list.get(0).value;
                        } else {
                            if (!pret.pair_list.isEmpty()) {
                                return oct.pair_list.get(0).value > pret.pair_list.get(0).value;
                            } else {
                                return oct.single_list.get(0).value > pret.single_list.get(0).value;
                            }
                        }
                    }
                }
            } else {
                if (oc.kill)
                    return true;
                else {
                    //noinspection SimplifiableIfStatement
                    if (oc.color != pre.color || oc.type != pre.type)
                        return false;
                    else {
                        return pre.type != TypeDefine.shuai && PokeGameTools.getvalue(oc.pokes.get(0)) > PokeGameTools.getvalue(pre.pokes.get(0));
                    }
                }
            }
        }
    }
}


