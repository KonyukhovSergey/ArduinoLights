package ru.serjik.parser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ru.serjik.parser.Tokenizer.Token;
import ru.serjik.parser.Tokenizer.TokenType;

public class ByteCodeGenerator
{
	public enum CommandTypes
	{
		ERROR_,

		ADD, SUB, MUL, DIV, NEG,

		SIN, COS, EXP, SQRT, POW, ABS,

		LOOP, DELAY, TIME, RND, RET, END,

		GREATER, LOWER, EQ, NEQ,

		SET_RGB, SET_GAMMA,

		PUSH_BYTE, PUSH_SHORT, PUSH_INT, PUSH_FLOAT,

		SHLEFT, SHRIGHT,

		GET_R, GET_G, GET_B,

		MEM_SET, MEM_GET,
	}

	private LinkedList<Token> tokens;

	private Map<String, Integer> variables = new HashMap<String, Integer>();
	private Map<String, Integer> arrays = new HashMap<String, Integer>();
	private int arrayOffset = 128; // max_variable_count

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
				writeAddressToArray(code, callPosition.position, labels.get(callPosition.name));
			}
			else
			{
				throw new Exception("call undefined label '" + callPosition.name + "'");
			}
		}

		for (EndIfPosition endIfPosition : endIfPositions)
		{
			writeAddressToArray(code, endIfPosition.ifPosition, endIfPosition.endIfPosition);
		}

		return code;
	}

	private void writeAddressToArray(byte[] data, int offset, int value)
	{
		data[offset + 0] = (byte) ((data[offset] & 0xf0) | ((value & 0x0f00) >> 8));
		data[offset + 1] = (byte) (value & 0xff);
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

				if (arrays.containsKey(token.sequence))
				{
					if (tokens.pollFirst().token != TokenType.OPEN_BRACKET)
					{
						throw new Exception("'[' expected");
					}

					expression();

					if (tokens.pollFirst().token != TokenType.CLOSE_BRACKET)
					{
						throw new Exception("']' expected");
					}

					if (tokens.pollFirst().token != TokenType.ASSIGN)
					{
						throw new Exception("'=' expected");
					}

					expression();

					baos.write(CommandTypes.MEM_SET.ordinal());
					baos.write(arrays.get(token.sequence));

					DebugOutput.println("mem_set " + token.sequence + " offset = " + arrays.get(token.sequence));
				}
				else
				{
					switch (tokens.peekFirst().token)
					{
						case ASSIGN:
							tokens.pollFirst();
							break;

						case MEMBER:
							break;

						default:
							throw new Exception("assign or member token expected '" + token.sequence + "'");
					}

					expression();

					writePopVariable(token.sequence);
					DebugOutput.println("pop " + token.sequence);
				}
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
			baos.write(CommandTypes.LOOP.ordinal());
			DebugOutput.println(token.sequence);
		}
		else if (token.sequence.equals("array"))
		{
			keywordArray();
		}
		else if (token.sequence.equals("end"))
		{
			baos.write(CommandTypes.END.ordinal());
			DebugOutput.println(token.sequence);
		}
		else if (token.sequence.equals("call"))
		{
			Token labelToken = tokens.pollFirst();

			if (labelToken.token != TokenType.IDENTIFIER)
			{
				throw new Exception("call 'label identifier expected'");
			}

			callPositions.add(new CallLabelPosition(baos.size(), labelToken.sequence));
			baos.write(0x40); // call
			baos.write(0x00);
			DebugOutput.println(token.sequence + " " + labelToken.sequence);
		}
		else if (token.sequence.equals("ret"))
		{
			baos.write(CommandTypes.RET.ordinal());
			DebugOutput.println(token.sequence);
		}
		else if (token.sequence.equals("shleft"))
		{
			baos.write(CommandTypes.SHLEFT.ordinal());
			DebugOutput.println(token.sequence);
		}
		else if (token.sequence.equals("shright"))
		{
			baos.write(CommandTypes.SHRIGHT.ordinal());
			DebugOutput.println(token.sequence);
		}
		else if (token.sequence.equals("if"))
		{
			expression();

			Token thenToken = tokens.pollFirst();

			if (thenToken.token != TokenType.KEYWORD || thenToken.sequence.equals("then") == false)
			{
				throw new Exception("'then' expected");
			}

			int pos = baos.size();

			baos.write(0x60); // jumpz
			baos.write(0x00);

			prog();

			endIfPositions.add(new EndIfPosition(pos, baos.size()));
		}
		else if (token.sequence.equals("endif") || token.sequence.equals("repeat"))
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

			callPositions.add(new CallLabelPosition(baos.size(), labelToken.sequence));
			baos.write(0x50); // goto
			baos.write(0x00);
			DebugOutput.println("goto " + token.sequence);
		}
		else if (token.sequence.equals("while"))
		{
			int posWhile = baos.size();

			expression();

			Token tokenDo = tokens.pollFirst();

			if (tokenDo.sequence.equals("do") == false)
			{
				throw new Exception("'do' expected");
			}

			int pos = baos.size();

			baos.write(0x60); // jumpz
			baos.write(0x00);

			prog();

			writeGotoAddress(posWhile);

			endIfPositions.add(new EndIfPosition(pos, baos.size()));
		}
		return true;
	}

	private void keywordArray() throws Exception
	{
		Token tokenArrayName = tokens.pollFirst();

		if (tokenArrayName.token != TokenType.IDENTIFIER)
		{
			throw new Exception("identifier is expected: array name[size];");
		}

		if (arrays.containsKey(tokenArrayName.sequence))
		{
			throw new Exception("array name is already defined");
		}

		if (tokens.pollFirst().token != TokenType.OPEN_BRACKET)
		{
			throw new Exception("'[' is expected");
		}

		Token tokenArraySize = tokens.pollFirst();

		if (tokenArraySize.token != TokenType.CONST_INTEGER)
		{
			throw new Exception("array size must be const integer");
		}

		if (tokens.pollFirst().token != TokenType.CLOSE_BRACKET)
		{
			throw new Exception("']'  is expected");
		}

		arrayOffset -= Integer.parseInt(tokenArraySize.sequence);

		if (arrayOffset < variables.keySet().size())
		{
			throw new Exception("not enougth memory");
		}

		arrays.put(tokenArrayName.sequence, arrayOffset);
	}

	private void writeGotoAddress(int pos)
	{
		baos.write((byte) ((0x50) | ((pos & 0x0f00) >> 8)));
		baos.write((byte) (pos & 0xff));
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
					baos.write(CommandTypes.GREATER.ordinal());
					DebugOutput.println("greater");
					break;

				case '<':
					math_expression();
					baos.write(CommandTypes.LOWER.ordinal());
					DebugOutput.println("lower");
					break;

				case '=':
					math_expression();
					baos.write(CommandTypes.EQ.ordinal());
					DebugOutput.println("eq");
					break;

				case '!':
					math_expression();
					baos.write(CommandTypes.NEQ.ordinal());
					DebugOutput.println("neq");
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
					baos.write(CommandTypes.ADD.ordinal());
					DebugOutput.println("add");
					break;

				case '-':
					term();
					baos.write(CommandTypes.SUB.ordinal());
					DebugOutput.println("sub");
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
					baos.write(CommandTypes.MUL.ordinal());
					DebugOutput.println("mul");
					break;

				case '/':
					factor();
					baos.write(CommandTypes.DIV.ordinal());
					DebugOutput.println("div");
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
			baos.write(CommandTypes.NEG.ordinal());
			DebugOutput.println("neg");
		}
		else
		{
			if (token.token == TokenType.CONST_FLOAT)
			{
				writePushFloat(token.sequence);
				DebugOutput.println("push float " + token.sequence);
			}

			if (token.token == TokenType.CONST_INTEGER)
			{
				writePushInt(token.sequence);
				DebugOutput.println("push int " + token.sequence);
			}

			if (token.token == TokenType.IDENTIFIER)
			{
				if (arrays.containsKey(token.sequence))
				{
					if (tokens.pollFirst().token != TokenType.OPEN_BRACKET)
					{
						throw new Exception("'[' expected");
					}

					expression();

					if (tokens.pollFirst().token != TokenType.CLOSE_BRACKET)
					{
						throw new Exception("']' expected");
					}

					baos.write(CommandTypes.MEM_GET.ordinal());
					baos.write(arrays.get(token.sequence));

					DebugOutput.println("mem_get " + token.sequence + " offset = " + arrays.get(token.sequence));
				}
				else
				{
					writePushVariable(token.sequence);
					DebugOutput.println("push " + token.sequence);
				}
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
			baos.write(CommandTypes.TIME.ordinal());
		}
		else if (token.sequence.equals("rnd"))
		{
			baos.write(CommandTypes.RND.ordinal());
		}
		else if (token.sequence.equals("delay"))
		{
			expression();
			baos.write(CommandTypes.DELAY.ordinal());
		}
		else if (token.sequence.equals("gamma"))
		{
			expression();
			baos.write(CommandTypes.SET_GAMMA.ordinal());
		}
		else if (token.sequence.equals("sin"))
		{
			expression();
			baos.write(CommandTypes.SIN.ordinal());
		}
		else if (token.sequence.equals("cos"))
		{
			expression();
			baos.write(CommandTypes.COS.ordinal());
		}
		else if (token.sequence.equals("getr"))
		{
			expression();
			baos.write(CommandTypes.GET_R.ordinal());
		}
		else if (token.sequence.equals("getg"))
		{
			expression();
			baos.write(CommandTypes.GET_G.ordinal());
		}
		else if (token.sequence.equals("getb"))
		{
			expression();
			baos.write(CommandTypes.GET_B.ordinal());
		}
		else if (token.sequence.equals("exp"))
		{
			expression();
			baos.write(CommandTypes.EXP.ordinal());
		}
		else if (token.sequence.equals("sqrt"))
		{
			expression();
			baos.write(CommandTypes.SQRT.ordinal());
		}
		else if (token.sequence.equals("pow"))
		{
			expression();
			if (tokens.pollFirst().token != TokenType.COMMA)
			{
				throw new Exception("'pow(a, b)' expected");
			}
			expression();
			baos.write(CommandTypes.POW.ordinal());
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
			baos.write(CommandTypes.SET_RGB.ordinal());
		}
		else if (token.sequence.equals("abs"))
		{
			expression();
			baos.write(CommandTypes.ABS.ordinal());
		}

		DebugOutput.println(token.sequence);

		if (tokens.pollFirst().token != TokenType.CLOSE_BRACE)
		{
			throw new Exception("')' expected");
		}

	}

	private void writePushVariable(String sequence) throws Exception
	{
		if (variables.containsKey(sequence))
		{
			baos.write(0x80 | variables.get(sequence));
		}
		else
		{
			throw new Exception("variable '" + sequence + "' is not defined");
		}
	}

	private void writePopVariable(String sequence) throws Exception
	{
		if (variables.containsKey(sequence) == false)
		{
			if (variables.size() > 63)
			{
				throw new Exception("too many variables. there are 64 variables maximum in the program.");
			}
			if (variables.size() > arrayOffset - 1)
			{
				throw new Exception("too many variables. there is no free memory");
			}
			variables.put(sequence, variables.size());
		}
		baos.write(0xc0 | variables.get(sequence));
	}

	private void writeTwoBytes()
	{
		baos.write(0);
		baos.write(0);
	}

	private void writePushFloat(String sequence) throws Exception
	{
		baos.write(CommandTypes.PUSH_FLOAT.ordinal());
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeFloat(Float.parseFloat(sequence));
	}

	private void writePushInt(String sequence) throws Exception
	{
		int value = Integer.parseInt(sequence);

		if (value < 0)
		{
			throw new Exception("incorrect '" + sequence + "'");
		}

		if (value < 256)
		{
			baos.write(CommandTypes.PUSH_BYTE.ordinal());
			baos.write(value);
		}
		else if (value < 65536)
		{
			baos.write(CommandTypes.PUSH_SHORT.ordinal());
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeShort(value);
		}
		else
		{
			baos.write(CommandTypes.PUSH_INT.ordinal());
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
