package ru.jauseg.arduinolights;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothDeviceReciever extends BroadcastReceiver
{
	private OnBluetoothDeviceListener onBluetoothDeviceListener = null;

	public BluetoothDeviceReciever(OnBluetoothDeviceListener onBluetoothDeviceListener)
	{
		this.onBluetoothDeviceListener = onBluetoothDeviceListener;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (BluetoothDevicePicker.ACTION_DEVICE_SELECTED.equals(intent.getAction()))
		{
			context.unregisterReceiver(this);
			
			BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			
			if (onBluetoothDeviceListener != null)
			{
				onBluetoothDeviceListener.onBluetoothDevice(device);
			}
		}

	}

	public interface OnBluetoothDeviceListener
	{
		void onBluetoothDevice(BluetoothDevice device);
	}
}
