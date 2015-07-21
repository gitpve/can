package com.ContentObserver;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

public class PveContentObserver extends ContentObserver{
	
	
	private static final int POWER_ON=3;
	private static final int BACK_ON=1;
	private static final int PHONE_TALK=1;
	
	private static final Uri URI_POWER=Uri.parse("content://com.pve.car.spy/power");
	private static final Uri UIR_BACK=Uri.parse("content://com.pve.car.spy/back");
	private static final Uri URI_PHONE=Uri.parse("content://com.pve.car.spy/phone");
	
	private Context mContext;
	private ContentResolver mContentResolver;
	
	
	
	public PveContentObserver(Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
	}
	
	public void setContext(Context context){
		mContext=context;
		RegisterUri();
	}
	
	private void RegisterUri() {
		if(mContext!=null){
			mContentResolver=mContext.getContentResolver();
		}
		
		if(mContentResolver==null)
			return;
		mContentResolver.registerContentObserver(URI_PHONE, true, this);
		mContentResolver.registerContentObserver(URI_POWER, true, this);
		mContentResolver.registerContentObserver(UIR_BACK, true, this);
	}

	
//	@Override
//	public void onChange(boolean selfChange) {
////		queryPhone();
////		queryBack();
////		queryPower();
//	}
	
	
	public boolean queryPhone(){
		boolean bRet=false;
		Cursor cursor=mContentResolver.query(URI_PHONE, null, null, null, null);
		if(cursor!=null){
			int phoneStatus=getDataFromCursor(cursor);
			if(phoneStatus==PHONE_TALK){
				bRet=true;
			}else{
				bRet=false;
			}
			cursor.close();
		}
		return bRet;
	}
	
	public boolean queryBack(){
		boolean bRet=false;
		Cursor cursor=mContentResolver.query(UIR_BACK, null, null, null, null);
		if(cursor!=null){
			int backStatus=getDataFromCursor(cursor);
			if(backStatus==BACK_ON){
				bRet=true;
			}else{
				bRet=false;
			}
			cursor.close();
		}
		return bRet;
	}
	
	public boolean queryPower(){
		boolean bRet=false;
		Cursor cursor=mContentResolver.query(URI_POWER, null, null, null, null);
		if(cursor!=null){
			int powerStatus=getDataFromCursor(cursor);
			if(powerStatus==POWER_ON){
				bRet=true;
			}else{
				bRet=false;
			}
			cursor.close();
		}
		return bRet;
	}
	
	
	private int getDataFromCursor(Cursor cursor){
		int iData=0;
		String[] str=new String[cursor.getColumnCount()];
		for (int i = 0; i < str.length; i++) {
			str[i]=cursor.getColumnName(i);
		}
		while(cursor.moveToNext()){
			for (int j= 0; j< str.length; j++) {
				iData=cursor.getInt(cursor.getColumnIndex(str[j]));
			}
		}
		return iData;
	}

}
