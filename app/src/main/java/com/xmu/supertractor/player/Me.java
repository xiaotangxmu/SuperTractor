package com.xmu.supertractor.player;



import com.xmu.supertractor.connection.bluetooth.BluetoothComThread;
import com.xmu.supertractor.card.Out_Card;


public class Me extends Player {

    public Out_Card oc = new Out_Card();
    private static Me me = null;
    public BluetoothComThread blueComThread;
    public boolean server_flag;

    public static void create_me(String name) {
        me = null;
        me = new Me(name);
    }

    public static Me get_me() {
        return me;
    }

    private Me(String name) {
        super(name);
        this.oc.clear();
        this.server_flag = false;
        this.blueComThread = null;
    }


}
