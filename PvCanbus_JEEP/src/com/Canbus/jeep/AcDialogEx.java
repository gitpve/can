package com.Canbus.jeep;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Const.CanbusKeys;
import com.Util.AirInfo;
import com.Util.DoorInfo;

public class AcDialogEx {
	private Context mContext;
	private View mView;
	private Dialog mDialog;
	private Timer mTimer;
	private DoorInfo doorInfo;
	private AirInfo airInfo;
	private boolean showDialog;
	private boolean frontDoor;
	private boolean backDoor;
	private boolean frontLeftDoor;
	private boolean frontRightDoor;
	private boolean backLeftDoor;
	private boolean backRightDoor;

	private RelativeLayout carRLayout;
	private RelativeLayout airRLayout;
	private ImageView ivLevel;
	private ImageView ivCycle;
	private ImageView ivSupplyDefault;
	private ImageView ivSupplyUp;
	private ImageView ivSupplyDown;
	private ImageView ivLeftSeat;
	private ImageView ivRightSeat;
	private ImageView ivFrontClear;
	private ImageView ivBackClear;
	private ImageView ivAC_AUTO;
	private ImageView ivMax;
	private ImageView ivDaul;
	private ImageView ivRear;
	private ImageView ivLight1;
	private ImageView ivLight2;
	private ImageView ivFront;
	private ImageView ivBack;
	private ImageView ivRightFront;
	private ImageView ivRightBack;
	private ImageView ivLeftFront;
	private ImageView ivLeftBack;

	private TextView tvLeft;
	private TextView tvRight;

	public AcDialogEx(Context context) {
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = inflater.inflate(R.layout.canbox, null);
		initWidgets();
		mDialog = new Dialog(context, R.style.Theme_NoTitleBar_Light_Panel) {
			public boolean onTouchEvent(MotionEvent event) {

				if (isShowing()
						&& event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					mDialog.cancel();
					return true;
				}
				return false;
			}
		};

		mDialog.setContentView(mView);
		mDialog.setCanceledOnTouchOutside(true);
		Window window = mDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		LayoutParams lp = window.getAttributes();
		lp.token = null;
		lp.type = LayoutParams.TYPE_SYSTEM_ALERT;
		lp.width = LayoutParams.MATCH_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		window.setAttributes(lp);
		window.addFlags(LayoutParams.FLAG_NOT_FOCUSABLE
				| LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_TOUCHABLE);
	}

	private void initWidgets() {

		carRLayout = (RelativeLayout) mView.findViewById(R.id.car_info);
		airRLayout = (RelativeLayout) mView.findViewById(R.id.air_info);
		ivLevel = (ImageView) mView.findViewById(R.id.level);
		ivCycle = (ImageView) mView.findViewById(R.id.air_cycle);
		ivSupplyDefault = (ImageView) mView.findViewById(R.id.supply_default);
		ivSupplyDown = (ImageView) mView.findViewById(R.id.supply_down);
		ivSupplyUp = (ImageView) mView.findViewById(R.id.supply_up);
		ivLeftSeat = (ImageView) mView.findViewById(R.id.left_seat);
		ivRightSeat = (ImageView) mView.findViewById(R.id.right_seat);
		ivFrontClear = (ImageView) mView.findViewById(R.id.front_clear);
		ivBackClear = (ImageView) mView.findViewById(R.id.back_clear);
		ivAC_AUTO = (ImageView) mView.findViewById(R.id.ac_auto);
		ivMax = (ImageView) mView.findViewById(R.id.max);
		ivDaul = (ImageView) mView.findViewById(R.id.daul);
		ivRear = (ImageView) mView.findViewById(R.id.rear);
		ivLight1 = (ImageView) mView.findViewById(R.id.light1);
		ivLight2 = (ImageView) mView.findViewById(R.id.light2);
		ivFront = (ImageView) mView.findViewById(R.id.front);
		ivBack = (ImageView) mView.findViewById(R.id.back);
		ivRightFront = (ImageView) mView.findViewById(R.id.right_front);
		ivLeftFront = (ImageView) mView.findViewById(R.id.left_front);
		ivRightBack = (ImageView) mView.findViewById(R.id.right_back);
		ivLeftBack = (ImageView) mView.findViewById(R.id.left_back);
		tvLeft = (TextView) mView.findViewById(R.id.text_left);
		tvRight = (TextView) mView.findViewById(R.id.text_right);

	}

	protected void onPaint() {
		showDialog = true;
		UpdateAirInfo();
		UpDateCarInfo();
		if (airInfo.bShow) {
			carRLayout.setVisibility(View.GONE);
			airRLayout.setVisibility(View.VISIBLE);
		} else if (doorInfo.bShow) {
			carRLayout.setVisibility(View.VISIBLE);
			airRLayout.setVisibility(View.GONE);
		} else {
			showDialog = false;
		}

	}

	private void UpDateCarInfo() {
		doorInfo = CanbusService.getDoorInfo();
		if (doorInfo == null || !doorInfo.bShow)
			return;
		if (frontDoor == doorInfo.front && backDoor == doorInfo.back
				&& frontLeftDoor == doorInfo.left_front
				&& frontRightDoor == doorInfo.right_front
				&& backLeftDoor == doorInfo.left_back
				&& backRightDoor == doorInfo.right_back) {
			showDialog = false;
			doorInfo.bShow = false;
			return;
		}
		if (doorInfo.front)
			ivFront.setVisibility(View.VISIBLE);
		else
			ivFront.setVisibility(View.INVISIBLE);
		if (doorInfo.back)
			ivBack.setVisibility(View.VISIBLE);
		else
			ivFront.setVisibility(View.INVISIBLE);
		if (doorInfo.left_front)
			ivLeftFront.setVisibility(View.VISIBLE);
		else
			ivLeftFront.setVisibility(View.INVISIBLE);
		if (doorInfo.left_back)
			ivLeftBack.setVisibility(View.VISIBLE);
		else
			ivLeftBack.setVisibility(View.INVISIBLE);
		if (doorInfo.right_front)
			ivRightFront.setVisibility(View.VISIBLE);
		else
			ivRightFront.setVisibility(View.INVISIBLE);
		if (doorInfo.right_back)
			ivRightBack.setVisibility(View.VISIBLE);
		else
			ivRightBack.setVisibility(View.INVISIBLE);

		frontDoor = doorInfo.front;
		backDoor = doorInfo.back;
		frontLeftDoor = doorInfo.left_front;
		frontRightDoor = doorInfo.right_front;
		backLeftDoor = doorInfo.left_back;
		backRightDoor = doorInfo.right_back;
	}

	private void UpdateAirInfo() {
		airInfo = CanbusService.getAirInfo();
		if (airInfo == null || !airInfo.bShow) {
			return;
		}

		// if(airInfo.air_condition==CanbusKeys.AIR_CONDITIONING_OFF){
		// showDialog=false;
		// cancel();
		// }

		// if(airInfo.air_update==CanbusKeys.AIR_CONDITIONING_OFF){
		// airInfo.bShow=false;
		// return;
		// }

		updateRtp(airInfo.Rtp);
		updateLtp(airInfo.Ltp);
		updateWindLevel(airInfo.windlevle);
		updateAirCycle(airInfo.cycle);
		updateAC_Auto(airInfo);
		updateAirSupply(airInfo);
		updateBClear(airInfo.BClear);
		updateFClear(airInfo.FClear);
		updateLSeat(airInfo.LSeat);
		updateRSeat(airInfo.RSeat);

	}

	private void updateRtp(float rtp) {
		if (airInfo.unit == 0) {
			if (rtp <= CanbusKeys.TP_MIN)
				tvRight.setText("LO");
			else if (rtp >= CanbusKeys.TP_MAX)
				tvRight.setText("HI");
			else
				tvRight.setText(rtp
						+ mContext.getResources().getString(R.string.Celsius));
		} else {
			if (rtp < 60)
				tvRight.setText("LO");
			else if (rtp > 87)
				tvRight.setText("HI");
			else
				tvRight.setText((int) rtp
						+ mContext.getResources()
								.getString(R.string.Fahrenheit));

		}
	}

	private void updateLtp(float ltp) {
		if (airInfo.unit == 0) {
			if (ltp <= CanbusKeys.TP_MIN)
				tvLeft.setText("LO");
			else if (ltp >=CanbusKeys.TP_MAX)
				tvLeft.setText("HI");
			else
				tvLeft.setText(ltp
						+ mContext.getResources().getString(R.string.Celsius));
		} else {
			if (ltp < 60)
				tvLeft.setText("LO");
			else if (ltp > 87)
				tvLeft.setText("HI");
			else
				tvLeft.setText((int) ltp
						+ mContext.getResources()
								.getString(R.string.Fahrenheit));
		}
	}

	private void updateRSeat(int rseat) {
		if (ivRightSeat == null)
			return;
		int id = 0;
		ivRightSeat.setVisibility(View.VISIBLE);
		switch (rseat) {
		case CanbusKeys.SEAT_LEVEL0:
			ivRightSeat.setVisibility(View.INVISIBLE);
			break;
		case CanbusKeys.SEAT_LEVLE1:
			id = R.drawable.right_seat1;
			break;
		case CanbusKeys.SEAT_LEVEL2:
			id = R.drawable.right_seat2;
			break;
		case CanbusKeys.SEAT_LEVEL3:
			id = R.drawable.right_seat3;
			break;
		default:
			break;
		}
		if (id != 0)
			ivRightSeat.setBackgroundResource(id);
	}

	private void updateLSeat(int lseat) {
		if (ivLeftSeat == null)
			return;
		int id = 0;
		ivLeftSeat.setVisibility(View.VISIBLE);
		switch (lseat) {
		case CanbusKeys.SEAT_LEVEL0:
			ivLeftSeat.setVisibility(View.INVISIBLE);
			break;
		case CanbusKeys.SEAT_LEVLE1:
			id = R.drawable.left_seat1;
			break;
		case CanbusKeys.SEAT_LEVEL2:
			id = R.drawable.left_seat2;
			break;
		case CanbusKeys.SEAT_LEVEL3:
			id = R.drawable.left_seat3;
			break;
		default:
			break;
		}
		if (id != 0)
			ivLeftSeat.setBackgroundResource(id);
	}

	private void updateBClear(int bclear) {
		if (ivBackClear == null)
			return;
		if (bclear == CanbusKeys.AIR_CONDITIONING_ON)
			ivBackClear.setVisibility(View.VISIBLE);
		else
			ivBackClear.setVisibility(View.INVISIBLE);

	}

	private void updateFClear(int fclear) {
		if (ivFrontClear == null)
			return;
		if (fclear == CanbusKeys.AIR_CONDITIONING_ON)
			ivFrontClear.setVisibility(View.VISIBLE);
		else
			ivFrontClear.setVisibility(View.INVISIBLE);
	}

	private void updateAirSupply(AirInfo airInfo) {
		if (airInfo.supplyDefault == CanbusKeys.AIR_CONDITIONING_ON)
			ivSupplyDefault.setVisibility(View.VISIBLE);
		else
			ivSupplyDefault.setVisibility(View.INVISIBLE);
		if (airInfo.supplyUp == CanbusKeys.AIR_CONDITIONING_ON)
			ivSupplyUp.setVisibility(View.VISIBLE);
		else
			ivSupplyUp.setVisibility(View.INVISIBLE);
		if (airInfo.supplyDown == CanbusKeys.AIR_CONDITIONING_ON)
			ivSupplyDown.setVisibility(View.VISIBLE);
		else
			ivSupplyDown.setVisibility(View.INVISIBLE);

	}

	private void updateAC_Auto(AirInfo airInfo) {
		if (airInfo.ac == CanbusKeys.AIR_CONDITIONING_ON)
			ivAC_AUTO.setVisibility(View.VISIBLE);
		else
			ivAC_AUTO.setVisibility(View.INVISIBLE);
		if (airInfo.max == CanbusKeys.AIR_CONDITIONING_ON)
			ivMax.setVisibility(View.VISIBLE);
		else
			ivMax.setVisibility(View.INVISIBLE);
		if (airInfo.dual == CanbusKeys.AIR_CONDITIONING_ON)
			ivDaul.setVisibility(View.VISIBLE);
		else
			ivDaul.setVisibility(View.INVISIBLE);
		if (airInfo.rear == CanbusKeys.AIR_CONDITIONING_ON)
			ivRear.setVisibility(View.VISIBLE);
		else
			ivRear.setVisibility(View.INVISIBLE);
		if (airInfo.auto1 == CanbusKeys.AIR_CONDITIONING_ON)
			ivLight1.setVisibility(View.VISIBLE);
		else
			ivLight1.setVisibility(View.INVISIBLE);
		if (airInfo.auto2 == CanbusKeys.AIR_CONDITIONING_ON)
			ivLight2.setVisibility(View.VISIBLE);
		else
			ivLight2.setVisibility(View.INVISIBLE);
	}

	private void updateAirCycle(int cycle) {
		if (ivCycle == null)
			return;
		int id = 0;
		switch (cycle) {
		case CanbusKeys.AIR_CONDITIONING_AQS_CYCLE:
			id = R.drawable.cycle_aqs;
			break;
		case CanbusKeys.AIR_CONDITIONING_IN_CYCLE:
			id = R.drawable.cycle_in;
			break;
		case CanbusKeys.AIR_CONDITIONING_OUT_CYCLE:
			id = R.drawable.cycle_out;
			break;
		default:
			break;
		}

		if (id != R.drawable.cycle_aqs) {
			ivCycle.setVisibility(View.VISIBLE);
			ivCycle.setBackgroundResource(id);
		} else
			ivCycle.setVisibility(View.INVISIBLE);
	}

	private void updateWindLevel(int level) {
		if (ivLevel == null)
			return;
		int id = 0;
		switch (level) {
		case CanbusKeys.AIR_CONDITIONING_LEVEL0:
			id = R.drawable.level0;
			break;
		case CanbusKeys.AIR_CONDITIONING_LEVEL1:
			id = R.drawable.level1;
			break;
		case CanbusKeys.AIR_CONDITIONING_LEVEL2:
			id = R.drawable.level2;
			break;
		case CanbusKeys.AIR_CONDITIONING_LEVEL3:
			id = R.drawable.level3;
			break;
		case CanbusKeys.AIR_CONDITIONING_LEVEL4:
			id = R.drawable.level4;
			break;
		case CanbusKeys.AIR_CONDITIONING_LEVEL5:
			id = R.drawable.level5;
			break;
		case CanbusKeys.AIR_CONDITIONING_LEVEL6:
			id = R.drawable.level6;
			break;
		case CanbusKeys.AIR_CONDITIONING_LEVEL7:
			id = R.drawable.level7;
			break;

		default:
			id = R.drawable.level0;
			break;
		}
		if (id != 0)
			ivLevel.setBackgroundResource(id);
	}

	public void show() {
		onPaint();
		if (showDialog) {
			if (!mDialog.isShowing()) {
				mDialog.show();
			}
			onTimer();
		}
	}

	private void onTimer() {
		if (mTimer != null)
			mTimer.cancel();
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				end();
			}

			private void end() {
				mTimer.cancel();
				mDialog.cancel();
			}
		}, 3000);
	}

	public void cancel() {
		mDialog.cancel();
	}
}
