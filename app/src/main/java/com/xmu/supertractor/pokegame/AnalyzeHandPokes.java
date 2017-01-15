package com.xmu.supertractor.pokegame;


import com.xmu.supertractor.card.Hand_Card;
import com.xmu.supertractor.card.Pair;
import com.xmu.supertractor.card.Single;
import com.xmu.supertractor.card.Tractor;

import java.util.ArrayList;
import java.util.Collections;

import static com.xmu.supertractor.Tools.PrintLog.log;


public class AnalyzeHandPokes {

    public static void analyze_hand_pokes(Hand_Card hc) {
        PokeGameTools.cardsort(hc.pokes);
        analyze_single(hc);
        analyze_pair(hc);
        analyze_tractor(hc);
    }

    private static void analyze_single(Hand_Card hc) {
        for (int i = 1; i <= 5; ++i)
            hc.single_map.get(i).clear();
        for (Integer i : hc.pokes) {
            Single s = new Single(i);
            hc.single_map.get(s.color).add(s);
        }

    }

    static void analyze_throw_independence(Hand_Card hc, int color) {
        analyze_hand_pokes(hc);
        int min_single_index;
        boolean flag = false;
        Single minsingle = null;
        ArrayList<Single> single_color = hc.single_map.get(color);
        if (!single_color.isEmpty()) {
            for (min_single_index = 0; min_single_index < single_color.size() - 1; min_single_index = min_single_index + 2) {
                boolean res = single_color.get(min_single_index).pokes.get(0).intValue() == single_color.get(min_single_index + 1).pokes.get(0).intValue();
                if (!res) {
                    flag = true;
                    minsingle = new Single(single_color.get(min_single_index).pokes.get(0));
                    break;
                }
            }
            single_color.clear();
            if (flag) {
                single_color.add(minsingle);
            }
        }
        if (!hc.independent_tractor_map.get(color).isEmpty()) {
            for (Tractor t : hc.independent_tractor_map.get(color)) {
                for (int i = 0; i < t.len; ++i) {
                    hc.pair_map.get(color).remove(new Pair(t.pokes.get(2 * i)));
                }
            }
        }
        String tag = "AnalyzeHandPokes";
        log(tag, "------min single-------");
        for (Single s : hc.single_map.get(color))
            log(tag, s.pokes.get(0) + " ");
        log(tag, "------independence pair------");
        for (Pair p : hc.pair_map.get(color))
            log(tag, p.pokes.get(0) + " ");
        log(tag, "------independence tractor------");
        for (Tractor t : hc.independent_tractor_map.get(color))
            log(tag, PokeGameTools.array_to_String(t.pokes) + " ");
    }

    private static void analyze_pair(Hand_Card hc) {
        for (int i = 1; i <= 5; ++i)
            hc.pair_map.get(i).clear();
        for (int i = 0; i < (hc.pokes.size() - 1); ++i) {
            if (hc.pokes.get(i).intValue() == hc.pokes.get(i + 1).intValue()) {
                Pair p = new Pair(hc.pokes.get(i));
                hc.pair_map.get(p.color).add(p);
            }
        }
    }

    private static void analyze_tractor(Hand_Card hc) {
        for (int i = 1; i <= 5; ++i) {
            hc.tractor_map.get(i).clear();
            hc.independent_tractor_map.get(i).clear();
        }

        for (int x = 0; x < hc.pair_map.size(); x++) {
            int color = hc.pair_map.keyAt(x);
            ArrayList<Pair> as = hc.pair_map.get(color);
            int i, j;
            ArrayList<Pair> temp = new ArrayList<>();
            ArrayList<Tractor> color_tractor_list = hc.tractor_map.get(color);
            if (as.size() <= 1)
                continue;
            ArrayList<Pair> astemp = new ArrayList<>();
            astemp.addAll(as);
            for (i = 0; i < (astemp.size() - 1); ++i) {
                if (astemp.get(i).value == astemp.get(i + 1).value) {
                    for (j = i + 1; (j < astemp.size()) && (astemp.get(i).value == astemp.get(j).value); )
                        astemp.remove(j);
                }
            }
            for (i = 0; i < (astemp.size() - 1); ++i) {
                for (j = i; j < (astemp.size() - 1); ++j) {
                    if (astemp.get(j).value + 1 == astemp.get(j + 1).value) {
                        temp.clear();
                        for (int y = i; y <= (j + 1); ++y) {
                            temp.add(astemp.get(y));
                        }
                        color_tractor_list.add(new Tractor(temp));
                    } else
                        break;
                }
            }
            i = 0;
            ArrayList<Tractor> tl = hc.independent_tractor_map.get(color);
            while (i < (as.size() - 1)) {
                for (j = i; j < (as.size() - 1); ++j) {
                    if (as.get(j).value + 1 != as.get(j + 1).value)
                        break;
                }
                if (i != j) {
                    temp.clear();
                    for (int k = i; k <= j; ++k)
                        temp.add(as.get(k));
                    tl.add(new Tractor(temp));
                }
                i = j + 1;
            }
            if (!tl.isEmpty()) {
                Collections.sort(tl, PokeGameTools.tractor_up_com);
                ArrayList<Integer> ll = hc.tractor_len_map.get(color);
                for (Tractor t : tl)
                    ll.add(t.len);
            }
        }


//		System.out.println("-------------Tractor---------------");
//
//		for (Iterator<Map.Entry<Integer, ArrayList<Tractor>>> it = hc.tractor_map.entrySet().iterator(); it
//				.hasNext();) {
//
//			Map.Entry<Integer, ArrayList<Tractor>> entry = (Map.Entry<Integer, ArrayList<Tractor>>) it.next();
//			ArrayList<Tractor> as = (ArrayList<Tractor>) entry.getValue();
//			for (Tractor t : as) {
//				for (Integer ig : t.pokes)
//					System.out.print(ig + " ");
//				System.out.println("---len:" + t.len + ",color:" + t.color + ",value:" + t.value);
//			}
//			System.out.println("color:" + entry.getKey() + "  num:" + as.size());
//			System.out.println();
//		}
//		System.out.println("Independent_tractor");
//		for (Iterator<Map.Entry<Integer, ArrayList<Tractor>>> it = hc.independent_tractor_map.entrySet().iterator(); it
//				.hasNext();) {
//
//			Map.Entry<Integer, ArrayList<Tractor>> entry = (Map.Entry<Integer, ArrayList<Tractor>>) it.next();
//			ArrayList<Tractor> as = (ArrayList<Tractor>) entry.getValue();
//			for (Tractor t : as) {
//				for (Integer ig : t.pokes)
//					System.out.print(ig + " ");
//				System.out.println("---len:" + t.len + ",color:" + t.color + ",value:" + t.value);
//			}
//			System.out.println("color:" + entry.getKey() + "  num:" + as.size());
//		}
//		System.out.println("-----------len list----------");
//		for (Iterator<Entry<Integer, ArrayList<Integer>>> it = hc.tractor_len_map.entrySet().iterator(); it
//				.hasNext();) {
//
//			Map.Entry<Integer, ArrayList<Integer>> entry = (Map.Entry<Integer, ArrayList<Integer>>) it.next();
//			ArrayList<Integer> ls = (ArrayList<Integer>) entry.getValue();
//			for (Integer t : ls) {
//				System.out.print(t + " ");
//			}
//			System.out.println();
//		}
//		System.out.println("-----------------------------------");
    }
}
