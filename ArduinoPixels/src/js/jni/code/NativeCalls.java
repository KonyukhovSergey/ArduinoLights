package js.jni.code;

public class NativeCalls
{
	native public int init(byte[] byteCode);

	native public int exec(byte[] frameBuffer);

	static
	{
		System.loadLibrary("LightMachine");
	}
}
