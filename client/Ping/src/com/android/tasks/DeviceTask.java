package com.android.tasks;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;

import com.android.helpers.DeviceHelper;
import com.android.helpers.PingHelper;
import com.android.listeners.ResponseListener;
import com.android.models.Measurement;
import com.android.models.Ping;
import com.android.utils.HTTPUtil;

/*
 * Measurement Task 
 * set tasks to run and give ip address to ping and more
 * 
 * Call another task to backend
 * 
 * 
 */
public class DeviceTask extends ServerTask{
	
	Measurement measurement;
	
	public DeviceTask(Context context, Map<String, String> reqParams, ResponseListener listener, Measurement measurement) {
		super(context, reqParams, listener);
		this.measurement = measurement;
	}

	@Override
	public void runTask() {
		
		measurement = DeviceHelper.deviceHelp(getContext(), measurement);
		this.getResponseListener().onCompleteMeasurement(measurement);
	}

	@Override
	public String toString() {
		return "Device Task";
	}
	

}
