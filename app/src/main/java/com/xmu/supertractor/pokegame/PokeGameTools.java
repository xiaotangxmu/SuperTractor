package com.xmu.supertractor.pokegame;

import android.content.Context;
import android.util.SparseIntArray;
import android.widget.Toast;

import com.xmu.supertractor.card.Base_Card;
import com.xmu.supertractor.card.Tractor;
import com.xmu.supertractor.card.TypeDefine;
import com.xmu.supertractor.parameter.Status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.xmu.supertractor.Tools.PrintLog.log;


public class PokeGameTools {
    private static String tag = "PokeGameTools";
    public static String newLine = System.getProperty("line.separator");
    static Comparator<Integer> down_com = new Comparator<Integer>() {
        public int compare(Integer t1, Integer t2) {
            return t2 - t1;
        }
    };

    public static Comparator<Tractor> tractor_down_com = new Comparator<Tractor>() {
        public int compare(Tractor t1, Tractor t2) {
            if (t1.len == t2.len)
                return 0;
            else
                return t2.len - t1.len;
        }
    };

    static Comparator<Tractor> tractor_up_com = new Comparator<Tractor>() {
        public int compare(Tractor t1, Tractor t2) {
            if (t1.len == t2.len)
                return 0;
            else
                return t1.len - t2.len;
        }
    };

    public static Comparator<Integer> card_show_com = new Comparator<Integer>() {
        @Override
        public int compare(Integer i1, Integer i2) {
            if (2 == Status.main_color) {
                if ((getcolor(i1) == 3) && (getcolor(i2) == 4)) {
                    return 1;
                } else
                    return 0;
            } else if (3 == Status.main_color) {
                if ((getcolor(i1) == 1) && (getcolor(i2) == 2)) {
                    return 1;
                } else {
                    return 0;
                }
            } else
                return 0;
        }
    };

    public static void card_show_sort(ArrayList<Integer> al) {
        int l = al.size();

    }

    private static void quick_sort(ArrayList<Integer> al, int a, int b) {
        Integer start = al.get(a);

    }

    public static void maopao_card_show_sort(ArrayList<Integer> al) {
        if (Status.main_color != 2) {
            if (Status.main_color != 3)
                return;
        }
        int l = al.size();
        for (int i = 1; i <= l - 1; ++i) {
            for (int j = 0; j < l - i; ++j) {
                if (2 == Status.main_color) {
                    if ((getcolor(al.get(j)) == 3) && (getcolor(al.get(j + 1)) == 4)) {
                        Integer temp = al.get(j);
                        al.set(j, al.get(j + 1));
                        al.set(j + 1, temp);
                    }
                } else if (3 == Status.main_color) {
                    if ((getcolor(al.get(j)) == 1) && (getcolor(al.get(j + 1)) == 2)) {
                        Integer temp = al.get(j);
                        al.set(j, al.get(j + 1));
                        al.set(j + 1, temp);
                    }
                }
            }
        }
    }


    public static Comparator<Integer> cardcom = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            // TODO Auto-generated method stub
            if (o1.intValue() == o2.intValue())
                return 0;
            if (mainorno(o1) || mainorno(o2)) {
                int res = comparemap.get(o1) - comparemap.get(o2);
                return (res == 0) ? (getcolor(o1) - getcolor(o2)) : res;
            } else {
                int res = getcolor(o1) - getcolor(o2);
                return (res == 0) ? (comparemap.get(o1) - comparemap.get(o2)) : res;
            }
        }
    };
    private static SparseIntArray comparemap = new SparseIntArray();
    private static SparseIntArray colormap = new SparseIntArray();

    private PokeGameTools() {
    }

    static {
        colormap.put(0, -1);
        colormap.put(4, 0);
        colormap.put(3, 1);
        colormap.put(2, 2);
        colormap.put(1, 4);
        colormap.put(151, 5);
        colormap.put(161, 6);
    }

    public static int get_player_pos(int i) {
        for (int k = 1; k < 5; ++k) {
            if (Status.pos_list.get(k) == i) {
                return k;
            }
        }
        return 0;
    }

    static int get_color_num(Base_Card bc, int color) {
        int num = 0;
        for (Integer i : bc.pokes) {
            if (PokeGameTools.getcolor(i) == color)
                ++num;
        }
        return num;
    }

    public static void cardsort(List<Integer> cardlist) {
        if (!cardlist.isEmpty()) {
            Collections.sort(cardlist, cardcom);
        }
    }

    public static int getcolor(Integer i) {
        if (mainorno(i))
            return 5;
        else
            return i % 10;
    }

    public static void print_value_sort() {
        for (int i = 0; i < comparemap.size(); ++i) {
            log(tag, comparemap.keyAt(i) + "---" + comparemap.valueAt(i));
        }
    }

    public static int getvalue(Integer i) {
        return comparemap.get(i);
    }


    public static void computeval() {
        int t;
        if (0 != Status.main_color) {
            for (int i = 0; i < 52; ++i) {
                t = 20 + i / 4 * 10 + i % 4 + 1;
                if (Status.main_color != (t % 10)) {
                    if (Status.main_level != (t / 10)) {
                        if (t / 10 < Status.main_level) {
                            comparemap.put(t, t / 10 - 1);
                        } else {
                            comparemap.put(t, t / 10 - 2);
                        }
                    } else {
                        comparemap.put(t, 25);
                    }
                } else {
                    if (Status.main_level != (t / 10)) {
                        if (t / 10 < Status.main_level) {
                            comparemap.put(t, t / 10 + 11);
                        } else {
                            comparemap.put(t, t / 10 + 10);
                        }
                    } else {
                        comparemap.put(t, 26);
                    }
                }
            }
            comparemap.put(151, 27);
            comparemap.put(161, 28);
        }
        if (0 == Status.main_color) {
            for (int i = 0; i < 52; ++i) {
                t = 20 + i / 4 * 10 + i % 4 + 1;
                if (Status.main_level != (t / 10)) {
                    if (t / 10 < Status.main_level) {
                        comparemap.put(t, t / 10 - 1);
                    } else {
                        comparemap.put(t, t / 10 - 2);
                    }
                } else {
                    comparemap.put(t, 13);
                }
                comparemap.put(151, 14);
                comparemap.put(161, 15);
            }
        }
    }

    public static boolean mainorno(Integer num) {
        return comparemap.get(num) >= 13;
    }

    public static boolean comparecolor(Integer a, Integer b) {
        if (b == 0)
            return true;
        Integer colora = ((a == 151) || (a == 161)) ? a : a % 10;
        Integer colorb = ((b == 151) || (b == 161)) ? b : b % 10;
        int res = colormap.get(colora) - colormap.get(colorb);
        return res > 0;
    }


    public static String array_to_String(ArrayList<Integer> al) {
        StringBuilder sb = new StringBuilder();
        for (Integer i : al)
            sb.append(i).append("-");
        return sb.toString();
    }

    public static String type_to_string(int type) {
        switch (type) {
            case TypeDefine.single:
                return "single";
            case TypeDefine.pair:
                return "pair";
            case TypeDefine.tractor:
                return "tractor";
            case TypeDefine.shuai:
                return "shuai";
            case TypeDefine.tie:
                return "tie";
            default:
                return " ";
        }
    }

    public static void MyToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }


    public static int next_player(int p, int next) {
        int pos = PokeGameTools.get_player_pos(p);
        for (int i = 0; i < next; ++i)
            pos = (pos == 4 ? 1 : pos + 1);
        return Status.pos_list.get(pos);
    }
}
