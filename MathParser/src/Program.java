import ru.serjik.parser.ByteCodeGenerator;
import ru.serjik.parser.Decompiler;
import ru.serjik.parser.Tokenizer;

public class Program
{
	public static void main(String[] args)
	{
		// System.out.print("kuku");

		Tokenizer tokenizer = new Tokenizer();

		try
		{
			// System.out.println(tokenizer.format("x=1;x=1.1;loop;r=255;"));
			tokenizer.tokenize("t=t+0.01;" + "loop;" + "p=t;" + "call proc1;r = p;" + "p=t*2+1;" + "call proc1;g = p;"
					+ "p=t*3+2;" + "call proc1;b=p;if b<10 then b=255; endif ;" + "end;proc1:;p = 25*sin(p)+25;d=x-p;p=255/(1+d*d);ret");
			
			System.out.println("");
			
			System.out.println(tokenizer.format());
			
			ByteCodeGenerator byteCodeGenerator = new ByteCodeGenerator(tokenizer);

			System.out.println("decompiled code");

			byte[] byteCode = byteCodeGenerator.getByteCode();

			System.out.println(Decompiler.decode(byteCode));

			System.out.printf("Serial.set(\"\\x%02x", byteCode.length+1);
			System.out.print("\\x04");

			for (int i = 0; i < byteCode.length; i++)
			{
				System.out.printf("\\x%02x", byteCode[i]);
			}

			System.out.println("\", "+((int) (byteCode.length + 1)) + " + 1);");
			System.out.println("size = " + ((int) (byteCode.length + 1)));

			//
			// // tokenizer.tokenize(" sin(x) * (1.4 + var_12) ");
			//
			// //Evaluator eval = new Evaluator(tokenizer);
			//
			// byte[] bc = eval.getByteCode();
			//
			// System.out.print("\\x"+ String.format("%02x\\x04",bc.length+1));
			// for (byte b : bc)
			// {
			// System.out.print("\\x"+ String.format("%02x",b));
			// }
			//
			// System.out.println("");
			// System.out.println("size = "+(int)(bc.length+2));
			// System.out.println("");
			//
			// System.out.println(Decompiler.decode(bc));
			//
			int bl = 0;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
