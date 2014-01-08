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
			tokenizer.tokenize("x=1;loop;repeat1:;call proc1;goto repeat1;end;proc1:;c=x*x;ret;proc2:;if x>10 then call proc1;endif;y=4;ret");
			ByteCodeGenerator byteCodeGenerator = new ByteCodeGenerator(tokenizer);
			
			System.out.println("decompiled code");
			
			System.out.println(Decompiler.decode(byteCodeGenerator.getByteCode()));
			
//			
//			// tokenizer.tokenize(" sin(x) * (1.4 + var_12) ");
//
//			//Evaluator eval = new Evaluator(tokenizer);
//
//			byte[] bc = eval.getByteCode();
//			
//			System.out.print("\\x"+ String.format("%02x\\x04",bc.length+1));
//			for (byte b : bc)
//			{
//				System.out.print("\\x"+ String.format("%02x",b));
//			}
//			
//			System.out.println("");
//			System.out.println("size = "+(int)(bc.length+2));
//			System.out.println("");
//			
//			System.out.println(Decompiler.decode(bc));
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
