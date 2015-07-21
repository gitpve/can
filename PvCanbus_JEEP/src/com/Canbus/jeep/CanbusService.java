package com.Canbus.jeep;

import java.nio.charset.Charset;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.Const.CanbusKeys;
import com.ContentObserver.PveContentObserver;
import com.Interface.CanbusListener;
import com.Util.AirInfo;
import com.Util.DoorInfo;
import com.Util.LogUtil;
import com.Util.RadarInfo;
import com.controller.CanbusController;

public class CanbusService extends Service implements CanbusListener {

	private static CanbusTransData mTransData = null;
	private static CanbusListener mCanbusListener = null;
	private static CanbusController controller = null;
	private static Context context = null;
	private static PveContentObserver contentObserver;
	private static AcDialogEx dialogEx = null;

	private final String COM_PVE_CANBUS_CMD = "com.android.canbus.receiver";
	private final String PVE_CANBUS_RADIO_SET = "com.pve.canbus.radioset";
	private UIBroadcastReceiver broadcastReceiver = new UIBroadcastReceiver();

	class UIBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String intentAction = intent.getAction();
			LogUtil.Log4d("receiver action+", intentAction);
			if (intentAction.equals(Intent.ACTION_SCREEN_OFF)) {
				;//
			} else if (intentAction.equals(Intent.ACTION_SCREEN_ON)) {
				controller.connect();
			} else if (intentAction.equals(Intent.ACTION_TIME_TICK)) {
				//
			} else if (intentAction.equals(PVE_CANBUS_RADIO_SET)) {
				int band = intent.getIntExtra("canbus_radio_band", 0);
				int freq = intent.getIntExtra("canbus_radio_freq", 0);
				if (freq != 0) {
					controller.setFreq(band, freq);
				}

			} else if (intentAction.equals(COM_PVE_CANBUS_CMD)) {
				Bundle bundle = intent.getExtras();
				try {

					byte[] firstByte = null;
					byte[] secondByte = null;
					String str_first = bundle.get("first_cmd").toString();
					if (str_first != null) {
						firstByte = str_first.getBytes(Charset
								.forName("ISO-8859-1"));
					}

					String str_second = bundle.get("second_cmd").toString();
					if (str_second != null) {
						secondByte = str_second.getBytes(Charset
								.forName("ISO-8859-1"));
					}
					controller.dashboardShow(firstByte, secondByte);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {

		registerReceiver();
		contentObserver = new PveContentObserver(new Handler());
		mTransData = CanbusTransData.getTransData();
		controller = CanbusController.getController();
		controller.setListener(mCanbusListener);
		controller.open();
		context = getApplicationContext();
		contentObserver.setContext(context);

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	public void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(COM_PVE_CANBUS_CMD);
		filter.addAction(PVE_CANBUS_RADIO_SET);
		registerReceiver(broadcastReceiver, filter);
	}

	public static CanbusTransData getTransData() {
		return mTransData;

	}

	public static Context getContext() {
		return context;
	}

	public static void setListener(CanbusListener listener) {
		mCanbusListener = listener;
	}

	@Override
	public void listener() {
		if (mCanbusListener != null)
			mCanbusListener.listener();
	}

	public static AirInfo getAirInfo() {
		return mTransData.getAirInfo();
	}

	public static DoorInfo getDoorInfo() {
		return mTransData.getDoorInfo();
	}

	public static int getKey() {
		return mTransData.getKey();

	}

	public static RadarInfo getRadarInfoBack() {
		return mTransData.getRadarInfoBack();
	}

	public static RadarInfo getRadarInfoFront() {
		return mTransData.getRadarInfoFront();
	}

	private static void createDialog() {
		LogUtil.Log4d("canbus--", "createDialog");
		if (dialogEx == null)
			dialogEx = new AcDialogEx(context);
		dialogEx.show();
	}

	public static void transData(String result) {
		if (mTransData != null) {
			int ret = mTransData.TransData(result);
			if (ret == CanbusKeys.DIALOG) {
				createDialog();
			} else if (ret == CanbusKeys.KEY) {
				int transKey = getKey();

			
				
				
				if (!contentObserver.queryBack()
						&& contentObserver.queryPower()) {

					if (transKey == 0)
						return;
					if (!contentObserver.queryPhone()) {
						controller.postKey(transKey);
						if (transKey == 220)
							controller.postKey(108);
						else if (transKey == 221)
							controller.postKey(103);
					} else if (transKey == 219 || transKey == 169
							|| transKey == 115 || transKey == 14) {
						controller.postKey(transKey);
					}
				}
			}
		}
	}
	
}
