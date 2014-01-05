package ru.serjik.parser;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer
{

	private class TokenInfo
	{
		public final Pattern regex;
		public final TokenType token;

		public TokenInfo(Pattern regex, TokenType function)
		{
			super();
			this.regex = regex;
			this.token = function;
		}
	}

	public class Token
	{
		public final TokenType token;
		public final String sequence;

		public Token(TokenType token, String sequence)
		{
			super();
			this.token = token;
			this.sequence = sequence;
		}
	}

	private LinkedList<TokenInfo> tokenInfos;
	private LinkedList<Token> tokens;

	public Tokenizer()
	{
		tokenInfos = new LinkedList<TokenInfo>();
		tokens = new LinkedList<Token>();

		add("loop",TokenType.LOOP);
		add("sin", TokenType.FUNCTION);
		add("cos", TokenType.FUNCTION);
		add("exp", TokenType.FUNCTION);
		add("sqrt", TokenType.FUNCTION);
		add("delay", TokenType.FUNCTION);
		add("time", TokenType.FUNCTION);
		add("rnd", TokenType.FUNCTION);
		add("pow", TokenType.FUNCTION);
		add("\\(", TokenType.OPENING_BRACKET);
		add("\\)", TokenType.CLOSING_BRACKET);
		add("\\+", TokenType.ADD_SUB);
		add("\\-", TokenType.ADD_SUB);
		add("\\*", TokenType.MUL_DIV);
		add("\\/", TokenType.MUL_DIV);
		add("[0-9.]+", TokenType.CONST);
		add("[a-zA-Z][a-zA-Z0-9_]*", TokenType.VARIABLE);
		add("\\;", TokenType.SEMICOLON);
		add("\\=", TokenType.ASSIGN);
		add("\\,", TokenType.COMMA);

	}

	public String format(String expr) throws Exception
	{

		tokenize(expr);

		StringBuilder sb = new StringBuilder();

		for (Tokenizer.Token token : getTokens())
		{
			switch (token.token)
			{
				case ADD_SUB:
				case MUL_DIV:
				case ASSIGN:
					sb.append(' ');
					sb.append(token.sequence);
					if (token.sequence.equals("-"))
					{
						if (getTokens().indexOf(token) > 0)
						{
							Token prevToken = getTokens().get(getTokens().indexOf(token) - 1);
							if (prevToken.token == TokenType.CONST || prevToken.token == TokenType.VARIABLE)
							{
								sb.append(' ');
							}
						}
					}
					else
					{
						sb.append(' ');

					}
					break;

				case SEMICOLON:
					sb.append(token.sequence);
					sb.append('\n');
					break;

				case COMMA:
					sb.append(token.sequence);
					sb.append(' ');
					break;

				default:
					sb.append(token.sequence);
					break;
			}
		}
		return sb.toString();
	}

	public void add(String regex, TokenType function)
	{
		tokenInfos.add(new TokenInfo(Pattern.compile("^" + regex), function));
	}

	public void tokenize(String str) throws Exception
	{
		String s = str.trim().toLowerCase();
		tokens.clear();
		while (!s.equals(""))
		{
			boolean match = false;
			for (TokenInfo info : tokenInfos)
			{
				Matcher m = info.regex.matcher(s);
				if (m.find())
				{
					match = true;
					String tok = m.group().trim();
					s = m.replaceFirst("").trim();
					tokens.add(new Token(info.token, tok));
					break;
				}
			}
			if (!match)
				throw new Exception("Unexpected character in input: " + s);
		}
	}

	public LinkedList<Token> getTokens()
	{
		return tokens;
	}

	public enum TokenType
	{
		UNDEFINED, FUNCTION, OPENING_BRACKET, CLOSING_BRACKET, ADD_SUB, MUL_DIV, CONST, VARIABLE, SEMICOLON, ASSIGN, COMMA, LOOP
	}

}
