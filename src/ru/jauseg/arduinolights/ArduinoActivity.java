package ru.jauseg.arduinolights;

import ru.jauseg.arduinolights.BluetoothDeviceReciever.OnBluetoothDeviceListener;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ArduinoActivity extends Activity implements OnBluetoothDeviceListener
{
	private static BluetoothAdapter bluetoothAdapter;
	private static BluetoothDevice bluetoothDevice = null;

	static
	{
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	private Button buttonBluetoothSwitch;
	private TextView textBluetoothState;
	private LinearLayout layoutBluetoothDevice;
	private TextView textBluetoothDevice;
	private Button buttonBluetoothDevice;
	private boolean isDeviceRecieverRegistered = false;
	private BroadcastReceiver bluetoothDeviceReciever = new BluetoothDeviceReciever(this);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if (bluetoothAdapter != null)
		{
			setContentView(R.layout.activity_arduino);

			buttonBluetoothSwitch = (Button) findViewById(R.id.button_bluetooth_switch);
			textBluetoothState = (TextView) findViewById(R.id.text_bluetooth_state);
			buttonBluetoothSwitch.setOnClickListener(onClickListener);

			layoutBluetoothDevice = (LinearLayout) findViewById(R.id.layout_bluetooth_device);
			textBluetoothDevice = (TextView) findViewById(R.id.text_bluetooth_device);
			buttonBluetoothDevice = (Button) findViewById(R.id.button_bluetooth_device);
			buttonBluetoothDevice.setOnClickListener(onClickListener);
		}
		else
		{
			setContentView(R.layout.activity_bluetooth_not_exist);
		}

	}

	@Override
	protected void onResume()
	{
		if (bluetoothAdapter != null)
		{
			buttonBluetoothSwitch.post(userInterfaceUpdaterRunnable);
			if (isDeviceRecieverRegistered)
			{
				unregisterReceiver(bluetoothDeviceReciever);
			}
		}
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		if (bluetoothAdapter != null)
		{
			buttonBluetoothSwitch.removeCallbacks(userInterfaceUpdaterRunnable);
			isDeviceRecieverRegistered = false;
		}
		super.onPause();
	}

	private Runnable userInterfaceUpdaterRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			userInterfaceUpdate();
			buttonBluetoothSwitch.postDelayed(this, 250);
		}
	};

	private OnClickListener onClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
				case R.id.button_bluetooth_switch:
					onButtonBluetoothSwitchClick();
					break;

				case R.id.button_bluetooth_device:
					onButtonBluetoothDeviceClick();
					break;

				default:
					break;
			}
		}
	};

	private void userInterfaceUpdate()
	{
		boolean isBluetoothEnabled = false;
		switch (bluetoothAdapter.getState())
		{
			case BluetoothAdapter.STATE_OFF:
				textBluetoothState.setText("OFF");
				buttonBluetoothSwitch.setText("Turn ON");
				buttonBluetoothSwitch.setEnabled(true);
				break;
			case BluetoothAdapter.STATE_ON:
				isBluetoothEnabled = true;
				textBluetoothState.setText("ON");
				buttonBluetoothSwitch.setText("Turn OFF");
				buttonBluetoothSwitch.setEnabled(true);
				break;
			case BluetoothAdapter.STATE_TURNING_OFF:
				textBluetoothState.setText("Turning OFF");
				buttonBluetoothSwitch.setText("Turn ON");
				buttonBluetoothSwitch.setEnabled(false);
				break;
			case BluetoothAdapter.STATE_TURNING_ON:
				textBluetoothState.setText("Turning ON");
				buttonBluetoothSwitch.setText("Turn ON");
				buttonBluetoothSwitch.setEnabled(false);
				break;
		}

		if (isBluetoothEnabled)
		{
			layoutBluetoothDevice.setVisibility(View.VISIBLE);

			if (bluetoothDevice != null)
			{
				textBluetoothDevice.setText(String.format("%s (%s)", bluetoothDevice.getName(),
						bluetoothDevice.getAddress()));
			}
		}
		else
		{
			layoutBluetoothDevice.setVisibility(View.GONE);
		}
	}

	private void onButtonBluetoothDeviceClick()
	{
		if (isDeviceRecieverRegistered)
		{

		}
		else
		{
			isDeviceRecieverRegistered = true;
			registerReceiver(bluetoothDeviceReciever, new IntentFilter(BluetoothDevicePicker.ACTION_DEVICE_SELECTED));
			startActivity(BluetoothDevicePicker.intent());
		}
	}

	private void onButtonBluetoothSwitchClick()
	{
		if (bluetoothAdapter.isEnabled())
		{
			bluetoothAdapter.disable();
		}
		else
		{
			bluetoothAdapter.enable();
		}
		userInterfaceUpdate();
	}

	@Override
	public void onBluetoothDevice(BluetoothDevice device)
	{
		isDeviceRecieverRegistered = false;
		bluetoothDevice = device;
		userInterfaceUpdate();
	}
}
