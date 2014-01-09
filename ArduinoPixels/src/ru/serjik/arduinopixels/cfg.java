package ru.serjik.arduinopixels;

import android.content.Context;

public class cfg extends ApplicationPreferencesBase
{
	public final static String DEVICE_MAC_ADDRESS = "DeviceMacAdress;null";
	public final static String PROGRAM = "program;t=t+0.1;loop;r=255;g=255;b=255;end";

	public cfg(Context context, String name)
	{
		super(context, name);
	}

}
