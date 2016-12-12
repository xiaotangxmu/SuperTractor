package com.xmu.supertractor.pokegame;

import com.xmu.supertractor.card.Hand_Card;
import com.xmu.supertractor.card.Pair;
import com.xmu.supertractor.card.Single;
import com.xmu.supertractor.card.Tractor;

import java.util.ArrayList;
import java.util.Collections;


public class AnalyzeHandPokes {
    public static void analyze_hand_pokes(Hand_Card hc) {
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

        // System.out.println("-------------Single---------------");
        // for (Iterator<Map.Entry<Integer, ArrayList<Single>>> it =
        // hc.single_map.entrySet().iterator(); it.hasNext();) {
        // Map.Entry<Integer, ArrayList<Single>> entry = (Map.Entry<Integer,
        // ArrayList<Single>>) it.next();
        // ArrayList<Single> as = (ArrayList<Single>) entry.getValue();
        // for (Single s : as) {
        // System.out.println(s.pokes.get(0) + ":value:" + s.value + ",color:" +
        // s.color);
        // }
        // System.out.println("color:" + entry.getKey() + " num:" + as.size());
        // System.out.println();
        // }
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

//		System.out.println("-------------Pair---------------");
//
//		for (Iterator<Map.Entry<Integer, ArrayList<Pair>>> it = hc.pair_map.entrySet().iterator(); it.hasNext();) {
//			Map.Entry<Integer, ArrayList<Pair>> entry = (Map.Entry<Integer, ArrayList<Pair>>) it.next();
//			ArrayList<Pair> as = (ArrayList<Pair>) entry.getValue();
//			for (Pair p : as) {
//				System.out.println(p.pokes.get(0) + "," + p.pokes.get(1) + ":val:" + p.value + ",color:" + p.color);
//			}
//			System.out.println("color:" + entry.getKey() + "  num:" + as.size());
//			System.out.println();
//		}
    }

    private static void analyze_tractor(Hand_Card hc) {
        for (int i = 1; i <= 5; ++i) {
            hc.tractor_map.get(i).clear();
            hc.independent_tractor_map.get(i).clear();
        }

        for (int x = 0; x < hc.pair_map.size(); x++) {
            int color= hc.pair_map.keyAt(x);
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
