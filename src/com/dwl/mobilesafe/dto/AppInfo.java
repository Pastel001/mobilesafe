package com.dwl.mobilesafe.dto;

import android.graphics.drawable.Drawable;

public class AppInfo {
	private Drawable icon;
	private String name;
	private boolean inRom;
	private boolean userApp;
	private String packageName;

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isInRom() {
		return inRom;
	}

	public void setInRom(boolean inRom) {
		this.inRom = inRom;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean isUserApp() {
		return userApp;
	}

	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}

	@Override
	public String toString() {
		return "AppInfo [icon=" + icon + ", name=" + name + ", inRom=" + inRom
				+ ", userApp=" + userApp + ", packageName=" + packageName + "]";
	}
}
