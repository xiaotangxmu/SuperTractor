package com.xmu.supertractor.pokegame;

import com.xmu.supertractor.card.Base_Card;
import com.xmu.supertractor.card.Hand_Card;
import com.xmu.supertractor.card.Out_Card;
import com.xmu.supertractor.card.Pair;
import com.xmu.supertractor.card.Single;
import com.xmu.supertractor.card.Throw;
import com.xmu.supertractor.card.Tie;
import com.xmu.supertractor.card.Tractor;
import com.xmu.supertractor.card.TypeDefine;

public class AnalyzeOutCard {

    public static Out_Card analyze_out_card(Out_Card oc) {
        PokeGameTools.cardsort(oc.pokes);
        oc.color = analyze_card_color(oc);
        if (oc.color == TypeDefine.tie) {
            return new Tie(oc);
        } else
            return analyze_card_type(oc);
    }

    private static Out_Card analyze_card_type(Out_Card oc) {
        if (is_single(oc)) {
            return new Single(oc);
        }
        if (is_pair(oc)) {
            return new Pair(oc);
        }
        if (is_tractor(oc)) {
            return new Tractor(oc);
        }
        return new Throw(oc);
    }

    private static boolean is_tractor(Out_Card oc) {
        if ((oc.len() > 2) && (oc.len() % 2 == 0) && (all_pair_or_not(oc))) {
            for (int i = 0; i < oc.len() - 3; i += 2) {
                if (PokeGameTools.getvalue(oc.pokes.get(i)) + 1 != PokeGameTools.getvalue(oc.pokes.get(i + 2)))
                    return false;
            }
            return true;
        } else
            return false;
    }

    private static boolean is_pair(Out_Card oc) {
        return (2 == oc.len()) && (oc.pokes.get(0).intValue() == oc.pokes.get(1).intValue());
    }

    private static boolean is_single(Out_Card oc) {
        return (1 == oc.len());
    }

    static boolean exist_len_tractor_or_not(Hand_Card hc, int color, int len) {
        boolean flag = false;
        for (Tractor t : hc.tractor_map.get(color)) {
            if (t.len == len) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    static boolean all_pair_or_not(Base_Card bd) {
        return pair_num(bd) * 2 == bd.len();
    }

    static int pair_num(Base_Card bd) {
        int res = 0;
        for (int i = 0; i < bd.pokes.size() - 1; i++) {
            if (bd.pokes.get(i).intValue() == bd.pokes.get(i + 1).intValue()) {
                ++res;
                ++i;
            }
        }
        return res;
    }

    private static int analyze_card_color(Out_Card bd) {
        boolean flag = true;
        int first_color = PokeGameTools.getcolor(bd.pokes.get(0));
        for (Integer i : bd.pokes) {
            if (PokeGameTools.getcolor(i) != first_color) {
                flag = false;
                break;
            }
        }
        return flag ? first_color : TypeDefine.tie;
    }
}
