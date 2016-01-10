package com.dwl.mobilesafe.dto;

import android.graphics.drawable.Drawable;

public class TaskInfo {
	private Drawable icon;
	private String name;
	private long memsize;
	private boolean userTask;
	private String packageName;
	private boolean checked;
	public Drawable getIcon() {
		return icon;
	}
	@Override
	public String toString() {
		return "TaskInfo [icon=" + icon + ", name=" + name + ", memsize="
				+ memsize + ", userTask=" + userTask + ", packageName="
				+ packageName + "]";
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
	public long getMemsize() {
		return memsize;
	}
	public void setMemsize(long memsize) {
		this.memsize = memsize;
	}
	public boolean isUserTask() {
		return userTask;
	}
	public void setUserTask(boolean userTask) {
		this.userTask = userTask;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
