package com.Canbus.jeep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class CanbusActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent=new Intent(getApplicationContext(),CanbusService.class);
		startService(intent);
		finish();
		
	}
}
