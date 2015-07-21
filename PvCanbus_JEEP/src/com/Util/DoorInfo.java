package com.Util;

public class DoorInfo {
	public boolean front;
	public boolean back;
	public boolean left_front;
	public boolean left_back;
	public boolean right_front;
	public boolean right_back;
	public boolean bShow;
	
	public void clear(){
		front=false;
		back=false;
		left_back=false;
		left_front=false;
		right_back=false;
		right_front=false;
		bShow=false;
	}
}
