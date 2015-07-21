package com.Util;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.Canbus.jeep.CanbusService;

public class SetMute {
	private static final String AUTHORITY="com.pve.car.spy";
	public static final Uri URI_MUTE=Uri.parse("content://"+AUTHORITY+"/mute");
	private int state=0;
	private DataTableObserver dto=new DataTableObserver(new Handler());
	private ContentResolver cr=null;
	
	public void setSlientMode(){
		cr=CanbusService.getContext().getContentResolver();
		cr.registerContentObserver(URI_MUTE, true, dto);
		
		Cursor cursor=cr.query(URI_MUTE, new String[]{"mute"}, null, null, null);
		ContentValues cv=new ContentValues();
		if(cursor!=null){
			cursor.moveToFirst();
			state=cursor.getInt(cursor.getColumnIndexOrThrow("mute"));
			cursor.close();
		}
		if(state==0){
			state=1;
			cv.put("mute", state);
			cr.update(URI_MUTE, cv, null, null);
		}else{
			state=0;
			cv.put("mute", state);
			cr.update(URI_MUTE, cv, null, null);
		}
	}
	
	
	public class DataTableObserver extends ContentObserver{

		public DataTableObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}
		
		public void onChange(boolean selfChange){
			Cursor cursor=cr.query(URI_MUTE, new String[]{"mute"}, null, null, null);
			
			if(cursor!=null){
				cursor.moveToFirst();
				state=cursor.getInt(cursor.getColumnIndexOrThrow("mute"));
				cursor.close();
			}
		}
	}
}
