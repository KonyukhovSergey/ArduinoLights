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
			tokenizer.tokenize("q=1;w=3;start_pos=25;loop;i=0;cicle:;set(i,255,i*5,255-i*5);i=i+1;if i<50 then goto cicle;endif;set(start_pos,255,255,255);start_pos=start_pos+1; if start_pos > 49 then start_pos=0; endif; end");
			/*
			 * loop;i=0;cicle_label:;set(i,255,i*5,255-i*5);i=i+1;if i<50 then goto cicle;endif;end
			 */

			System.out.println("");

			System.out.println(tokenizer.format());

			ByteCodeGenerator byteCodeGenerator = new ByteCodeGenerator(tokenizer);

			System.out.println("decompiled code");

			byte[] byteCode = byteCodeGenerator.getByteCode();

			System.out.println(Decompiler.decode(byteCode));

			System.out.printf("Serial.set(\"\\x%02x", byteCode.length);

			for (int i = 0; i < byteCode.length; i++)
			{
				System.out.printf("\\x%02x", byteCode[i]);
			}

			System.out.println("\", " + ((int) (byteCode.length)) + " + 2);");
			System.out.println("size = " + ((int) (byteCode.length)));

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
