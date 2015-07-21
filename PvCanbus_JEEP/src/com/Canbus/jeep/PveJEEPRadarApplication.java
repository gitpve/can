package com.Canbus.jeep;

import android.app.Application;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.Interface.RadarInterface;
import com.Util.RadarInfo;

public class PveJEEPRadarApplication extends Application implements RadarInterface{
	private static PveJEEPRadarApplication sMe;
	private WindowManager mWindowManager = null;
	private View mViews = null;
	private WindowManager.LayoutParams mLayoutParams = null;
	private ImageView ivRadarF_L;
	private ImageView ivRadarF_LF;
	private ImageView ivRadarF_RF;
	private ImageView ivRadarF_R;
	private ImageView ivRadarB_L;
	private ImageView ivRadarB_LB;
	private ImageView ivRadarB_RB;
	private ImageView ivRadarB_R;

	public PveJEEPRadarApplication() {
		sMe = this;
	}

	public static PveJEEPRadarApplication getInstance() {
		return sMe;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		initFloatingFrame();
		setFrameShowState(1);
		super.onCreate();
	}

	@SuppressWarnings("deprecation")
	private void initFloatingFrame() {
		mWindowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		int height = mWindowManager.getDefaultDisplay().getHeight();
		mLayoutParams = new LayoutParams();
		mLayoutParams.width = 350; // 宽度
		mLayoutParams.height = 500; // 高度
		mLayoutParams.x = 30;
		mLayoutParams.y = height - 480;

		mLayoutParams.alpha = 0.95f; // 透明度
		mLayoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
		mLayoutParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
		mLayoutParams.format = PixelFormat.TRANSLUCENT;
		mLayoutParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL;
		mViews = LayoutInflater.from(this).inflate(R.layout.radar_info, null);

		ivRadarB_L = (ImageView) mViews.findViewById(R.id.radarB_L);
		ivRadarB_LB = (ImageView) mViews.findViewById(R.id.radarB_LB);
		ivRadarB_RB = (ImageView) mViews.findViewById(R.id.radarB_RB);
		ivRadarB_R = (ImageView) mViews.findViewById(R.id.radarB_R);
		ivRadarF_L = (ImageView) mViews.findViewById(R.id.radarF_L);
		ivRadarF_LF = (ImageView) mViews.findViewById(R.id.radarF_LF);
		ivRadarF_RF = (ImageView) mViews.findViewById(R.id.radarF_RF);
		ivRadarF_R = (ImageView) mViews.findViewById(R.id.radarF_R);

		RelativeLayout mView = (RelativeLayout) mViews.findViewById(R.id.rlRadar);
		mView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_MOVE == event.getAction()) {
					mLayoutParams.x = ((int) (event.getRawX())) - 150;
					try {
						mWindowManager.updateViewLayout(mViews, mLayoutParams);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				return true;
			}
		});

	}

	public void showFloatingFrame() {
		log_d("showFloatingFrame");
		CanbusTransData.getTransData().setRadarListener(this);
		updateDatas();
		if (getFrameShowState() == 1) {
			try {
				mWindowManager.addView(mViews, mLayoutParams);
				setFrameShowState(0);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	public void removeFloatingFrame() {
		if ((mWindowManager != null) && (mViews != null) && (getFrameShowState() == 0)) {
			try {
				log_d("removeFloatingFrame");
				mWindowManager.removeView(mViews);
				setFrameShowState(1);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	private void log_d(String msg) {
		Log.d("RadarHondaAccord", msg);
	}

	private void setFrameShowState(int value) {
		android.provider.Settings.System.putInt(getContentResolver(), "pve_radar_honda_show", value);
	}

	private int getFrameShowState() {
		return android.provider.Settings.System.getInt(getContentResolver(), "pve_radar_honda_show", 1);// 0为显示，1为未显示
	}

	public void updateRadarData(RadarInfo ri, boolean front) {
		// TODO Auto-generated method stub
		updateRadarInfo(ri, front);
	}

	private void updateDatas() {
		updateRadarInfo(CanbusService.getRadarInfoFront(), CanbusService.getRadarInfoBack());//radar_front set null
		}

	private void updateRadarInfo(RadarInfo riFront, RadarInfo riBack) {
		if ((riFront == null && riBack == null) || (!riFront.bShow && !riBack.bShow)) {
			ivRadarF_L.setImageResource(getImageResId(1, 0));
			ivRadarF_LF.setImageResource(getImageResId(2, 0));
			ivRadarF_RF.setImageResource(getImageResId(4, 0));
			ivRadarF_R.setImageResource(getImageResId(3, 0));
			ivRadarB_L.setImageResource(getImageResId(3, 0));
			ivRadarB_LB.setImageResource(getImageResId(4, 0));
			ivRadarB_RB.setImageResource(getImageResId(2, 0));
			ivRadarB_R.setImageResource(getImageResId(1, 0));

		} else {
			int iFL = riFront.left;
			int iFLM = riFront.left_midd;
			int iFRM = riFront.right_midd;
			int iFR = riFront.right;
			int iBL = riBack.left;
			int iBLM = riBack.left_midd;
			int iBRM = riBack.right_midd;
			int iBR = riBack.right;
			Log.d("#RadarActivity#", " Radarinfo data:FL=" + iFL + ",FLM=" + iFLM + ",FRM=" + iFRM + ",FR=" + iFR
					+ "'BL=" + iBL + ",BLM=" + iBLM + ",BRM=" + iBRM + ",BR=" + iBR);

			ivRadarF_L.setImageResource(getImageResId(1, iFL));
			ivRadarF_LF.setImageResource(getImageResId(2, iFLM));
			ivRadarF_RF.setImageResource(getImageResId(4, iFRM));
			ivRadarF_R.setImageResource(getImageResId(3, iFR));
			ivRadarB_L.setImageResource(getImageResId(3, iBL));
			ivRadarB_LB.setImageResource(getImageResId(4, iBLM));
			ivRadarB_RB.setImageResource(getImageResId(2, iBRM));
			ivRadarB_R.setImageResource(getImageResId(1, iBR));
		}
	}

	private void updateRadarInfo(RadarInfo ri, boolean front) {
		if (ri == null || !ri.bShow) {
			return;
		}
		if (front == true) {
			Log.d("RadarActivity", "FrontRadar info:L=" + ri.left + ",LB=" + ri.left_midd + ",RB=" + ri.right_midd
					+ ",B=" + ri.right);
			ivRadarF_L.setImageResource(getImageResId(1, ri.left));
			ivRadarF_LF.setImageResource(getImageResId(2, ri.left_midd));
			ivRadarF_RF.setImageResource(getImageResId(4, ri.right_midd));
			ivRadarF_R.setImageResource(getImageResId(3, ri.right));
		} else {
			Log.d("RadarActivity", "BackRadar info:L=" + ri.left + ",LB=" + ri.left_midd + ",RB=" + ri.right_midd
					+ ",B=" + ri.right);
			ivRadarB_L.setImageResource(getImageResId(3, ri.left));
			ivRadarB_LB.setImageResource(getImageResId(4, ri.left_midd));
			ivRadarB_RB.setImageResource(getImageResId(2, ri.right_midd));
			ivRadarB_R.setImageResource(getImageResId(1, ri.right));
		}
	}

	private int getImageResId(int sideId, int level) {
		int resId = 0;
		switch (sideId) {
		case 1: {
			resId = getImageId1(level);
		}
			break;
		case 2: {
			resId = getImageId2(level);
		}
			break;
		case 3: {
			resId = getImageId3(level);
		}
			break;
		case 4: {
			resId = getImageId4(level);
		}
			break;

		default:
			break;
		}
		return resId;
	}

	private int getImageId1(int level) {
		int resId = 0;
		if (level > 4)
			return R.drawable.radar1_level10;
		switch (level) {
		case 0: {
			resId = R.drawable.radar1_level0;
		}
			break;
		case 1: {
			resId = R.drawable.radar1_level1;
		}
			break;
		case 2: {
			resId = R.drawable.radar1_level5;
		}
			break;
		case 3: {
			resId = R.drawable.radar1_level8;
		}
			break;
		case 4: {
			resId = R.drawable.radar1_level10;
		}
			break;
		default: {
			resId = R.drawable.radar1_level0;
		}
			break;
		}
		return resId;
	}

	private int getImageId2(int level) {
		int resId = 0;
		if (level > 4)
			return R.drawable.radar2_level10;
		switch (level) {
		case 0: {
			resId = R.drawable.radar2_level0;
		}
			break;
		case 1: {
			resId = R.drawable.radar2_level1;
		}
			break;
		case 2: {
			resId = R.drawable.radar2_level5;
		}
			break;
		case 3: {
			resId = R.drawable.radar2_level8;
		}
			break;
		case 4: {
			resId = R.drawable.radar2_level10;
		}
			break;
		default: {
			resId = R.drawable.radar2_level0;
		}
			break;
		}
		return resId;
	}

	private int getImageId3(int level) {
		int resId = 0;
		if (level > 4)
			return R.drawable.radar3_level10;
		switch (level) {
		case 0: {
			resId = R.drawable.radar3_level0;
		}
			break;
		case 1: {
			resId = R.drawable.radar3_level1;
		}
			break;
		case 2: {
			resId = R.drawable.radar3_level5;
		}
			break;
		case 3: {
			resId = R.drawable.radar3_level8;
		}
			break;
		case 4: {
			resId = R.drawable.radar3_level10;
		}
			break;
		default: {
			resId = R.drawable.radar3_level0;
		}
			break;
		}
		return resId;
	}

	private int getImageId4(int level) {
		int resId = 0;
		if (level > 4)
			return R.drawable.radar4_level10;
		switch (level) {
		case 0: {
			resId = R.drawable.radar4_level0;
		}
			break;
		case 1: {
			resId = R.drawable.radar4_level1;
		}
			break;
		case 2: {
			resId = R.drawable.radar4_level5;
		}
			break;
		case 3: {
			resId = R.drawable.radar4_level8;
		}
			break;
		case 4: {
			resId = R.drawable.radar4_level10;
		}
			break;
		default: {
			resId = R.drawable.radar4_level0;
		}
			break;
		}
		return resId;
	}

	@Override
	public void closeActivity() {

		removeFloatingFrame();
		CanbusTransData.getTransData().releaseRadarListener();
	}
}
