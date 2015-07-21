package com.Canbus.jeep;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.widget.Toast;

import com.Const.CanbusKeys;
import com.Const.PveKeys;
import com.Interface.RadarInterface;
import com.Interface.SwitchRadarAndBackInterface;
import com.Interface.VoiceInterface;
import com.Util.AirInfo;
import com.Util.DoorInfo;
import com.Util.RadarInfo;
import com.Util.SetMute;
import com.Util.VoiceInfo;

public class CanbusTransData {
	private AirInfo airInfo = new AirInfo();
	private DoorInfo doorInfo = new DoorInfo();
	private RadarInfo radarInfoBack = new RadarInfo();
	private RadarInfo radarInfoFront = new RadarInfo();
	private RadarInfo radarInfoBackLast = new RadarInfo();
	private VoiceInfo vi=new VoiceInfo();
	private VoiceInterface vInterface;
	
	private SetMute sm=new SetMute();

	private int key;
	private static CanbusTransData transData = new CanbusTransData();

	public static CanbusTransData getTransData() {
		return transData;
	}

	private CanbusTransData() {
	}

	public AirInfo getAirInfo() {
		return airInfo;
	}

	public DoorInfo getDoorInfo() {
		return doorInfo;
	}

	public int getKey() {

		int tranKey = 0;
		switch (key) {
		case CanbusKeys.VOL_ADD:
			tranKey = PveKeys.VOL_ADD;
			break;
		case CanbusKeys.VOL_SUB:
			tranKey = PveKeys.VOL_SUB;
			break;
		case CanbusKeys.NEXT:
			tranKey = PveKeys.NEXT;
			break;
		case CanbusKeys.PREV:
			tranKey = PveKeys.PREV;
			break;
		case CanbusKeys.MODE:
			tranKey = PveKeys.SRC;
			break;
		case CanbusKeys.BT:
			tranKey = PveKeys.PICKUP;
			break;
		case CanbusKeys.MUTE:
			tranKey = PveKeys.VOICE;
			break;
		case CanbusKeys.BAND:
			sm.setSlientMode();
//			tranKey = 0;
			break;
		default:
			break;
		}
		return tranKey;
	}

	public int TransData(String result) {
		if (result == null) {
			key = 0;
			return CanbusKeys.NONE;
		}
		try {
			JSONArray jsons = new JSONArray(result);
			for (int index = 0; index < jsons.length(); index++) {
				doorInfo.clear();
				airInfo.clear();
				doorInfo.bShow = false;
				airInfo.bShow = false;

				JSONObject js = jsons.getJSONObject(index);
				JSONObject jsTemp = js.getJSONObject("CANBUS");
				String strInfo = jsTemp.getString("DataType");

				if (strInfo != null && strInfo.equals("key")) {
					String valueString = jsTemp.getString("Value");
					if (valueString != null) {
						jsTemp = new JSONObject(valueString);
						key = jsTemp.getInt("keyValue");
					}
					return CanbusKeys.KEY;
				} else if (strInfo != null && strInfo.equals("door_info")) {
					airInfo.bShow = false;
					doorInfo.bShow = true;
					key = 0;

					String valueString = jsTemp.getString("Value");
					if (valueString != null) {
						jsTemp = new JSONObject(valueString);
						String strDoor = jsTemp.getString("DoorFront");
						if (strDoor != null && strDoor.equals("open"))
							doorInfo.front = true;
						strDoor = jsTemp.getString("DoorBack");
						if (strDoor != null && strDoor.equals("open"))
							doorInfo.back = true;
						strDoor = jsTemp.getString("DoorLeftFront");
						if (strDoor != null && strDoor.equals("open"))
							doorInfo.left_front = true;
						strDoor = jsTemp.getString("DoorLeftBack");
						if (strDoor != null && strDoor.equals("open"))
							doorInfo.left_back = true;
						strDoor = jsTemp.getString("DoorRightFront");
						if (strDoor != null && strDoor.equals("open"))
							doorInfo.right_front = true;
						strDoor = jsTemp.getString("DoorRightBack");
						if (strDoor != null && strDoor.equals("open"))
							doorInfo.right_back = true;
					}
					return CanbusKeys.DIALOG;
				}  else if (strInfo != null && strInfo.equals("radar_back")) {
					radarInfoBack.bShow = true;
					String valueString = jsTemp.getString("Value");
					if (valueString != null) {
						jsTemp = new JSONObject(valueString);

						radarInfoBack.left = jsTemp.getInt("back_left");
						radarInfoBack.left_midd = jsTemp
								.getInt("back_left_mid");
						radarInfoBack.right_midd = jsTemp
								.getInt("back_right_mid");
						radarInfoBack.right = jsTemp.getInt("back_rihgt");

						if (car_park_state != CanbusKeys.RADAR_PARK_ON) {
							updateParkOn(true);
						}
						if (radarInterface != null
								&& isRadarDataChanged(radarInfoBackLast,
										radarInfoBack)) {
							radarInterface
									.updateRadarData(radarInfoBack, false);
							radarInfoBackLast.left = radarInfoBack.left;
							radarInfoBackLast.left_midd = radarInfoBack.left_midd;
							radarInfoBackLast.right_midd = radarInfoBack.right_midd;
							radarInfoBackLast.right = radarInfoBack.right;
						}
						OnTimer();
					}
					return CanbusKeys.NONE;
				}else if(strInfo!=null&&strInfo.equals("vol_info")){
					String valueString=jsTemp.getString("Value");
					if(valueString!=null){
						jsTemp=new JSONObject(valueString);
						
						vi.volume=jsTemp.getInt("Volume");
						vi.bass=jsTemp.getInt("Bass");
						vi.balance=jsTemp.getInt("Balance");
						vi.treble=jsTemp.getInt("Treble");
						vi.fader=jsTemp.getInt("Fader");
						vi.middle=jsTemp.getInt("Middle");
						
						if(vInterface!=null){
							vInterface.updateVoice(vi);
						}
						
						
						
						
						
						
						
						
						
						System.out.println("-----vol_info-----"+jsTemp.toString());
						
						Toast.makeText(CanbusService.getContext(), jsTemp.toString(), Toast.LENGTH_LONG).show();
						
						
						
						
						
					}
					return CanbusKeys.NONE;
				}else if(strInfo!=null&&strInfo.equals("version_info")){
					String valueString=jsTemp.getString("Value");
					if(valueString!=null){
						jsTemp=new JSONObject(valueString);
						
						System.out.println("------version_info---------"+jsTemp.toString());
						Toast.makeText(CanbusService.getContext(), jsTemp.toString(), Toast.LENGTH_LONG).show();
						
						
						
						
						
					}
					return CanbusKeys.NONE;
				}else if(strInfo!=null&&strInfo.equals("amp_state")){
					String valueString=jsTemp.getString("Value");
					if(valueString!=null){
						jsTemp=new JSONObject(valueString);
						
						Toast.makeText(CanbusService.getContext(), jsTemp.toString(), Toast.LENGTH_LONG).show();
						System.out.println("--------amp_state-------"+jsTemp.toString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CanbusKeys.NONE;
	}

	// ---------------------------radar-------------------

	public RadarInfo getRadarInfoBack() {
		return radarInfoBack;
	}

	public RadarInfo getRadarInfoFront() {
		return radarInfoFront;
	}

	private RadarInterface radarInterface = null;

	public void setRadarListener(RadarInterface ri) {
		radarInterface = ri;
	}

	public void releaseRadarListener() {
		radarInterface = null;
	}

	private static int car_park_state = CanbusKeys.RADAR_PARK_OFF;

	private void updateParkOn(boolean on) {
		if (on) {
			car_park_state = CanbusKeys.RADAR_PARK_ON;
			if (CanbusKeys.RADAR_GEARS_BACK == getGearState()) {
				switch2Back();
			} else {
				switch2Radar();
			}
			PveJEEPRadarApplication.getInstance().showFloatingFrame();
		} else {
			car_park_state = CanbusKeys.RADAR_PARK_OFF;
			switch2Radar();
			if (radarInterface != null) {
				radarInterface.closeActivity();
			}
		}
	}

	private void switch2Back() {
		SwitchRadarAndBackInterface.getInstance().backsetval();
	}

	private void switch2Radar() {
		SwitchRadarAndBackInterface.getInstance().radarsetval();
	}

	public int getGearState() {
		return SwitchRadarAndBackInterface.getInstance().getGearval();
	}

	private Timer mTimer = null;

	private void OnTimer() {
		if (mTimer != null)
			mTimer.cancel();
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				mTimer.cancel();
				updateParkOn(false);
			}
		}, 1000);
	}

	private boolean isRadarDataChanged(RadarInfo riLast, RadarInfo riNew) {
		if (riLast.left != riNew.left || riLast.left_midd != riNew.left_midd
				|| riLast.right != riNew.right
				|| riLast.right_midd != riNew.right_midd) {
			return true;
		} else {
			return false;
		}
	}
	// --------------------------radar-----------------------------
	
	public void setVoiceInterface(VoiceInterface vinter){
		vInterface=vinter;
		
	}

}
