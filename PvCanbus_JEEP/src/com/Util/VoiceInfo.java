package com.Util;

public class VoiceInfo {
	
	@Override
	public String toString() {
		return "VoiceInfo [volume=" + volume + ", bass=" + bass + ", middle="
				+ middle + ", treble=" + treble + ", balance=" + balance
				+ ", fader=" + fader + "]";
	}

	public int volume;
	public int bass;
	public int middle;
	public int treble;
	public int balance;
	public int fader;
	
	public void clear(){
		volume=0;
		bass=0;
		middle=0;
		treble=0;
		balance=0;
		fader=0;
	}

}
