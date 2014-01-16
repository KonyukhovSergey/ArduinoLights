package ru.serjik.arduinopixels;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

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

			String defaultProgram = "\n// keywords: loop, call label, ret, goto label, if ... then ... endif; end;\n"+ 
	                   "// math: sin(x), cos(x), exp(x), sqrt(x), pow(x, y)\n" +
	                   "// label: 'identifier:'\n" +
	                   "// system: delay(milliseconds); rnd() returned [0..1]; set(i,r,g,b);\n" +
	                   "// loop: one draw cicle\n" +
	                   "pos = 0;color = 0;" +
	                   "r = 255; g = 0; b = 0;" +
	                   "loop;" +
	                   "set(pos,r,g,b);delay(1);" +
	                   "pos=pos+1;" +
	                   "if pos > 49 then pos = 0;" +
	                   "color=color+1;if color>3 then color = 0;endif;"+
	                   "if color==0 then r=0;b=0;b=255;endif;" +
	                   "if color==1 then r=0;g=255;b=0;endif;" +
	                   "if color==2 then r=255;g=0;b=0;endif;" +
	                   "if color==3 then r=255;g=255;b=255;endif;" +
	                   "endif;" +
	                   "end";
			
			app.cfg.set(name, defaultProgram);
			open(name);
			return true;
		}

		if (item.getItemId() == R.id.action_pick_bluetooth_device)
		{
			startActivity(BluetoothDevicePicker.intent());
			return true;
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
