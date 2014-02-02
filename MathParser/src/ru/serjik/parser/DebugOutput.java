package ru.serjik.parser;

public class DebugOutput
{
	public static boolean isDebugEnabled = false;

	public static final void println(String line)
	{
		if (isDebugEnabled)
		{
			System.out.println(line);
		}
	}
}
