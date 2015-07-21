package com.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.Canbus.jeep.CanbusService;
import com.Util.LogUtil;

public class BootReceiver extends BroadcastReceiver {

	private static final String action_boot = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {

		LogUtil.Log4d("Canbus", intent.getAction());
		if (intent.getAction().equals(action_boot)) {

			Intent mBootIntent = new Intent(context, CanbusService.class);
			mBootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(mBootIntent);
		}
	}
}
