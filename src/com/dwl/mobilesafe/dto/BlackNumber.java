package com.dwl.mobilesafe.dto;

public class BlackNumber {
	private String number;
	private String mode;

	public BlackNumber() {
		super();
	}

	public BlackNumber(String number, String mode) {
		super();
		this.number = number;
		this.mode = mode;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return "BlackNumber [number=" + number + ", mode=" + mode + "]";
	}
}
