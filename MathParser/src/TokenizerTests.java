import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

import ru.serjik.parser.Tokenizer;
import ru.serjik.parser.Tokenizer.Token;
import ru.serjik.parser.Tokenizer.TokenType;

public class TokenizerTests
{

	@Test
	public void testTokensCount() throws Exception
	{
		Tokenizer tokenizer = new Tokenizer();

		tokenizer.tokenize("sin;endif;end;()\nif then else // krkr");
		LinkedList<Token> tokens = tokenizer.getTokens();

		assertEquals(12, tokens.size());
	}

	@Test
	public void testSin() throws Exception
	{
		Tokenizer tokenizer = new Tokenizer();

		tokenizer.tokenize("x=sin(10);y= sin (2);");
		LinkedList<Token> tokens = tokenizer.getTokens();

		assertEquals(14, tokens.size());
		assertEquals(TokenType.SYSTEM_FUNCTION, tokens.get(2).token);
		assertEquals(TokenType.SYSTEM_FUNCTION, tokens.get(9).token);
	}

	@Test
	public void testRelation() throws Exception
	{
		Tokenizer tokenizer = new Tokenizer();

		tokenizer.tokenize("x<1;y>2;z==3;a!=b");
		LinkedList<Token> tokens = tokenizer.getTokens();

		assertEquals(15, tokens.size());
		assertEquals(TokenType.RELATION, tokens.get(1).token);
		assertEquals(TokenType.RELATION, tokens.get(5).token);
		assertEquals(TokenType.RELATION, tokens.get(9).token);
		assertEquals(TokenType.RELATION, tokens.get(13).token);
	}
	
	@Test
	public void testCos() throws Exception
	{
		Tokenizer tokenizer = new Tokenizer();

		tokenizer.tokenize("x=cos(10);z = cos	(10);y= sin (2);");
		LinkedList<Token> tokens = tokenizer.getTokens();

		assertEquals(21, tokens.size());
		assertEquals(TokenType.SYSTEM_FUNCTION, tokens.get(2).token);
		assertEquals(TokenType.SYSTEM_FUNCTION, tokens.get(9).token);
	}

	@Test
	public void testFloat() throws Exception
	{
		Tokenizer tokenizer = new Tokenizer();

		tokenizer.tokenize("345 0.1 123 .1 405 1. -999 -99.9");
		// 01234567890123456789012
		LinkedList<Token> tokens = tokenizer.getTokens();

		assertEquals(TokenType.CONST_FLOAT, tokens.get(1).token);
		assertEquals(TokenType.CONST_FLOAT, tokens.get(3).token);
		assertEquals(TokenType.CONST_FLOAT, tokens.get(5).token);
		assertEquals(TokenType.CONST_FLOAT, tokens.get(9).token);

		assertEquals("99.9", tokens.get(9).sequence);
	}

	@Test
	public void testIntegers() throws Exception
	{
		Tokenizer tokenizer = new Tokenizer();

		tokenizer.tokenize("345 0.1 123 .1 405 1. -999 -99.9");
		// 01234567890123456789012
		LinkedList<Token> tokens = tokenizer.getTokens();

		assertEquals(TokenType.CONST_INTEGER, tokens.get(0).token);
		assertEquals(TokenType.CONST_INTEGER, tokens.get(2).token);
		assertEquals(TokenType.CONST_INTEGER, tokens.get(4).token);
		assertEquals(TokenType.CONST_INTEGER, tokens.get(7).token);

		assertEquals("999", tokens.get(7).sequence);
	}

	@Test
	public void testProgram() throws Exception
	{
		Tokenizer tokenizer = new Tokenizer();

		tokenizer.tokenize("x=1;y=2;z=sin(x+y);call proc1;end;proc1:ret;");
		// 01234567890123456789012
		LinkedList<Token> tokens = tokenizer.getTokens();

		assertEquals(25, tokens.size());

		assertEquals(TokenType.IDENTIFIER, tokens.pollFirst().token);
		assertEquals(TokenType.ASSIGN, tokens.pollFirst().token);
		assertEquals(TokenType.CONST_INTEGER, tokens.pollFirst().token);
		assertEquals(TokenType.SEMICOLON, tokens.pollFirst().token);
		assertEquals(TokenType.IDENTIFIER, tokens.pollFirst().token);
		assertEquals(TokenType.ASSIGN, tokens.pollFirst().token);
		assertEquals(TokenType.CONST_INTEGER, tokens.pollFirst().token);
		assertEquals(TokenType.SEMICOLON, tokens.pollFirst().token);
		assertEquals(TokenType.IDENTIFIER, tokens.pollFirst().token);
		assertEquals(TokenType.ASSIGN, tokens.pollFirst().token);
		assertEquals(TokenType.SYSTEM_FUNCTION, tokens.pollFirst().token);
		assertEquals(TokenType.OPEN_BRACE, tokens.pollFirst().token);
		assertEquals(TokenType.IDENTIFIER, tokens.pollFirst().token);
		assertEquals(TokenType.ADD_SUB, tokens.pollFirst().token);
		assertEquals(TokenType.IDENTIFIER, tokens.pollFirst().token);
		assertEquals(TokenType.CLOSE_BRACE, tokens.pollFirst().token);
		assertEquals(TokenType.SEMICOLON, tokens.pollFirst().token);
		assertEquals(TokenType.KEYWORD, tokens.pollFirst().token);
		assertEquals(TokenType.IDENTIFIER, tokens.pollFirst().token);

		assertEquals(TokenType.SEMICOLON, tokens.pollFirst().token);
		assertEquals(TokenType.KEYWORD, tokens.pollFirst().token);
		assertEquals(TokenType.SEMICOLON, tokens.pollFirst().token);
		assertEquals(TokenType.LABEL, tokens.pollFirst().token);
		assertEquals(TokenType.KEYWORD, tokens.pollFirst().token);
		assertEquals(TokenType.SEMICOLON, tokens.pollFirst().token);
	}

	@Test
	public void testLabels() throws Exception
	{
		Tokenizer tokenizer = new Tokenizer();

		tokenizer.tokenize("x=1;call proc1;end;proc1:ret;");

		LinkedList<Token> tokens = tokenizer.getTokens();
		
		assertEquals(TokenType.IDENTIFIER,tokens.get(5).token);
		assertEquals(TokenType.LABEL,tokens.get(9).token);
		
		assertEquals(tokens.get(5).sequence, tokens.get(9).sequence);
		
	}

}
