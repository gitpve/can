package com.Util;

import com.Const.CanbusKeys;

public class AirInfo {
	
	public int air_condition;
	public int air_update;
	public float Ltp;
	public float Rtp;
	public int windlevle;
	public int cycle;
	public int supplyDefault;
	public int supplyUp;
	public int supplyDown;
	public int FClear;
	public int BClear;
	public int LSeat;
	public int RSeat;
	public int ac;
	public int auto1;
	public int auto2;
	public int max;
	public int dual;
	public int rear;
	public int unit;
	public boolean bShow;
	
	public void clear(){
		air_condition=CanbusKeys.AIR_CONDITIONING_OFF;
		air_update=CanbusKeys.AIR_CONDITIONING_OFF;
		Ltp=0;
		Rtp=0;
		windlevle=CanbusKeys.AIR_CONDITIONING_LEVEL0;
		cycle=CanbusKeys.AIR_CONDITIONING_AQS_CYCLE;
		supplyDefault=CanbusKeys.AIR_CONDITIONING_OFF;
		supplyUp=CanbusKeys.AIR_CONDITIONING_OFF;
		supplyDown=CanbusKeys.AIR_CONDITIONING_OFF;
		FClear=CanbusKeys.AIR_CONDITIONING_OFF;
		BClear=CanbusKeys.AIR_CONDITIONING_OFF;
		ac=CanbusKeys.AIR_CONDITIONING_OFF;
		auto1=CanbusKeys.AIR_CONDITIONING_OFF;
		auto2=CanbusKeys.AIR_CONDITIONING_OFF;
		max=CanbusKeys.AIR_CONDITIONING_OFF;
		dual=CanbusKeys.AIR_CONDITIONING_OFF;
		rear=CanbusKeys.AIR_CONDITIONING_OFF;
		LSeat=CanbusKeys.SEAT_LEVEL0;
		RSeat=CanbusKeys.SEAT_LEVEL0;
		bShow=false;
		unit=0;
	}

}
