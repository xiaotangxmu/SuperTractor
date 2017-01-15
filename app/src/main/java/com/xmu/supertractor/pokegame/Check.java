package com.xmu.supertractor.pokegame;


import java.util.ArrayList;
import java.util.Collections;

import com.xmu.supertractor.card.Hand_Card;
import com.xmu.supertractor.card.Out_Card;
import com.xmu.supertractor.card.Pair;
import com.xmu.supertractor.card.Single;
import com.xmu.supertractor.card.Throw;
import com.xmu.supertractor.card.Tractor;
import com.xmu.supertractor.card.TypeDefine;
import com.xmu.supertractor.desk.Desk;
import com.xmu.supertractor.parameter.Status;
import com.xmu.supertractor.player.Me;

import static com.xmu.supertractor.Tools.PrintLog.log;

public class Check {

    public static String tag="Check";
    public static String check_out_cards(Out_Card first, Out_Card oc, Hand_Card hc) {
        if (Me.get_me().seq != Status.out_player)
            return "还没轮到您";
        if (oc.pokes.isEmpty())
            return "不能为空";
        if (Status.first_out_or_not) {
            if (oc.color != TypeDefine.tie)
                return "t";
            else
                return "请选择同种花色牌";
        }
        if (oc.len() != first.len())
            return "出牌数量不符";
        int first_color = first.color;
        int oc_color = oc.color;
        int first_color_num = first.len();
        int oc_color_num = PokeGameTools.get_color_num(oc, first_color);
        int hc_color_num = PokeGameTools.get_color_num(hc, first_color);
        int oc_pair_num = AnalyzeOutCard.pair_num(oc);

        // 小于是否全出

        if (hc_color_num <= first_color_num) {
            if (oc_color_num == hc_color_num)
                return "t";
            else
                return "仍有可出的花色";
        }

        // 大于是否都出
        if (oc_color_num != first_color_num)
            return "仍有可出的花色";

        int first_type = first.type;
        int oc_type = oc.type;
        if (first_type == TypeDefine.single)
            return "t";
        if (first_type == TypeDefine.pair) {
            if (oc_type == TypeDefine.pair)
                return "t";
            else {
                if (hc.pair_map.get(oc_color).isEmpty())
                    return "t";
                else
                    return "仍有可出的对牌";
            }
        }
        int hc_pair_num = hc.pair_map.get(oc_color).size();
        if (first_type == TypeDefine.tractor) {
            Tractor t = (Tractor) first;
            if (hc_pair_num > t.len) {
                if (oc.type == TypeDefine.tractor)
                    return "t";
                else {
                    if (AnalyzeOutCard.exist_len_tractor_or_not(hc, oc_color, t.len))
                        return "仍有可出的拖拉机";
                    else if (AnalyzeOutCard.all_pair_or_not(oc))
                        return "t";
                    else
                        return "仍有可出的对牌";
                }
            } else if (hc_pair_num == oc_pair_num)
                return "t";
            else
                return "仍有可出的对牌";
        }
        if (first_type == TypeDefine.shuai) {
            Throw t = (Throw) first;
            ArrayList<Integer> hc_tractor_len_list = hc.tractor_len_map.get(oc_color);
            if (hc_pair_num <= (t.pair_num))
                if (hc_pair_num == oc_pair_num)
                    return "t";
                else
                    return "仍有可出的对牌";
            else {
                if (t.pair_num != 0) {
                    if (oc.type != TypeDefine.shuai && oc.type != TypeDefine.tractor)
                        return "牌型不匹配";
                    Throw oct = null;
                    if (oc.type == TypeDefine.shuai)
                        oct = (Throw) oc;
                    if (oc.type == TypeDefine.tractor)
                        oct = new Throw(oc);
//                    oc = oct;
                    if (!t.tractor_list.isEmpty()) {
                        ArrayList<Integer> needed_tractor_len = new ArrayList<>();
                        compute_needed_tractor(t.tractor_len_list, hc_tractor_len_list, needed_tractor_len);
                        ArrayList<Integer> needed_tractor_len2 = new ArrayList<>();
                        compute_needed_tractor(needed_tractor_len, oct.tractor_len_list, needed_tractor_len2);
                        if (needed_tractor_len.size() != needed_tractor_len2.size())
                            return "仍有可出的拖拉机";
                        for (int i = 0; i < needed_tractor_len.size(); ++i) {
                            //noinspection NumberEquality
                            if (needed_tractor_len.get(i) != needed_tractor_len2.get(i))
                                return "拖拉机不匹配";
                        }
                        int sum_needed_tractor_len = 0;
                        for (Integer a : t.tractor_len_list)
                            sum_needed_tractor_len += a;
                        int sum_needed_tractor_len2 = 0;
                        for (Integer a : needed_tractor_len)
                            sum_needed_tractor_len2 += a;
                        if (sum_needed_tractor_len == sum_needed_tractor_len2) {
                            if (t.pair_list.size() > (oct.pair_list.size() + oc_pair_num - sum_needed_tractor_len2))
                                return "对牌数不匹配";
                            return "t";
                        } else {
                            int extra_pair_needed_num = sum_needed_tractor_len - sum_needed_tractor_len2;
                            int needed_pair_num = extra_pair_needed_num + t.pair_list.size();
                            if (needed_pair_num != oct.pair_list.size())
                                return "对牌数量不足";
                            return "t";
                        }
                    } else {
                        if (oct.pair_num >= t.pair_num)
                            return "t";
                        else
                            return "对牌数不足";
                    }
                } else {
                    return "t";
                }
            }

        }
        return "t";
    }

    public static boolean kill_or_not(Out_Card first, Out_Card oc, int pushplayer) {
        if (Status.first_out_player == pushplayer)
            return false;
        if (first.color != TypeDefine.main && oc.color == TypeDefine.main) {
            if (first.type != TypeDefine.shuai) {
                return first.type == oc.type;
            } else {
                Throw f = (Throw) first;
                if (f.pair_num != 0) {
                    if (oc.type != TypeDefine.shuai && oc.type != TypeDefine.tractor)
                        return false;
                    else {
                        Throw o = null;
                        if (oc.type == TypeDefine.shuai)
                            o = (Throw) oc;
                        if (oc.type == TypeDefine.tractor)
                            o = new Throw(oc);
                        if (o.pair_num < f.pair_num)
                            return false;
                        if (!f.tractor_list.isEmpty()) {
                            ArrayList<Integer> needed_tractor_len = new ArrayList<>();
                            compute_needed_tractor(f.tractor_len_list, o.tractor_len_list, needed_tractor_len);
                            if (needed_tractor_len.size() != f.tractor_len_list.size())
                                return false;
                            for (int i = 0; i < f.tractor_len_list.size(); ++i) {
                                //noinspection NumberEquality
                                if (needed_tractor_len.get(i) != f.tractor_len_list.get(i))
                                    return false;
                            }
                        }
                        return o.pair_num > f.pair_num;
                    }
                } else {
                    return true;
                }
            }
        } else
            return false;
    }

    public static boolean checkthrow(Out_Card out_card, int recsour, ArrayList<Integer> al) {
        Desk desk= Desk.dk_getInstance();
        boolean valid_shuai = true;
        Hand_Card First_player_card = desk.deskplayer_map.get(PokeGameTools.next_player(recsour, 1)).hc;
        Hand_Card Second_player_card = desk.deskplayer_map.get(PokeGameTools.next_player(recsour, 2)).hc;
        Hand_Card Third_player_card = desk.deskplayer_map.get(PokeGameTools.next_player(recsour, 3)).hc;
        int color = out_card.color;
        ArrayList<Hand_Card> hc_list = new ArrayList<>();
        hc_list.add(First_player_card);
        hc_list.add(Second_player_card);
        hc_list.add(Third_player_card);
        al.clear();
        Hand_Card throw_hand = new Hand_Card();
        throw_hand.pokes.addAll(out_card.pokes);
        AnalyzeHandPokes.analyze_throw_independence(throw_hand, color);
        ArrayList<Single> min_single_arr = throw_hand.single_map.get(color);
        if (!min_single_arr.isEmpty()) {
            int i = 1;
            for (Hand_Card hd : hc_list) {
                ArrayList<Single> als = hd.single_map.get(color);
                if (als.isEmpty()) {
                    ++i;
                    continue;
                }
                Single min_single = min_single_arr.get(0);
                for (Single s : als)
                    if (min_single.value < s.value) {
                        valid_shuai = false;
                        log(tag, "Throw fail!!!!!    player:" + Status.out_player + " Single:" + min_single.pokes.get(0) + " < " + desk.getMember(PokeGameTools.next_player(recsour, i)).name + " Single:" + s.pokes);
                        al.addAll(min_single.pokes);
                        break;
                    }
                if (!valid_shuai)
                    break;
                i++;
            }
        }
        if (valid_shuai) {
            ArrayList<Pair> min_pair_arr = throw_hand.pair_map.get(color);
            if (!min_pair_arr.isEmpty()) {
                Pair min_pair = min_pair_arr.get(0);
                int i = 1;
                for (Hand_Card hd : hc_list) {
                    ArrayList<Pair> alp = hd.pair_map.get(color);
                    if (alp.isEmpty()) {
                        ++i;
                        continue;
                    }
                    for (Pair p : alp)
                        if (min_pair.value < p.value) {
                            log(tag, "Throw fail!!!!!    player:" + Status.out_player + " Pair:" + min_pair.pokes.get(0) + " < " + desk.getMember(PokeGameTools.next_player(recsour, i)).name + " Pair:" + p.pokes);
                            valid_shuai = false;
                            al.addAll(min_pair.pokes);
                            break;
                        }
                    if (!valid_shuai)
                        break;
                    ++i;
                }
            }
        }
        if (valid_shuai) {
            ArrayList<Tractor> independent_throw_tractor_arr = throw_hand.independent_tractor_map.get(color);
            if (!independent_throw_tractor_arr.isEmpty()) {
                int i = 1;
                Collections.sort(independent_throw_tractor_arr, PokeGameTools.tractor_up_com);
                for (Hand_Card hd : hc_list) {
                    ArrayList<Tractor> throw_tractor_arr = hd.tractor_map.get(color);
                    if (throw_tractor_arr.isEmpty()) {
                        ++i;
                        continue;
                    }
                    for (Tractor throw_t : independent_throw_tractor_arr) {
                        for (Tractor player_t : throw_tractor_arr) {
                            if (throw_t.len == player_t.len && throw_t.value < player_t.value) {
                                log(tag, "Throw fail!!!!!    player:" + Status.out_player + " Tractor:" + throw_t.pokes.get(0) + " < " + desk.getMember(PokeGameTools.next_player(recsour, i)).name + " Tractor:" + player_t.pokes);
                                valid_shuai = false;
                                al.addAll(throw_t.pokes);
                                break;
                            }
                        }
                    }
                    if (!valid_shuai)
                        break;
                    ++i;
                }
            }
        }
        return valid_shuai;
    }

    private static void compute_needed_tractor(ArrayList<Integer> sour, ArrayList<Integer> sublist,
                                               ArrayList<Integer> res) {
        ArrayList<Integer> sourtemp = new ArrayList<>();
        sourtemp.addAll(sour);
        ArrayList<Integer> temp = new ArrayList<>();
        ArrayList<Tractor_map> midres = new ArrayList<>();
        Tractor_map best = new Tractor_map();
        for (int i = 0; i < sublist.size(); ++i) {
            temp.clear();
            midres.clear();
            recourtion(sourtemp, 0, temp, sublist.get(i), midres);
            int minest = sublist.get(i);
            best.al.clear();
            for (Tractor_map tm : midres) {
                if (tm.sub < minest) {
                    minest = tm.sub;
                    best.sub = tm.sub;
                    best.al.clear();
                    best.al.addAll(tm.al);
                } else if (tm.sub == minest) {
                    if (best.al.size() > tm.al.size()) {
                        best.sub = tm.sub;
                        best.al.clear();
                        best.al.addAll(tm.al);
                    }
                }
            }
            if (!best.al.isEmpty()) {
                for (Integer ig : best.al) {
                    sourtemp.remove(sourtemp.indexOf(ig));
                }
                System.out.println();
                res.addAll(best.al);
            }
        }
        Collections.sort(res, PokeGameTools.down_com);
    }

    private static void recourtion(ArrayList<Integer> sour, int i, ArrayList<Integer> temp, Integer com,
                                   ArrayList<Tractor_map> midres) {
        if (i < sour.size()) {
            temp.add(sour.get(i));
            recourtion(sour, i + 1, temp, com, midres);
            temp.remove(temp.size() - 1);
            recourtion(sour, i + 1, temp, com, midres);
        } else {
            int sum = 0;
            for (Integer a : temp) {
                sum += a;
            }
            if (sum != 0 && (com - sum >= 0)) {
                Tractor_map tm = new Tractor_map(com - sum, temp);
                midres.add(tm);
            }
        }
    }
}

class Tractor_map {
    int sub = 0;
    ArrayList<Integer> al = new ArrayList<>();

    Tractor_map(int subb, ArrayList<Integer> all) {
        al.addAll(all);
        sub = subb;
    }

    Tractor_map() {
    }
}
