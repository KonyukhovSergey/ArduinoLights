package js.jni.code;

public class NativeCalls
{
	native public static int init(byte[] byteCode);

	native public static int tick();

	native public static int draw(int[] frameBuffer);

	static
	{
		System.loadLibrary("LightMachine");
	}
}
