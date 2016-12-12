package com.xmu.supertractor.desk;

import com.xmu.supertractor.connection.bluetooth.BluetoothComThread;
import com.xmu.supertractor.card.Hand_Card;
import com.xmu.supertractor.card.Out_Card;

public class Member {
    public String name;
    public int seq;
    Hand_Card hc = null;
    public Out_Card oc=null;

    public BluetoothComThread bluetooththread;

    Member(int seq, String name, BluetoothComThread bluetooththread) {
        super();
        this.name = name;
        this.hc = new Hand_Card();
        this.oc = new Out_Card();
        this.seq = seq;
        this.bluetooththread = bluetooththread;
    }



    Member(int seq, String name) {
        super();
        this.name = name;
        this.hc = new Hand_Card();
        this.oc = new Out_Card();
        this.seq = seq;
    }
}
