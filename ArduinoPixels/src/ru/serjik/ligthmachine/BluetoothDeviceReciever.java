package ru.serjik.ligthmachine;

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
	//	context.unregisterReceiver(this);
		
		if (BluetoothDevicePicker.ACTION_DEVICE_SELECTED.equals(intent.getAction()))
		{

			BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

			if (onBluetoothDeviceListener != null)
			{
				onBluetoothDeviceListener.onBluetoothDevice(device);
			}
			return;
		}

		if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(intent.getAction()))
		{
			// disconnect request
			return;
		}
		if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent.getAction()))
		{
			// disconnected, do what you want to notify user here, toast, or
			// dialog, etc.
			return;
		}
	}

	public interface OnBluetoothDeviceListener
	{
		void onBluetoothDevice(BluetoothDevice device);
		void onBluetoothDisconnected();
		void onBluetoothDisconnectRequest();
	}
}
