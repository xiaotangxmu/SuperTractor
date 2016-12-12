package com.xmu.supertractor.player;

import android.util.Log;
import android.util.SparseArray;


public class PlayerList {
    public SparseArray<Player> map = null;

    private static PlayerList playerlist = null;

    private PlayerList() {
        map = new SparseArray<>();
    }

    public static void init_playerlist() {
        playerlist = null;
        playerlist = new PlayerList();
    }

    public static PlayerList getPlayerList() {
        if (null == playerlist)
            playerlist = new PlayerList();
        return playerlist;
    }

    public void add_player(Player player) {
        if (!exist_or_not(player.seq)) {
            playerlist.map.put(player.seq, player);
            Log.d("player", "playerlist add seq:" + player.seq + "," + player.name);
        }
    }

    public Player getPlayer(int i) {
        if (exist_or_not(i))
            return playerlist.map.get(i);
        else {
            Log.d("player", "getPlayer " + i + " Error!");
            return null;
        }
    }

    public void add_player(int seq, String name) {
        if (!exist_or_not(seq)) {
            Player p = new Player(seq, name);
            add_player(p);
        } else {
            Log.d("xiaotang", seq + " player already exist");
        }
    }

    private boolean exist_or_not(int seq) {
        if (map.size() == 0)
            return false;
        boolean exist = false;
        for (int i = 0, nsize = map.size(); i < nsize; i++) {
            Player player = map.valueAt(i);
            if(player.seq==seq){
                exist=true;
                break;
            }
        }
        return exist;
    }
//
//    public Player getFrontPlayer(int i) {
//        return getPlayer((1 == i) ? 3 : i - 1);
//    }
//
//    public Player getNextPlayer(int i) {
//        return getPlayer((3 == i) ? 1 : i + 1);
//    }
//

}
