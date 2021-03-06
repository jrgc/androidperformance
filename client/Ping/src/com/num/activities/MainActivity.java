package com.num.activities;

import java.util.ArrayList;
import java.util.HashMap;

import com.num.R;
import com.num.Values;
import com.num.database.DatabasePicker;
import com.num.database.datasource.LatencyDataSource;
import com.num.database.mapping.LatencyMapping;
import com.num.helpers.GAnalytics;
import com.num.helpers.ServiceHelper;
import com.num.helpers.ThreadPoolHelper;
import com.num.listeners.FakeListener;
import com.num.models.ActivityItem;
import com.num.models.Row;
import com.num.tasks.SignalStrengthTask;
import com.num.tasks.ValuesTask;
import com.num.ui.UIUtil;
import com.num.ui.adapter.ItemAdapter;
import com.num.utils.DeviceUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ListView;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity {
	private ListView listview;
	// private TextView tv;
	private Activity activity;
	private ThreadPoolHelper serverhelper;
	private Values session = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		activity = this;
		session = (Values) getApplicationContext();
		session.loadValues();
		listview = (ListView) findViewById(R.id.main_list_view);

		serverhelper = new ThreadPoolHelper(10, 30);

		serverhelper.execute(new ValuesTask(this, new FakeListener()));
		ServiceHelper.processRestartService(this);
		ArrayList<Row> cells = new ArrayList<Row>();

		cells.add(new Row(new ActivityItem("Speed Test",
				"Get Down/Up speed, 40 sec", new Handler() {
					public void handleMessage(Message msg) {
						Intent myIntent = new Intent(activity,
								FullDisplayActivity.class);
						myIntent.putExtra("model_key", "throughput");
						myIntent.putExtra("time", "45");
						startActivity(myIntent);
						GAnalytics.log(GAnalytics.ACTION, "Click", "Speed Test");
					}

				}, R.drawable.throughput)));
		cells.add(new Row(new ActivityItem("Application Usage",
				"Get data breakdown by app", new Handler() {
					public void handleMessage(Message msg) {
						Intent myIntent = new Intent(activity,
								FullDisplayActivity.class);
						myIntent.putExtra("model_key", "usage");
						startActivity(myIntent);
					}
				}, R.drawable.usage)));
		cells.add(new Row(new ActivityItem("Latency",
				"Get time to reach server, 5 sec", new Handler() {
					public void handleMessage(Message msg) {
						Intent myIntent = new Intent(activity,
								FullDisplayActivity.class);
						myIntent.putExtra("model_key", "latency");
						myIntent.putExtra("time", "15");
						startActivity(myIntent);
					}

				}, R.drawable.stopwatch)));

		cells.add(new Row(new ActivityItem("Configure", "Change preference",
				new Handler() {
					public void handleMessage(Message msg) {
						Intent myIntent = new Intent(activity,
								SettingsActivity.class);
						myIntent.putExtra("force", true);
						startActivity(myIntent);
					}

				}, R.drawable.configure)));

		cells.add(new Row(new ActivityItem("About Us",
				"Read about this project", new Handler() {
					public void handleMessage(Message msg) {
						Intent myIntent = new Intent(activity,
								AboutUsActivity.class);
						startActivity(myIntent);
					}

				}, R.drawable.team)));

		if (session.DEBUG == true) {
			cells.add(new Row(new ActivityItem("Graphing", "Quick Graph",
					new Handler() {
						public void handleMessage(Message msg) {

							DatabasePicker picker = session
									.createPicker(new LatencyDataSource(
											activity));
							picker.setTitle("Latency Graph");
							picker.filterBy(LatencyMapping.COLUMN_TYPE, "ping",
									"Type");
							picker.filterBy(LatencyMapping.COLUMN_DSTIP,
									"gsoogle", "Destination");
							picker.filterBy(LatencyMapping.COLUMN_MEASUREMENT,
									"average", "Metric");
							picker.filterBy(LatencyMapping.COLUMN_CONNECTION,
									DeviceUtil.getNetworkInfo(activity),
									"Connection");
							Intent myIntent = new Intent(activity,
									GraphActivity.class);
							activity.startActivity(myIntent);

						}

					}, R.drawable.measure)));
		}
		if (session.DEBUG == true) {
			cells.add(new Row(new ActivityItem("TraceRoute",
					"Trace hops to Georgia Tech server", new Handler() {
						public void handleMessage(Message msg) {
							Intent myIntent = new Intent(activity,
									FullDisplayActivity.class);
							myIntent.putExtra("model_key", "traceroute");
							startActivity(myIntent);
						}

					}, R.drawable.team)));
		}

		if (session.DEBUG == true) {
			cells.add(new Row(new ActivityItem("Signal Strength",
					"Signal Strength debugging", new Handler() {
						public void handleMessage(Message msg) {
							ThreadPoolHelper serverhelper = new ThreadPoolHelper(
									Values.THREADPOOL_MAX_SIZE,
									Values.THREADPOOL_KEEPALIVE_SEC);
							serverhelper.executeOnUIThread(activity,
									new SignalStrengthTask(activity,
											new HashMap<String, String>(),
											new FakeListener()));
						}
					}, R.drawable.team)));
		}

		ItemAdapter itemadapter = new ItemAdapter(activity, cells);
		for (Row cell : cells)
			itemadapter.add(cell);
		listview.setAdapter(itemadapter);
		itemadapter.notifyDataSetChanged();
		UIUtil.setListViewHeightBasedOnChildren(listview, itemadapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
