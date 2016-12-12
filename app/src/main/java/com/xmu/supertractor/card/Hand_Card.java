package com.xmu.supertractor.card;


import android.util.SparseArray;

import java.util.ArrayList;


public class Hand_Card extends Base_Card {
	//手牌类型
	public SparseArray<ArrayList<Single>> single_map=new SparseArray<>();
	public SparseArray< ArrayList<Pair>> pair_map = new SparseArray<>();
	public SparseArray< ArrayList<Tractor>> tractor_map = new SparseArray<>();
	public SparseArray< ArrayList<Tractor>> independent_tractor_map = new SparseArray<>();
	public SparseArray< ArrayList<Integer>> tractor_len_map=new SparseArray< >();
	
	public Hand_Card() {
		ArrayList<Single> heitao_single = new ArrayList<>();
		single_map.put(TypeDefine.heitao, heitao_single);
		ArrayList<Single> hongxin_single = new ArrayList<>();
		single_map.put(TypeDefine.hongxin, hongxin_single);
		ArrayList<Single> meihua_single = new ArrayList<>();
		single_map.put(TypeDefine.meihua, meihua_single);
		ArrayList<Single> fangpian_single = new ArrayList<>();
		single_map.put(TypeDefine.fangpian, fangpian_single);
		ArrayList<Single> main_single = new ArrayList<>();
		single_map.put(TypeDefine.main, main_single);

		ArrayList<Pair> heitao_pair = new ArrayList<>();
		pair_map.put(TypeDefine.heitao, heitao_pair);
		ArrayList<Pair> hongxin_pair = new ArrayList<>();
		pair_map.put(TypeDefine.hongxin, hongxin_pair);
		ArrayList<Pair> meihua_pair = new ArrayList<>();
		pair_map.put(TypeDefine.meihua, meihua_pair);
		ArrayList<Pair> fangpian_pair = new ArrayList<>();
		pair_map.put(TypeDefine.fangpian, fangpian_pair);
		ArrayList<Pair> main_pair = new ArrayList<>();
		pair_map.put(TypeDefine.main, main_pair);

		ArrayList<Tractor> heitao_tractor = new ArrayList<>();
		tractor_map.put(TypeDefine.heitao, heitao_tractor);
		ArrayList<Tractor> hongxin_tractor = new ArrayList<>();
		tractor_map.put(TypeDefine.hongxin, hongxin_tractor);
		ArrayList<Tractor> meihua_tractor = new ArrayList<>();
		tractor_map.put(TypeDefine.meihua, meihua_tractor);
		ArrayList<Tractor> fangpian_tractor = new ArrayList<>();
		tractor_map.put(TypeDefine.fangpian, fangpian_tractor);
		ArrayList<Tractor> main_tractor = new ArrayList<>();
		tractor_map.put(TypeDefine.main, main_tractor);

		ArrayList<Tractor> heitao_independent_tractor = new ArrayList<>();
		independent_tractor_map.put(TypeDefine.heitao, heitao_independent_tractor);
		ArrayList<Tractor> hongxin_independent_tractor = new ArrayList<>();
		independent_tractor_map.put(TypeDefine.hongxin, hongxin_independent_tractor);
		ArrayList<Tractor> meihua_independent_tractor = new ArrayList<>();
		independent_tractor_map.put(TypeDefine.meihua, meihua_independent_tractor);
		ArrayList<Tractor> fangpian_independent_tractor = new ArrayList<>();
		independent_tractor_map.put(TypeDefine.fangpian, fangpian_independent_tractor);
		ArrayList<Tractor> main_independent_tractor = new ArrayList<>();
		independent_tractor_map.put(TypeDefine.main, main_independent_tractor);

		ArrayList<Integer> heitao_tractor_len = new ArrayList<>();
		tractor_len_map.put(TypeDefine.heitao, heitao_tractor_len);
		ArrayList<Integer> hongxin_tractor_len = new ArrayList<>();
		tractor_len_map.put(TypeDefine.hongxin, hongxin_tractor_len);
		ArrayList<Integer> meihua_tractor_len = new ArrayList<>();
		tractor_len_map.put(TypeDefine.meihua, meihua_tractor_len);
		ArrayList<Integer> fangpian_tractor_len = new ArrayList<>();
		tractor_len_map.put(TypeDefine.fangpian, fangpian_tractor_len);
		ArrayList<Integer> main_tractor_len = new ArrayList<>();
		tractor_len_map.put(TypeDefine.main, main_tractor_len);
	}
	

}
