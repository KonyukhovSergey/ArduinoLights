package ru.serjik.parser;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import ru.serjik.parser.ByteCodeGenerator.CommandTypes;

public class Decompiler
{
	public static String decode(byte[] bc) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		sb.setLength(0);
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bc));

		CommandTypes commands[] = CommandTypes.values();

		int pos = -1;

		while (dis.available() > 0)
		{
			int b = (int) dis.readByte() & 0xff;
			pos++;

			if (b == CommandTypes.PUSH_FLOAT.ordinal())
			{
				sb.append("" + pos + " " + "push float ");
				sb.append(dis.readFloat() + " ");
				pos += 4;
				sb.append('\n');
				continue;
			}

			if (b == CommandTypes.PUSH_BYTE.ordinal())
			{
				sb.append("" + pos + " " + "push byte ");
				sb.append((int) (0xff & dis.readByte()) + " ");
				pos += 1;
				sb.append('\n');
				continue;
			}

			if (b == CommandTypes.PUSH_SHORT.ordinal())
			{
				sb.append("" + pos + " " + "push short ");
				sb.append(dis.readShort() + " ");
				pos += 2;
				sb.append('\n');
				continue;
			}

			if (b == CommandTypes.PUSH_INT.ordinal())
			{
				sb.append("" + pos + " " + "push int ");
				sb.append(dis.readInt() + " ");
				pos += 4;
				sb.append('\n');
				continue;
			}
			
			if (b == CommandTypes.MEM_SET.ordinal())
			{
				sb.append("" + pos + " " + "mem_set ");
				sb.append(dis.readByte() + " ");
				pos += 1;
				sb.append('\n');
				continue;
			}
			
			if (b == CommandTypes.MEM_GET.ordinal())
			{
				sb.append("" + pos + " " + "mem_get ");
				sb.append(dis.readByte() + " ");
				pos += 1;
				sb.append('\n');
				continue;
			}
			

			if ((b >> 6) == 2)
			{
				sb.append("" + pos + " " + "push v[");
				sb.append("" + (int) ((b & 0x3f)));
				sb.append("]\n");
				continue;
			}

			if ((b >> 6) == 3)
			{
				sb.append("" + pos + " " + "pop v[");
				sb.append("" + (int) ((b & 0x3f)));
				sb.append("]\n");
				continue;
			}

			if ((b >> 4) == 0x4)
			{
				int value = ((int) (b & 0x0f) << 8) | (dis.readByte() & 0xff);
				sb.append("" + pos + " call " + value);
				sb.append("\n");
				pos++;
				continue;
			}

			if ((b >> 4) == 0x5)
			{
				int value = ((int) (b & 0x0f) << 8) | (dis.readByte() & 0xff);
				sb.append("" + pos + " goto " + value);
				sb.append("\n");
				pos++;
				continue;
			}

			if ((b >> 4) == 0x6)
			{
				int value = ((int) (b & 0xf) << 8) | (dis.readByte() & 0xff);
				sb.append("" + pos + " jumpz " + value);
				sb.append("\n");
				pos++;
				continue;
			}

			if (b > 0 && b < 64)
			{
				sb.append("" + pos + " " + commands[b] + "\n");
				continue;
			}

			throw new Exception("incorrect code " + b);
		}

		return sb.toString();
	}
}
