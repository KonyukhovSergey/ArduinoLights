package ru.serjik.parser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ru.serjik.parser.Tokenizer.Token;
import ru.serjik.parser.Tokenizer.TokenType;

public class ByteCodeGenerator
{
	private static final int COMMAND_ADD = 0x01;
	private static final int COMMAND_SUB = 0x02;
	private static final int COMMAND_MUL = 0x03;
	private static final int COMMAND_DIV = 0x04;
	private static final int COMMAND_NEG = 0x05;

	private static final int COMMAND_SIN = 0x06;
	private static final int COMMAND_COS = 0x07;
	private static final int COMMAND_EXP = 0x08;
	private static final int COMMAND_LOOP = 0x09;
	private static final int COMMAND_SQRT = 0x0a;
	private static final int COMMAND_DELAY = 0x0b;
	private static final int COMMAND_TIME = 0x0c;
	private static final int COMMAND_RND = 0x0d;
	private static final int COMMAND_POW = 0x0e;
	private static final int COMMAND_ABS = 0x0f;

	private static final int COMMAND_CALL = 0x10;
	private static final int COMMAND_RET = 0x11;
	private static final int COMMAND_JUMP = 0x12;
	private static final int COMMAND_JUMPZ = 0x13;
	private static final int COMMAND_END = 0x14;

	private static final int COMMAND_GREATER = 0x15;
	private static final int COMMAND_LOWER = 0x16;
	private static final int COMMAND_EQ = 0x17;
	private static final int COMMAND_NEQ = 0x18;

	private static final int COMMAND_SET = 0x19;

	private LinkedList<Token> tokens;

	private Map<String, Integer> variables = new HashMap<String, Integer>();

	private List<CallLabelPosition> callPositions = new ArrayList<CallLabelPosition>();
	private Map<String, Integer> labels = new HashMap<String, Integer>();
	private List<EndIfPosition> endIfPositions = new ArrayList<EndIfPosition>();

	private ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);

	public ByteCodeGenerator(Tokenizer tokenizer) throws Exception
	{
		this.tokens = tokenizer.getTokens();

		removeComments();

		prog();
	}

	private void removeComments()
	{
		for (int i = 0; i < tokens.size();)
		{
			if (tokens.get(i).token == TokenType.COMMENT)
			{
				tokens.remove(i);
			}
			else
			{
				i++;
			}
		}
	}

	public byte[] getByteCode() throws Exception
	{
		byte[] code = baos.toByteArray();

		for (CallLabelPosition callPosition : callPositions)
		{
			if (labels.containsKey(callPosition.name))
			{
				writeIntToArray(code, callPosition.position, labels.get(callPosition.name));
			}
			else
			{
				throw new Exception("call undefined label '" + callPosition.name + "'");
			}
		}

		for (EndIfPosition endIfPosition : endIfPositions)
		{
			writeIntToArray(code, endIfPosition.ifPosition, endIfPosition.endIfPosition);
		}

		return code;
	}

	private void writeIntToArray(byte[] data, int offset, int value)
	{
		System.arraycopy(ByteBuffer.allocate(4).putInt(value).array(), 0, data, offset, 4);
	}

	// prog ::= block{;block}
	private void prog() throws Exception
	{
		while (block())
		{
			Token token = tokens.pollFirst();

			if (token == null)
			{
				break;
			}

			if (token.token != TokenType.SEMICOLON)
			{
				throw new Exception("';' expected");
			}
		}
	}

	// block ::= variable = expression | system_function | keyword
	private boolean block() throws Exception
	{
		Token token = tokens.pollFirst();

		if (token == null)
		{
			return false;
		}

		switch (token.token)
		{
		case SYSTEM_FUNCTION:
			function(token);
			break;

		case IDENTIFIER:
			if (tokens.pollFirst().token != TokenType.ASSIGN)
			{
				throw new Exception("assign token expected '" + token.sequence + "'");
			}

			expression();

			writePopVariable(token.sequence);
			System.out.println("pop " + token.sequence);
			break;

		case KEYWORD:
			return keyword(token);

		case LABEL:
			if (labels.containsKey(token.sequence))
			{
				throw new Exception("the '" + token.sequence + "' is defined already");
			}

			labels.put(token.sequence, baos.size());
			break;

		default:
			throw new Exception("function or variable expected");
		}
		return true;
	}

	private boolean keyword(Token token) throws Exception
	{
		if (token.sequence.equals("loop"))
		{
			baos.write(COMMAND_LOOP);
			System.out.println(token.sequence);
		}
		else if (token.sequence.equals("end"))
		{
			baos.write(COMMAND_END);
			System.out.println(token.sequence);
		}
		else if (token.sequence.equals("call"))
		{
			Token labelToken = tokens.pollFirst();

			if (labelToken.token != TokenType.IDENTIFIER)
			{
				throw new Exception("call 'label identifier expected'");
			}

			callPositions.add(new CallLabelPosition(baos.size() + 1, labelToken.sequence));
			writePushFloat("0.0");
			baos.write(COMMAND_CALL);
			System.out.println(token.sequence + " " + labelToken.sequence);
		}
		else if (token.sequence.equals("ret"))
		{
			baos.write(COMMAND_RET);
			System.out.println(token.sequence);
		}
		else if (token.sequence.equals("if"))
		{
			expression();

			Token thenToken = tokens.pollFirst();

			if (thenToken.token != TokenType.KEYWORD || thenToken.sequence.equals("then") == false)
			{
				throw new Exception("'then' expected");
			}

			int pos = baos.size() + 1;

			writePushFloat("0.0");

			baos.write(COMMAND_JUMPZ);

			prog();

			endIfPositions.add(new EndIfPosition(pos, baos.size()));
		}
		else if (token.sequence.equals("endif"))
		{
			return false;
		}
		else if (token.sequence.equals("goto"))
		{
			Token labelToken = tokens.pollFirst();

			if (labelToken.token != TokenType.IDENTIFIER)
			{
				throw new Exception("goto 'label identifier expected'");
			}

			callPositions.add(new CallLabelPosition(baos.size() + 1, labelToken.sequence));
			writePushFloat("0.0");
			baos.write(COMMAND_JUMP);
			System.out.println("goto " + token.sequence);
		}
		return true;
	}

	private void expression() throws Exception
	{
		math_expression();

		while (tokens.peekFirst() != null)
		{
			if (tokens.peekFirst().token != TokenType.RELATION)
			{
				break;
			}

			switch (tokens.pollFirst().sequence.charAt(0))
			{
			case '>':
				math_expression();
				baos.write(COMMAND_GREATER);
				System.out.println("greater");
				break;

			case '<':
				math_expression();
				baos.write(COMMAND_LOWER);
				System.out.println("lower");
				break;

			case '=':
				math_expression();
				baos.write(COMMAND_EQ);
				System.out.println("eq");
				break;

			case '!':
				math_expression();
				baos.write(COMMAND_NEQ);
				System.out.println("neq");
				break;
			}

		}
	}

	// math_expression ::= term {+|- term}
	private void math_expression() throws Exception
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
			if (token.token == TokenType.CONST_FLOAT)
			{
				writePushFloat(token.sequence);
				System.out.println("push float " + token.sequence);
			}

			if (token.token == TokenType.CONST_INTEGER)
			{
				writePushInt(token.sequence);
				System.out.println("push int " + token.sequence);
			}

			if (token.token == TokenType.IDENTIFIER)
			{
				writePushVariable(token.sequence);
				System.out.println("push " + token.sequence);
			}

			if (token.token == TokenType.OPEN_BRACE)
			{
				expression();

				if (tokens.pollFirst().token != TokenType.CLOSE_BRACE)
				{
					throw new Exception("')' expected");
				}
			}

			if (token.token == TokenType.SYSTEM_FUNCTION)
			{
				function(token);
			}
		}
	}

	private void function(Token token) throws Exception
	{
		if (token.token != TokenType.SYSTEM_FUNCTION)
		{
			throw new Exception("function token expected");
		}

		if (tokens.pollFirst().token != TokenType.OPEN_BRACE)
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
		else if (token.sequence.equals("set"))
		{
			expression();
			if (tokens.pollFirst().token != TokenType.COMMA)
			{
				throw new Exception("'set(i, r, g, b)' expected");
			}
			expression();
			if (tokens.pollFirst().token != TokenType.COMMA)
			{
				throw new Exception("'set(i, r, g, b)' expected");
			}
			expression();
			if (tokens.pollFirst().token != TokenType.COMMA)
			{
				throw new Exception("'set(i, r, g, b)' expected");
			}
			expression();
			baos.write(COMMAND_SET);
		}
		else if (token.sequence.equals("abs"))
		{
			expression();
			baos.write(COMMAND_ABS);
		}

		System.out.println(token.sequence);

		if (tokens.pollFirst().token != TokenType.CLOSE_BRACE)
		{
			throw new Exception("')' expected");
		}

	}

	// 10vvvvvv - pop vvvvvv - variable index
	private void writePopVariable(String sequence) throws Exception
	{
		if (variables.containsKey(sequence) == false)
		{
			if (variables.size() > 63)
			{
				throw new Exception("too many variables. there are 64 variables maximum in the program.");
			}
			variables.put(sequence, variables.size());
		}
		baos.write(0x80 | variables.get(sequence));
	}

	// 01vvvvvv - push vvvvvv - variable index
	private void writePushVariable(String sequence) throws Exception
	{
		if (variables.containsKey(sequence))
		{
			baos.write(0x40 | variables.get(sequence));
		}
		else
		{
			throw new Exception("variable '" + sequence + "' is not defined");
		}
	}

	// private void writePushConstant(String sequence) throws Exception
	// {
	// baos.write(0xc0);
	// DataOutputStream dos = new DataOutputStream(baos);
	// dos.writeFloat(Float.parseFloat(sequence));
	// }

	private void writePushFloat(String sequence) throws Exception
	{
		baos.write(0xc0);
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeFloat(Float.parseFloat(sequence));
	}

	private void writePushInt(String sequence) throws Exception
	{
		int value = Integer.parseInt(sequence);
		
		if (value < 256)
		{
			baos.write(0xc1);
			baos.write(value);
		}
		else if (value < 65536)
		{
			baos.write(0xc2);
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeShort(value);
		}
		else
		{
			baos.write(0xc3);
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeInt(value);
		}
	}

	private class CallLabelPosition
	{
		public final int position;
		public final String name;

		public CallLabelPosition(int position, String name)
		{
			this.position = position;
			this.name = name;
		}
	}

	private class EndIfPosition
	{
		public final int ifPosition;
		public final int endIfPosition;

		public EndIfPosition(int ifPosition, int endIfPosition)
		{
			this.ifPosition = ifPosition;
			this.endIfPosition = endIfPosition;
		}
	}

}
