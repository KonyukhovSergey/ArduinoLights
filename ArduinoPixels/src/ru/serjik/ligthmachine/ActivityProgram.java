package ru.serjik.ligthmachine;

import java.io.IOException;

import ru.serjik.arduinopixels.R;
import ru.serjik.parser.ByteCodeGenerator;
import ru.serjik.parser.Tokenizer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ActivityProgram extends Activity
{
	private static final String PROGRAM_NAME = "programName";

	private EditText editProg;

	private LightSimulator simulator;

	private boolean isDeleteSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_program);

		editProg = (EditText) findViewById(R.id.edit_program);

		String prog = app.cfg.get(getIntent().getStringExtra(PROGRAM_NAME));

		if (prog.isEmpty())
		{
			prog = getIntent().getStringExtra(PROGRAM_NAME) + "\n"
					+ "// keywords: send, call label, ret, goto label, if ... then ... endif; end;\n"
					+ "// math: sin(x), cos(x), exp(x), sqrt(x), pow(x, y)\n" + "// label: 'identifier:'\n"
					+ "// system: delay(milliseconds); rnd() returned [0..1]; set(i,r,g,b);\n" + "pos = 0;color = 0;"
					+ "r = 255; g = 0; b = 0;" + "mainloop:;" + "set(pos,r,g,b);delay(1);" + "pos=pos+1;"
					+ "if pos > 49 then pos = 0;" + "color=color+1;if color>3 then color = 0;endif;"
					+ "if color==0 then r=0;b=0;b=255;endif;" + "if color==1 then r=0;g=255;b=0;endif;"
					+ "if color==2 then r=255;g=0;b=0;endif;" + "if color==3 then r=255;g=255;b=255;endif;" + "endif;"
					+ "send;goto mainloop;";
		}

		editProg.setText(prog);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_program, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.action_close:
			finish();
			return true;

		case R.id.action_delete:
			isDeleteSelected = true;
			finish();
			return true;

		default:
			break;
		}
		// TODO Auto-generated method stub
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onResume()
	{
		isDeleteSelected = false;
		onFormatClick(null);
		super.onResume();
	}

	private String getProg()
	{
		return editProg.getText().toString();
	}

	@Override
	protected void onPause()
	{
		if (isDeleteSelected)
		{
			app.cfg.remove(getIntent().getStringExtra(PROGRAM_NAME));
		}
		else
		{
			app.cfg.set(getProgramName(), getProg());
		}

		try
		{
			app.disconnect();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		if (simulator != null)
		{
			onSimulateClick(null);
		}

		super.onPause();
	}

	public void onFormatClick(View view)
	{
		try
		{
			Tokenizer tokenizer = new Tokenizer();
			tokenizer.tokenize(getProg());
			editProg.setText(SourceColorFormatter.format(tokenizer));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void onSimulateClick(View view)
	{
		if (simulator == null)
		{
			try
			{
				Tokenizer tokenizer = new Tokenizer();
				tokenizer.tokenize(getProg());
				ByteCodeGenerator eval = new ByteCodeGenerator(tokenizer);
				byte[] byteCode = eval.getByteCode();
				if (byteCode.length > 1023)
				{
					Toast.makeText(this, "error! the prog is to long. \n" + byteCode.length + " bytes as byte code",
							Toast.LENGTH_LONG).show();
				}
				else
				{
					simulator = new LightSimulator(this, byteCode);
					LinearLayout container = (LinearLayout) findViewById(R.id.layout_simulation);
					container.addView(simulator);
					container.setVisibility(View.VISIBLE);
					simulator.start();
					((Button) findViewById(R.id.button_simulate)).setText("stop");
				}
			}
			catch (Exception e)
			{
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
		else
		{
			LinearLayout container = (LinearLayout) findViewById(R.id.layout_simulation);
			simulator.stop();
			container.removeView(simulator);
			container.setVisibility(View.GONE);
			((Button) findViewById(R.id.button_simulate)).setText("simulate");
			simulator = null;
		}

	}

	public void onCompileAndSendClick(View view)
	{
		try
		{
			Tokenizer tokenizer = new Tokenizer();
			tokenizer.tokenize(getProg());

			app.connect();

			ByteCodeGenerator eval = new ByteCodeGenerator(tokenizer);
			byte[] byteCode = eval.getByteCode();
			if (byteCode.length > 1023)
			{
				Toast.makeText(this, "error! the prog is to long. \n" + byteCode.length + " bytes as byte code",
						Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(this, "sending " + byteCode.length + " bytes as byte code...", Toast.LENGTH_LONG).show();
				app.send(byteCode);
			}
		}
		catch (Exception e)
		{
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public static Intent intent(Context context, String programName)
	{
		Intent intent = new Intent(context, ActivityProgram.class);
		intent.putExtra(PROGRAM_NAME, programName);
		return intent;
	}

	private String getProgramName()
	{
		String prog = getProg();

		int pos = prog.indexOf('\n');

		if (pos > 0)
		{
			String name = prog.substring(0, pos);

			if (name.startsWith("// "))
			{
				return name;
			}
		}
		return getIntent().getStringExtra(PROGRAM_NAME);
	}

}
