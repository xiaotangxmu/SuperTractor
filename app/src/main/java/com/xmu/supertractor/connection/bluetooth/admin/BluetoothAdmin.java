package com.xmu.supertractor.connection.bluetooth.admin;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.xmu.supertractor.connection.bluetooth.BluetoothComThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BluetoothAdmin {

	public static List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
	public static List<BluetoothDevice> contectedDevices = new ArrayList<BluetoothDevice>();
	public static BluetoothAdapter adapter = BluetoothAdapter
			.getDefaultAdapter();
	public static HashMap<Integer,BluetoothComThread> CommunThread_map = new HashMap<Integer,BluetoothComThread>();
	public static final UUID PRIVATE_UUID = UUID
			.fromString("0f3561b9-bda5-4672-84ff-ab1f98e349b6");

	/**
	 * 字符串常量，存放在Intent中的设备对象
	 */
	public static final String DEVICE = "DEVICE";

	/**
	 * 字符串常量，服务器所在设备列表中的位置
	 */
	public static final String SERVER_INDEX = "SERVER_INDEX";




	/**
	 * 字符串常量，Intent中的数据
	 */
	public static final String DATA = "DATA";



	/**
	 * Action类型标识符，Action类型 为读到数据
	 */
	public static final String ACTION_READ_DATA = "ACTION_READ_DATA";

	/**
	 * Action类型标识符，Action类型为 未发现设备
	 */
	public static final String ACTION_NOT_FOUND_SERVER = "ACTION_NOT_FOUND_DEVICE";

	/**
	 * Action类型标识符，Action类型为 开始搜索设备
	 */
	public static final String ACTION_START_DISCOVERY = "ACTION_START_DISCOVERY";

	/**
	 * Action：设备列表
	 */
	public static final String ACTION_FOUND_DEVICE = "ACTION_FOUND_DEVICE";

	/**
	 * Action：选择的用于连接的设备
	 */
	public static final String ACTION_SELECTED_DEVICE = "ACTION_SELECTED_DEVICE";

	/**
	 * Action：到游戏业务中的数据
	 */
	public static final String ACTION_DATA_TO_ACTIVITY = "ACTION_DATA_TO_ACTIVITY";

	/**
	 * Action：连接成功
	 */
	public static final String ACTION_CONNECT_SUCCESS = "ACTION_CONNECT_SUCCESS";

	/**
	 * Action：连接错误
	 */
	public static final String ACTION_CONNECT_ERROR = "ACTION_CONNECT_ERROR";

	/**
	 * Message类型标识符，连接成功
	 */
	public static final int MESSAGE_CONNECT_SUCCESS = 0x00000002;

	/**
	 * Message：连接失败
	 */
	public static final int MESSAGE_CONNECT_ERROR = 0x00000003;

	/**
	 * Message：读取到一个对象
	 */
	public static final int MESSAGE_READ_OBJECT = 0x00000001;

	public static void stopDiscovery() {
		adapter.cancelDiscovery();
	}

}