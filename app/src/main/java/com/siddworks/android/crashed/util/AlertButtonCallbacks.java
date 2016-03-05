package com.siddworks.android.crashed.util;

public interface AlertButtonCallbacks {
	public void alertDialogPositiveButtonPressed(String requestTag);
	public void alertDialogNegativeButtonPressed(String requestTag);
	public void alertDialogNeutralButtonPressed(String requestTag);
}
