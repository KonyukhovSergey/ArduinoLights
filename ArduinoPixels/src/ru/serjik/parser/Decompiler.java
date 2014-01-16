package ru.serjik.parser;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Decompiler
{
	private static final String[] functions = { "", "add", "sub", "mul", "div", "neg", "sin", "cos", "exp", "loop",
			"sqrt", "delay", "time", "rnd", "pow", "abs", "call", "ret", "jump", "jumpz", "end", "greater", "lower","eq","neq","set" };

	public static String decode(byte[] bc) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		sb.setLength(0);
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bc));

		int pos = -1;

		while (dis.available() > 0)
		{
			int b = (int) dis.readByte();
			pos++;

			if (b == (byte) 0xc0)
			{
				sb.append("" + pos + " " + "push ");
				sb.append(dis.readInt() + " ");
				pos += 4;
				sb.append('\n');
				continue;
			}

			if ((b & (byte) 0x40) > 0)
			{
				sb.append("" + pos + " " + "push v[");
				sb.append("" + (int) ((b & 0x3f)));
				sb.append("]\n");
				continue;
			}

			if ((b & 0x80) > 0)
			{
				sb.append("" + pos + " " + "pop v[");
				sb.append("" + (int) ((b & 0x3f)));
				sb.append("]\n");
				continue;
			}

			if (b > 0 && b < 64)
			{
				sb.append("" + pos + " " + functions[b] + " (code = " + b + ")");
				sb.append('\n');
				continue;
			}
			
			throw new Exception("incorrect code " + b);
		}

		return sb.toString();
	}
}
