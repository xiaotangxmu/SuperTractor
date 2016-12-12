package com.xmu.supertractor.connection.logic;


import com.xmu.supertractor.connection.bluetooth.admin.BluetoothAdmin;
import com.xmu.supertractor.connection.transmitunit.TransmitUnit;
import com.xmu.supertractor.connection.transmitunit.Unit_Player_Broadcast_Info;
import com.xmu.supertractor.connection.transmitunit.Unit_Player_Info;
import com.xmu.supertractor.connection.transmitunit.Unit_Pos_Info;
import com.xmu.supertractor.connection.wifi.admin.WifiAdmin;
import com.xmu.supertractor.connection.wifi.socket.AcceptThread;
import com.xmu.supertractor.desk.Desk;
import com.xmu.supertractor.parameter.Status;
import com.xmu.supertractor.player.Me;
import com.xmu.supertractor.player.PlayerList;
import com.xmu.supertractor.pokegame.PokeGameTools;

import static com.xmu.supertractor.Tools.PrintLog.log;



public class Connectionlogic {

    private static PlayerList playerlist;
    private static Desk desk;
    private static Me me;
    private static String tag = "Connectionlogic";

    public static void server_init() {
        desk = Desk.dk_getInstance();
        me = Me.get_me();
    }

    public static void client_init() {
        playerlist = PlayerList.getPlayerList();
        me = Me.get_me();
    }

    public static void add_myself() {
        me.seq = 1;
        desk.add_player(1, me.name);
        playerlist.add_player(me);
    }

    public static void connect_successs_client() {
        //noinspection StatementWithEmptyBody
        if (Status.wifi_or_bluetooth) {
            //noinspection UnnecessarySemicolon
            ;
        } else {
            me.blueComThread = BluetoothAdmin.CommunThread_map.get(0);
        }
        send_message_to_server(new TransmitUnit(0, -1, 0, me.name));
    }

    public synchronized static void receive_client_name_server(TransmitUnit tu) {
        String name = (String) tu.obj;
        desk.add_player(Status.connected_num+1, name);
        PlayerList.getPlayerList().add_player(Status.connected_num+1, name);
        AcceptThread.connected_statu=true;
        Unit_Player_Info u1 = new Unit_Player_Info(Status.connected_num+1, me.name);
        TransmitUnit msg_data = new TransmitUnit(1, 0, Status.connected_num+1, u1);
        send_message_to_client(msg_data);
    }


    public static void receive_my_seq_client(TransmitUnit recdata) {
        Unit_Player_Info u1 = (Unit_Player_Info) recdata.obj;
        String recname = u1.name;
        me.seq = u1.seq;
        playerlist.add_player(1, recname);
        playerlist.add_player(me);
        TransmitUnit msg_data = new TransmitUnit(2, 0, 0, me.seq);
        send_message_to_server(msg_data);
    }

//    public synchronized static void broadcast_palyer_info_server(TransmitUnit tu) {
//        boolean flag = false;
//        int num = (int) tu.obj;
//        Unit_Player_Broadcast_Info upbi = new Unit_Player_Broadcast_Info();
//        upbi.num = num;
//        log(tag, "Connected num:" + num + "");
//        for (int i = 1; i <= num; ++i)
//            log(tag, "desk member:" + i + (!desk.exist_or_not(i) ? " null" : desk.getMember(i).name));
//        while (!flag) {
//            flag = true;
//            for (int i = 1; i <= num; ++i) {
//                if (!desk.exist_or_not(i)) {
//                    flag = false;
//                    break;
//                }
//            }
//            if (flag) {
//                for (int i = 1; i <= num; ++i) {
//                    upbi.player_map.put(i, desk.getMember(i).name);
//                }
//                for (int i = 2; i <= num; ++i) {
//                    TransmitUnit t = new TransmitUnit(3, 0, i, upbi);
//                    send_message_to_client(t);
//                }
//            } else {
//
//            }
//        }
//
//        for (int i = 1; i <= num; ++i) {
//            upbi.player_map.put(i, desk.getMember(i).name);
//        }
//        for (int i = 2; i <= num; ++i) {
//            TransmitUnit t = new TransmitUnit(3, 0, i, upbi);
//            send_message_to_client(t);
//        }
//    }

    public static int receive_broadcast_client(TransmitUnit tu) {
        Unit_Player_Broadcast_Info upbi = (Unit_Player_Broadcast_Info) tu.obj;
        for (int i = 1; i <= upbi.num; ++i) {
            PlayerList.getPlayerList().add_player(i, upbi.player_map.get(i));
        }
        for (int i = 1; i <= upbi.num; ++i) {
            log(tag, i + "," + PlayerList.getPlayerList().getPlayer(i).name);
        }
        return upbi.num;
    }

    public static void send_message_to_client(TransmitUnit unit) {
        if (Status.wifi_or_bluetooth) {
            switch (unit.dest) {
                case 0:
                    if (WifiAdmin.CommunThread_map.get(2) != null)
                        WifiAdmin.CommunThread_map.get(2).writeObject(unit);
                    if (WifiAdmin.CommunThread_map.get(3) != null)
                        WifiAdmin.CommunThread_map.get(3).writeObject(unit);
                    if (WifiAdmin.CommunThread_map.get(4) != null)
                        WifiAdmin.CommunThread_map.get(4).writeObject(unit);
                    break;
                case 1:

                    break;
                case 2:
                    if (WifiAdmin.CommunThread_map.get(2) != null)
                        WifiAdmin.CommunThread_map.get(2).writeObject(unit);
                    break;
                case 3:
                    if (WifiAdmin.CommunThread_map.get(3) != null)
                        WifiAdmin.CommunThread_map.get(3).writeObject(unit);
                    break;
                case 4:
                    if (WifiAdmin.CommunThread_map.get(4) != null)
                        WifiAdmin.CommunThread_map.get(4).writeObject(unit);
                    break;
                default:
                    break;
            }
        } else {
            switch (unit.dest) {
                case 0:
                    if (BluetoothAdmin.CommunThread_map.get(2) != null)
                        BluetoothAdmin.CommunThread_map.get(2).writeObject(unit);
                    if (BluetoothAdmin.CommunThread_map.get(3) != null)
                        BluetoothAdmin.CommunThread_map.get(3).writeObject(unit);
                    if (BluetoothAdmin.CommunThread_map.get(4) != null)
                        BluetoothAdmin.CommunThread_map.get(4).writeObject(unit);
                    break;
                case 2:
                    if (BluetoothAdmin.CommunThread_map.get(2) != null)
                        BluetoothAdmin.CommunThread_map.get(2).writeObject(unit);
                    break;
                case 3:
                    if (BluetoothAdmin.CommunThread_map.get(3) != null)
                        BluetoothAdmin.CommunThread_map.get(3).writeObject(unit);
                    break;
                case 4:
                    if (BluetoothAdmin.CommunThread_map.get(4) != null)
                        BluetoothAdmin.CommunThread_map.get(4).writeObject(unit);
                    break;
                default:
                    break;
            }
        }
    }

    public static void send_message_to_server(TransmitUnit unit) {
        if (Status.wifi_or_bluetooth) {
            WifiAdmin.clientrhread.writeObject(unit);
        } else {
            BluetoothAdmin.CommunThread_map.get(0).writeObject(unit);
        }
    }

    public static void receive_pos_info(TransmitUnit unit) {
        Unit_Pos_Info upi= (Unit_Pos_Info) unit.obj;
        Status.pos_list.clear();
        Status.pos_list.addAll(upi.pos_list);
        Status.level_list.clear();
        Status.level_list.addAll(upi.a_or_b);
        log(tag, PokeGameTools.array_to_String(Status.pos_list));
        log(tag, PokeGameTools.array_to_String(Status.level_list));
        while (Status.pos_list.get(3) != me.seq) {
            Status.pos_list.set(1, Status.pos_list.get(2));
            Status.pos_list.set(2, Status.pos_list.get(3));
            Status.pos_list.set(3, Status.pos_list.get(4));
            Status.pos_list.set(4, Status.pos_list.get(0));
            Status.pos_list.set(0, Status.pos_list.get(1));
        }
        log(tag, PokeGameTools.array_to_String(Status.pos_list));
    }
}
