package ru.serjik.parser;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Decompiler
{
	private static final String[] functions = { "", "add", "sub", "mul", "div", "neg", "sin", "cos", "exp", "loop",
			"sqrt", "delay", "time", "rnd", "pow" };

	public static String decode(byte[] bc) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		sb.setLength(0);
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bc));

		while (dis.available() > 0)
		{
			int b = (int) dis.readByte();

			if (b == (byte) 0xc0)
			{
				sb.append("push ");
				sb.append(dis.readFloat());
				sb.append('\n');
				continue;
			}

			if ((b & (byte) 0x40) > 0)
			{
				sb.append("push ");
				sb.append("" + (char) ('a' + (char) (b & 0x1f)));
				sb.append('\n');
				continue;
			}

			if ((b & 0x80) > 0)
			{
				sb.append("pop ");
				sb.append("" + (char) ('a' + (char) (b & 0x1f)));
				sb.append('\n');
				continue;
			}

			if (b > 0 && b < 15)
			{
				sb.append(functions[b] + " (code = " + b + ")");
				sb.append('\n');
				continue;
			}

			throw new Exception("incorrect code " + b);
		}

		return sb.toString();
	}
}
