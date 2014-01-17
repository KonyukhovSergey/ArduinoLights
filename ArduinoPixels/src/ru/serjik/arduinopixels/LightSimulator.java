package ru.serjik.arduinopixels;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class LightSimulator extends View
{
	private final static int LIGHT_COUNT = 50;
	private int[] pixels = new int[LIGHT_COUNT];

	private Paint paint;

	public LightSimulator(Context context)
	{
		super(context);
		paint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(Color.BLACK);

		float w = getWidth() / LIGHT_COUNT;

		for (int i = 0; i < LIGHT_COUNT; i++)
		{
			paint.setColor(pixels[i]);
			canvas.drawCircle(i * w + w * 0.5f, 0.5f * w, w * 0.4f, paint);
		}
	}
}
