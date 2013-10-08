package ru.jauseg.arduinolights;

import java.net.Socket;

import ru.jauseg.arduinolights.BluetoothDeviceReciever.OnBluetoothDeviceListener;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
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
	private BroadcastReceiver bluetoothDeviceReciever = new BluetoothDeviceReciever(this);

	private LinearLayout layoutConnectionState;
	private TextView textConnectionState;
	private Button buttonConnectSwitch;
	private boolean isConnected = false;

	private Socket deviceSocket = null;

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

			layoutConnectionState = (LinearLayout) findViewById(R.id.layout_connection_state);
			textConnectionState = (TextView) findViewById(R.id.text_connection_state);
			buttonConnectSwitch = (Button) findViewById(R.id.button_connect_switch);
			buttonConnectSwitch.setOnClickListener(onClickListener);

			registerReceiver(bluetoothDeviceReciever, new IntentFilter(BluetoothDevicePicker.ACTION_DEVICE_SELECTED));
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
		}
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		if (bluetoothAdapter != null)
		{
			buttonBluetoothSwitch.removeCallbacks(userInterfaceUpdaterRunnable);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		if (bluetoothAdapter != null)
		{
			unregisterReceiver(bluetoothDeviceReciever);
		}
		super.onDestroy();
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

				case R.id.button_connect_switch:
					onButtonConnectionClick();
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

				layoutConnectionState.setVisibility(View.VISIBLE);

				if (deviceSocket != null)
				{
					textConnectionState.setText("Connected");
					buttonConnectSwitch.setText("Disconnect");
				}
				else
				{
					textConnectionState.setText("Disconnected");
					buttonConnectSwitch.setText("Connect");
				}
			}
			else
			{
				layoutConnectionState.setVisibility(View.GONE);
			}
		}
		else
		{
			layoutBluetoothDevice.setVisibility(View.GONE);
			layoutConnectionState.setVisibility(View.GONE);
		}
	}

	protected void onButtonConnectionClick()
	{
		// TODO Auto-generated method stub

	}

	private void onButtonBluetoothDeviceClick()
	{
		startActivity(BluetoothDevicePicker.intent());
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
		bluetoothDevice = device;
		userInterfaceUpdate();
	}
}
