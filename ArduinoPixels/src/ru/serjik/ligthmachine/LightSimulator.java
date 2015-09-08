package ru.serjik.ligthmachine;

import js.jni.code.NativeCalls;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.View;

public class LightSimulator extends View
{
	private final static int LIGHT_COUNT = 50;
	private int[] pixels = new int[LIGHT_COUNT];
	private byte[] byteCode = new byte[1024];

	private Paint paint;
	private volatile boolean started;

	public LightSimulator(Context context, byte[] byteCode)
	{
		super(context);
		paint = new Paint();
		System.arraycopy(byteCode, 0, this.byteCode, 0, byteCode.length);
		NativeCalls.init(this.byteCode);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		NativeCalls.draw(pixels);
		canvas.drawColor(Color.BLACK);

		float w = (float) getWidth() / (float) LIGHT_COUNT;

		for (int i = 0; i < LIGHT_COUNT; i++)
		{
			paint.setColor(pixels[i]);
			canvas.drawCircle(i * w + w * 0.5f, 0.5f * w, w * 0.45f, paint);
		}
		invalidate();
		// postInvalidateDelayed(50);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = width / LIGHT_COUNT;
		super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
	}

	public void start()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				started = true;
				while (started)
				{
					for (int i = 0; i < 1000; i++)
					{
						NativeCalls.tick();
					}
					SystemClock.sleep(10);
				}
			}
		}).start();
	}

	public void stop()
	{
		started = false;
	}

}
