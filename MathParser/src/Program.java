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
			//tokenizer.tokenize("q=255;w=3;start_pos=25;loop;i=0;cicle:;set(i,q,q,q);i=i+1;if i<50 then goto cicle;endif;set(start_pos,q,q,q);start_pos=start_pos+1; if start_pos > 49 then start_pos=0; endif; end");
			tokenizer.tokenize("// keywords: loop, call label, ret, goto label, if ... then ... endif; end;\n"+ 
			                   "// math: sin(x), cos(x), exp(x), sqrt(x), pow(x, y)\n" +
			                   "// label: 'identifier:'\n" +
			                   "// system: delay(milliseconds); rnd() returned [0..1]; set(i,r,g,b);\n" +
			                   "// loop: one draw cicle\n" +
			                   "pos = 0;color = 0;" +
			                   "r = 255; g = 0; b = 0;" +
			                   "loop;" +
			                   "set(pos,r,g,b);delay(1);" +
			                   "pos=pos+1;" +
			                   "if pos > 49 then pos = 0;" +
			                   "color=color+1;if color>6 then color = 0;endif;"+
			                   "if color==0 then r=0;b=0;b=255;endif;" +
			                   "if color==1 then r=0;g=255;b=0;endif;" +
			                   "if color==2 then r=255;g=0;b=0;endif;" +
			                   "if color==3 then r=255;g=255;b=0;endif;" +
			                   "if color==4 then r=255;g=0;b=255;endif;" +
			                   "if color==5 then r=0;g=255;b=255;endif;" +
			                   "if color==6 then r=255;g=255;b=255;endif;" +
			                   "endif;" +
			                   "end");
			 
			 

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
