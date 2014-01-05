package ru.serjik.arduinopixels;

import java.io.ByteArrayOutputStream;

import ru.serjik.parser.Evaluator;
import ru.serjik.parser.Tokenizer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityFormula extends Activity
{
	private EditText editProg;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_formula);

		editProg = (EditText) findViewById(R.id.edit_program);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_formula, menu);
		return true;
	}

	public void onFormatClick(View view)
	{
		try
		{
			Tokenizer tokenizer = new Tokenizer();
			editProg.setText(tokenizer.format(editProg.getText().toString()));
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
			editProg.setText(tokenizer.format(editProg.getText().toString()));
			
			Evaluator eval = new Evaluator(tokenizer);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
			
			baos.write(0x04);
			baos.write(eval.getByteCode());
			app.send(baos.toByteArray());
		}
		catch (Exception e)
		{
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public static Intent intent(Context context)
	{
		return new Intent(context, ActivityFormula.class);
	}

}
