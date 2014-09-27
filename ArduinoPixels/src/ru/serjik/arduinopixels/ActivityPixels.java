package ru.serjik.arduinopixels;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ActivityPixels extends Activity
{
	private static final String TAG = "ActivityPixels";

	private ListView listPrograms;
	private List<String> names;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pixels);
		listPrograms = (ListView) findViewById(R.id.list_programs);
	}

	@Override
	protected void onResume()
	{
		names = app.cfg.names("// ");
		Collections.sort(names);
		listPrograms.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names));
		listPrograms.setOnItemClickListener(onItemClickListener);
		super.onResume();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_pixels, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		if (item.getItemId() == R.id.action_new_program)
		{
			String name = "// " + android.text.format.DateFormat.format("dd.MM.yyyy HH:mm:ss", new java.util.Date());
			open(name);
			return true;
		}

		if (item.getItemId() == R.id.action_pick_bluetooth_device)
		{
			startActivity(BluetoothDevicePicker.intent());
			return true;
		}
		
		if(item.getItemId() == R.id.action_export)
		{
			
		}

		// TODO Auto-generated method stub
		return super.onMenuItemSelected(featureId, item);
	}

	private void open(String name)
	{
		startActivity(ActivityProgram.intent(this, name));
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			open("// " + names.get(arg2));
		}
	};

}
