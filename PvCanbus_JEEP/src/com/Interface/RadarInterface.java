package com.Interface;

import com.Util.RadarInfo;

public interface RadarInterface {

	public void updateRadarData(RadarInfo ri,boolean front);
	
	public void closeActivity();
}
