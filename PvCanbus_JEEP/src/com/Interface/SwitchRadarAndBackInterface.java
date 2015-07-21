package com.Interface;

import com.Const.CanbusKeys;


import android.util.Log;

public class SwitchRadarAndBackInterface {

	private static SwitchRadarAndBackInterface srabInterface = null;

	private SwitchRadarAndBackInterface() {
	}

	public static SwitchRadarAndBackInterface getInstance() {
		if (srabInterface == null) {
			srabInterface = new SwitchRadarAndBackInterface();
		}
		return srabInterface;
	}

	public native boolean backsetval_native();

	public native boolean radarsetval_native();

	public native int getval_native();
	public native int getinfoval_native();

	public int getGearval() {
		int result = getval_native();
		log_d("getval =" + result);
		if(result == 49){
			return CanbusKeys.RADAR_GEARS_BACK;
		}else{
			return CanbusKeys.RADAR_GEARS_NOT_BACK;	
		}
	}
	
	public int getParkState(){
		return getinfoval_native();
	}
	
	public void radarsetval() {
		log_d("radarsetval_native");
		radarsetval_native();
	}

	public void backsetval() {
		log_d("backsetval_native");
		backsetval_native();
	}

	private static void log_d(String msg) {
		Log.d("SwitchInterface", msg);

	}

	static {
		log_d("System.loadLibrary(RtkbackServiceJni)");
		System.loadLibrary("RtkbackServiceJni");
	}
}
