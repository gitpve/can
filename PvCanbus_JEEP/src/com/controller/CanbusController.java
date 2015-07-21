package com.controller;

import com.Canbus.jeep.CanbusService;
import com.Interface.CanbusInterface;
import com.Interface.CanbusListener;
import com.Interface.IParse;
import com.Util.LogUtil;

public class CanbusController implements IParse {

	private static CanbusInterface minter;
	private static CanbusController controller = new CanbusController();

	protected CanbusListener mListener;

	private CanbusController() {
		minter = new CanbusInterface();
	}

	public static CanbusController getController() {
		return controller;
	}

	public boolean open() {
		LogUtil.Log4d("---CanbusFAM---", "CanTeanaController.opendevice");
		if (minter != null) {
			minter.setListener(this);
			minter.openDevice();
			minter.setCallBack();
			connect();
		}
		return true;
	}

	public void connect() {
		if (minter == null)
			return;
		minter.startDevice();
	}

	public boolean close() {
		LogUtil.Log4d("---CanbusFAM---", "CanteanaController.closedevice");
		if (minter != null) {
			minter.resetListener();
			minter.closeDevice();
			minter.resetCallBack();
		}
		return true;
	}

	public void setListener(CanbusListener listener) {
		this.mListener = listener;
	}

	@Override
	public void parse(String str) {
   
		CanbusService.transData(str);
	}

	public void dashboardShow(byte[] firstByte, byte[] secondByte) {
		if (minter != null)
			minter.dashboardShow(firstByte, secondByte);
	}

	public void postKey(int key) {
		if (minter != null && key != 0)
			minter.postKey(key);
	}

	public void changeAVM(int index) {
		if (minter != null)
			minter.changeAVM(index);
	}

	public void setTime(int hour_format,int hour,int minute){
		if(minter!=null){
			LogUtil.Log4d("---canbusfam---", "settime:  "+hour+":"+minute);
			minter.setTime(hour_format, hour, minute);
		}
	}
	
	public void setFreq(int band,int freq){
		if(minter!=null){
			LogUtil.Log4d("---canbusfam---", "band=="+band+"--freq=="+freq);
			minter.setFreq(band, freq);
		}
	}
	
	
}
