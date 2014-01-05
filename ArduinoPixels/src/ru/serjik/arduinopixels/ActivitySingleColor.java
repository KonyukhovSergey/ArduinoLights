package ru.serjik.arduinopixels;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ActivitySingleColor extends Activity {

	private SeekBar seekBarRed;
	private SeekBar seekBarGreen;
	private SeekBar seekBarBlue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_color);

		seekBarRed = (SeekBar) findViewById(R.id.seek_bar_red);
		seekBarGreen = (SeekBar) findViewById(R.id.seek_bar_green);
		seekBarBlue = (SeekBar) findViewById(R.id.seek_bar_blue);

		seekBarRed.setOnSeekBarChangeListener(onSeekBarChangeListener);
		seekBarGreen.setOnSeekBarChangeListener(onSeekBarChangeListener);
		seekBarBlue.setOnSeekBarChangeListener(onSeekBarChangeListener);
	}
	
	@Override
	protected void onResume() {
		onValuesUpdate();
		super.onResume();
	}

	private OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			onValuesUpdate();

		}
	};


	private void onValuesUpdate() {
		byte[] data = new byte[] { 0x01, (byte) seekBarRed.getProgress(), (byte) seekBarGreen.getProgress(),
				(byte) seekBarBlue.getProgress() };
		app.send(data);
	}

	public static Intent intent(Context context) {
		return new Intent(context, ActivitySingleColor.class);
	}

}
