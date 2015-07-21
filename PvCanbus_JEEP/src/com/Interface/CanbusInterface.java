package com.Interface;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.Util.LogUtil;

public class CanbusInterface {
	private IParse mParse;
	
	private static final int callBackMessageType=0xff;
	
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case callBackMessageType:
				LogUtil.Log4d("debugInfo", "RegalInterface.handlerMessage");
				
				if(mParse!=null){
					mParse.parse(msg.getData().getString("callBackInfo"));
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		};
	};

	public void setListener(IParse parse){
		this.mParse=parse;
		setCallBack();
	}

	public void resetListener(){
		resetCallBack();
	}
	
	public CanbusInterface(){}
	
	public native int openDevice();
	
	public native int closeDevice();
	
	public native void setCallBack();
	
	public native void resetCallBack();
	
	public native void startDevice();
	
	public native void requestVersion();
	
	public native void dashboardShow(byte[] firstByte,byte[] secondByte);
	
	public native void postKey(int key);
	
	public native void setTime(int hour_format,int hour,int minute);
	
	public native void setFreq(int band,int Freq);
	
	public native void changeAVM(int index);
	
	public native void reqVersion();
	
	
	public void callBack(String result){
		Message msg=new Message();
		Bundle bundle=new Bundle();
		bundle.putString("callBackInfo", result);
		msg.setData(bundle);
		msg.what=callBackMessageType;
		handler.sendMessage(msg);
	}	
	
	static{
		System.loadLibrary("CanbusJEEP");
	}
}
