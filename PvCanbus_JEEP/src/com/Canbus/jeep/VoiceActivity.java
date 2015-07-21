package com.Canbus.jeep;

import com.Interface.VoiceInterface;
import com.Util.VoiceInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class VoiceActivity extends Activity implements VoiceInterface{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.voice_activity);
		
		Intent intent=new Intent(getApplicationContext(),CanbusService.class);
		startService(intent);
		
		
		
	}
	
	
	@Override
	public void updateVoice(VoiceInfo vi) {
		//
		//
		System.out.println(vi.toString());
	}
	
}
