package ru.serjik.light.parser;

public class DebugOutput
{
	public static boolean isDebugEnabled = true;

	public static final void println(String line)
	{
		if (isDebugEnabled)
		{
			System.out.println(line);
		}
	}
}
