package com.ping.activities;


import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.ping.R;
import com.ping.Values;
import com.ping.helpers.ServiceHelper;
import com.ping.helpers.ThreadPoolHelper;
import com.ping.listeners.BaseResponseListener;
import com.ping.models.Battery;
import com.ping.models.Device;
import com.ping.models.GPS;
import com.ping.models.MainModel;
import com.ping.models.Measurement;
import com.ping.models.Model;
import com.ping.models.Network;
import com.ping.models.Ping;
import com.ping.models.Sim;
import com.ping.models.Throughput;
import com.ping.models.Usage;
import com.ping.models.Wifi;
import com.ping.tasks.MeasurementTask;
import com.ping.ui.adapter.ListAdapter;


public class RunActivity extends BaseActivityGroup
{
	//private LinearLayout table;

	private ThreadPoolHelper serverhelper;
	private Values session = null;
	private Activity activity;
	private boolean firstPing=true;
	public String serviceTag = "PerformanceService";

	public ArrayList<Model> items;
	public ListView listview;
	public ListAdapter listadapter;

	Resources res;
	TabHost tabHost;
	TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	Intent intent;  // Reusable Intent for each tab
	public Button load;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manual_test);

		activity = this;

		serverhelper = new ThreadPoolHelper(5,10);


		ServiceHelper.processStopService(this,serviceTag);
		session = (Values) this.getApplicationContext();
		session.initDataStore();
		items = new ArrayList<Model>();
		//listadapter = new ListAdapter(activity,noteButton,R.layout.item_view,items);
		serverhelper.execute(new MeasurementTask(activity,true,true,true, new MeasurementListener()));
		//listview.setAdapter(listadapter);

		load = (Button) findViewById(R.id.load);
		res = getResources(); // Resource object to get Drawables
		tabHost =  (TabHost) findViewById(R.id.tabhost);
		tabHost.setup(this.getLocalActivityManager());


		// Create an Intent to launch an Activity for the tab (to be reused)


	}




	public class MeasurementListener extends BaseResponseListener{

		public void onCompletePing(Ping response) {

			//onCompleteOutput(response);
		}

		public void onCompleteDevice(Device response) {

			onCompleteOutput(response);
		}

		public void onCompleteMeasurement(Measurement response) {
			LoadBarHandler.sendEmptyMessage(0);
			onCompleteOutput(response);
		}

		public void onCompleteOutput(MainModel model){

			Message msg2=Message.obtain(UIHandler, 0, model);
			UIHandler.sendMessage(msg2);
		}

		public void onComplete(String response) {

		}

		public void onUpdateProgress(int val){
			Message msg=Message.obtain(progressBarHandler, 0, val);
			progressBarHandler.sendMessage(msg);
		}

		public void onCompleteGPS(GPS response) {
			onCompleteOutput(response);

		}

		public void makeToast(String text) {
			Message msg=Message.obtain(toastHandler, 0, text);
			toastHandler.sendMessage(msg);

		}

		public void onCompleteSignal(String signalStrength) {

		}
		public void onCompleteUsage(Usage response) {
			onCompleteOutput(response);

		}

		public void onCompleteThroughput(Throughput response) {
			onCompleteOutput(response);

		}

		public void onCompleteWifi(Wifi response) {
			onCompleteOutput(response);

		}

		public void onCompleteBattery(Battery response) {
			onCompleteOutput(response);

		}

		public void onCompleteNetwork(Network response) {
			onCompleteOutput(response);

		}

		public void onCompleteSIM(Sim response) {
			//onCompleteOutput(response);

		}

		public void onCompleteSummary(JSONObject Object) {
			// TODO Auto-generated method stub

		}

		public void onFail(String response) {
			// TODO Auto-generated method stub

		}
	}

	private Handler toastHandler = new Handler() {
		public void  handleMessage(Message msg) {
			try {
				String text = (String)msg.obj;
				//Toast.makeText(activity, text, 1000);
				Toast.makeText(RunActivity.this, text, 1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};



	private Handler UIHandler = new Handler(){
		public void  handleMessage(Message msg) {

			MainModel item = (MainModel)msg.obj;
			items.add(item);
			/*listadapter.add(item);			
			listadapter.notifyDataSetChanged();*/
			intent = new Intent().setClass(activity, DisplayActivity.class);

			session.storeModel(item);

			intent.putExtra("model_key",item.getTitle());
			
			View tabview = createTabView(tabHost.getContext(),item);
			
			spec = tabHost.newTabSpec(item.getTitle()).setIndicator(tabview)
					.setContent(intent);
			tabHost.addTab(spec);

		}
	};

	private Handler LoadBarHandler = new Handler(){
		public void  handleMessage(Message msg) {
			load.setVisibility(View.GONE);
			ServiceHelper.processStopService(activity,"com.android.services.PerformanceService");
			ServiceHelper.processStartService(activity,"com.android.services.PerformanceService");
		}
	};

	private Handler progressBarHandler = new Handler() {

		public void  handleMessage(Message msg) {
			try {
				int value=(Integer)msg.obj;
				//progressBar.setProgress(value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private static View createTabView(final Context context, Model item) {

		View view = LayoutInflater.from(context).inflate(R.layout.tabs_hg, null);

		ImageView tv = (ImageView) view.findViewById(R.id.icon);

		tv.setImageResource(item.getIcon());

		return view;

	}


}