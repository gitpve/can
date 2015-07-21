package com.Util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	
	public void getToast(Context context,String str){
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}

}
