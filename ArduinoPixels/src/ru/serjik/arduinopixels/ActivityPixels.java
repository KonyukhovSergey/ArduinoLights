package ru.serjik.arduinopixels;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ActivityPixels extends Activity {

	private static final String TAG = "ActivityPixels";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pixels);

	}

	@Override
	protected void onDestroy() {
		// unregisterReceiver(bluetoothDeviceReciever);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_pixels, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.action_pick_bluetooth_device) {
			startActivity(BluetoothDevicePicker.intent());
			return true;
		}
		// TODO Auto-generated method stub
		return super.onMenuItemSelected(featureId, item);
	}

	public void onConnectClick(View view) {
		app.connect();
	}

	public void onDisconnectClick(View view) {
		app.disconnect();
	}

	public void onSingleColorClick(View view) {
		startActivity(ActivitySingleColor.intent(this));
	}

	public void onPointRGBClick(View view) {
		app.send(new byte[] { 0x02 });
	}

	public void onAuroraClick(View view) {
		app.send(new byte[] { 0x03 });
	}
	
	public void onProgramClick(View view) {
		startActivity(ActivityFormula.intent(this));
	}
}
