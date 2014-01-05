package ru.serjik.parser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import ru.serjik.parser.Tokenizer.Token;
import ru.serjik.parser.Tokenizer.TokenType;

/*
 bit
 76

 11000000 0xc0 - push const: push next 4 bytes to stack as float value
 01xvvvvv 0x40 - push vvvvv - variable index
 10xvvvvv 0x80 - pop  vvvvv - variable index 

 00cccccc - command
 01 add
 02 sub
 03 mul
 04 div
 05 neg
 06 sin
 07 cos
 08 exp
 09 loop
 10 sqrt
 11 delay
 12 time
 13 rnd
 14 pow

 */

public class Evaluator
{
	private static final int COMMAND_ADD = 1;
	private static final int COMMAND_SUB = 2;
	private static final int COMMAND_MUL = 3;
	private static final int COMMAND_DIV = 4;
	private static final int COMMAND_NEG = 5;
	private static final int COMMAND_SIN = 6;
	private static final int COMMAND_COS = 7;
	private static final int COMMAND_EXP = 8;
	private static final int COMMAND_LOOP = 9;
	private static final int COMMAND_SQRT = 10;
	private static final int COMMAND_DELAY = 11;
	private static final int COMMAND_TIME = 12;
	private static final int COMMAND_RND = 13;
	private static final int COMMAND_POW = 14;

	private LinkedList<Token> tokens;

	ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);

	public Evaluator(Tokenizer tokenizer) throws Exception
	{
		this.tokens = tokenizer.getTokens();
		prog();
	}

	public byte[] getByteCode()
	{
		return baos.toByteArray();
	}

	// prog ::= block;[prog]
	private void prog() throws Exception
	{
		block();

		if (tokens.peekFirst() == null)
		{
			throw new Exception("';' expected at end of file");
		}

		if (tokens.pollFirst().token != TokenType.SEMICOLON)
		{
			throw new Exception("';' expected");
		}

		if (tokens.peekFirst() != null)
		{
			prog();
		}
	}

	// block ::= variable = expression | function | loop
	private void block() throws Exception
	{
		Token token = tokens.pollFirst();

		switch (token.token)
		{
			case FUNCTION:
				function(token);
				break;

			case VARIABLE:
				if (tokens.pollFirst().token != TokenType.ASSIGN)
				{
					throw new Exception("assign token expected");
				}

				expression();

				writePopVariable(token.sequence);
				System.out.println("pop " + token.sequence);
				break;

			case LOOP:
				baos.write(COMMAND_LOOP);
				System.out.println(token.sequence);
				break;

			default:
				throw new Exception("function or variable expected");
		}
	}

	// expression ::= term {+|- term}
	private void expression() throws Exception
	{
		term();

		while (tokens.peekFirst() != null)
		{
			if (tokens.peekFirst().token != TokenType.ADD_SUB)
			{
				break;
			}

			switch (tokens.pollFirst().sequence.charAt(0))
			{
				case '+':
					term();
					baos.write(COMMAND_ADD);
					System.out.println("add");
					break;
				case '-':
					term();
					baos.write(COMMAND_SUB);
					System.out.println("sub");
					break;
			}
		}
	}

	// term ::= factor {*|/ factor}
	private void term() throws Exception
	{
		factor();
		while (tokens.peekFirst() != null)
		{
			if (tokens.peekFirst().token != TokenType.MUL_DIV)
			{
				break;
			}
			switch (tokens.pollFirst().sequence.charAt(0))
			{
				case '*':
					factor();
					baos.write(COMMAND_MUL);
					System.out.println("mul");
					break;
				case '/':
					factor();
					baos.write(COMMAND_DIV);
					System.out.println("div");
					break;
			}
		}
	}

	// factor ::= [-] const | variable | (expression) | function(expression)
	private void factor() throws Exception
	{
		Token token = tokens.pollFirst();

		if (token.sequence.equals("-"))
		{
			factor();
			baos.write(COMMAND_NEG);
			System.out.println("neg");
		}
		else
		{
			if (token.token == TokenType.CONST)
			{
				writePushConstant(token.sequence);
				System.out.println("push " + token.sequence);
			}

			if (token.token == TokenType.VARIABLE)
			{
				writePushVariable(token.sequence);
				System.out.println("push " + token.sequence);
			}

			if (token.token == TokenType.OPENING_BRACKET)
			{
				expression();

				if (tokens.pollFirst().token != TokenType.CLOSING_BRACKET)
				{
					throw new Exception("')' expected");
				}
			}

			if (token.token == TokenType.FUNCTION)
			{
				function(token);
			}
		}
	}

	private void function(Token token) throws Exception
	{
		if (token.token != TokenType.FUNCTION)
		{
			throw new Exception("function token expected");
		}

		if (tokens.pollFirst().token != TokenType.OPENING_BRACKET)
		{
			throw new Exception("'(' expected");
		}

		if (token.sequence.equals("time"))
		{
			baos.write(COMMAND_TIME);
		}
		else if (token.sequence.equals("rnd"))
		{
			baos.write(COMMAND_RND);
		}
		else if (token.sequence.equals("delay"))
		{
			expression();
			baos.write(COMMAND_DELAY);
		}
		else if (token.sequence.equals("sin"))
		{
			expression();
			baos.write(COMMAND_SIN);
		}
		else if (token.sequence.equals("cos"))
		{
			expression();
			baos.write(COMMAND_COS);
		}
		else if (token.sequence.equals("exp"))
		{
			expression();
			baos.write(COMMAND_EXP);
		}
		else if (token.sequence.equals("sqrt"))
		{
			expression();
			baos.write(COMMAND_SQRT);
		}
		else if (token.sequence.equals("pow"))
		{
			expression();
			if (tokens.pollFirst().token != TokenType.COMMA)
			{
				throw new Exception("'pow(a, b)' expected");
			}
			expression();
			baos.write(COMMAND_POW);
		}

		System.out.println(token.sequence);

		if (tokens.pollFirst().token != TokenType.CLOSING_BRACKET)
		{
			throw new Exception("'(' expected");
		}

	}

	// 10xvvvvv - pop vvvvv - variable index
	private void writePopVariable(String sequence)
	{
		int variableIndex = sequence.toLowerCase().charAt(0) - 'a';
		baos.write(0x80 | variableIndex);
	}

	// 01xvvvvv - push vvvvv - variable index
	private void writePushVariable(String sequence)
	{
		int variableIndex = sequence.toLowerCase().charAt(0) - 'a';
		baos.write(0x40 | variableIndex);
	}

	private void writePushConstant(String sequence) throws Exception
	{
		baos.write(0xc0);
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeFloat(Float.parseFloat(sequence));
	}
}
