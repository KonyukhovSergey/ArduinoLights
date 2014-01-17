package ru.serjik.arduinopixels;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ru.serjik.parser.ByteCodeGenerator;
import ru.serjik.parser.Tokenizer;
import android.os.Bundle;
import android.os.Looper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityProgram extends Activity
{
	private static final String PROGRAM_NAME = "programName";

	private EditText editProg;

	private boolean isDeleteSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_program);

		editProg = (EditText) findViewById(R.id.edit_program);

		String prog = app.cfg.get(getIntent().getStringExtra(PROGRAM_NAME));

		if (false == prog.startsWith(getIntent().getStringExtra(PROGRAM_NAME)))
		{
			prog = getIntent().getStringExtra(PROGRAM_NAME) + "\n" + prog;
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
		app.cfg.remove(getIntent().getStringExtra(PROGRAM_NAME));
		
		if (isDeleteSelected == false)
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
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void onCompileAndSendClick(View view)
	{
		try
		{
			Tokenizer tokenizer = new Tokenizer();
			tokenizer.tokenize(getProg());
			//editProg.setText(tokenizer.format());
			
			app.connect();

			app.cfg.set(getIntent().getStringExtra(PROGRAM_NAME), tokenizer.format());

			ByteCodeGenerator eval = new ByteCodeGenerator(tokenizer);

			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);

			byte[] byteCode = eval.getByteCode();

			if (byteCode.length < 1022)
			{
				Toast.makeText(this, "sending " + byteCode.length + " bytes as byte code...", Toast.LENGTH_LONG).show();
				baos.write(0x04);
				baos.write(byteCode);
				
				app.send(baos.toByteArray());
			}
			else
			{
				Toast.makeText(this, "error! the prog is to long. \n" + byteCode.length + " bytes as byte code",
						Toast.LENGTH_LONG).show();
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
