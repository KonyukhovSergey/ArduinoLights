package ru.serjik.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer
{
	private LinkedList<TokenInfo> tokenInfos = null;
	private LinkedList<Token> tokens = null;

	public class Token
	{
		public final TokenType token;
		public final String sequence;
		public final int beginPosition;
		public final int endPosition;

		public Token(TokenType token, String sequence, int begin, int end)
		{
			super();

			this.token = token;
			this.sequence = sequence;
			this.beginPosition = begin;
			this.endPosition = end;

			System.out.println(sequence + " " + token);
		}
	}

	public Tokenizer()
	{
		tokenInfos = new LinkedList<TokenInfo>();
		tokens = new LinkedList<Token>();

		add("\\s+", TokenType.WHITE_SPACE);
		add("//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/", TokenType.COMMENT);

		add("\\(", TokenType.OPEN_BRACE);
		add("\\)", TokenType.CLOSE_BRACE);

		add("\\+", TokenType.ADD_SUB);
		add("\\-", TokenType.ADD_SUB);
		add("\\*", TokenType.MUL_DIV);
		add("\\/", TokenType.MUL_DIV);

		add("<", TokenType.RELATION);
		add(">", TokenType.RELATION);
		add("==", TokenType.RELATION);
		add("!=", TokenType.RELATION);

		add("[0-9]*\\.[0-9]+", TokenType.CONST_FLOAT);
		add("[0-9]+\\.", TokenType.CONST_FLOAT);
		add("[0-9]+", TokenType.CONST_INTEGER);

		add("[a-z][a-z0-9_]+:", TokenType.LABEL);
		add("[a-z][a-z0-9_]*", TokenType.IDENTIFIER);

		add("\\;", TokenType.SEMICOLON);
		add("\\=", TokenType.ASSIGN);
		add("\\,", TokenType.COMMA);

		predefinedKeywords.put("sin", TokenType.SYSTEM_FUNCTION);
		predefinedKeywords.put("sin", TokenType.SYSTEM_FUNCTION);
		predefinedKeywords.put("cos", TokenType.SYSTEM_FUNCTION);
		predefinedKeywords.put("exp", TokenType.SYSTEM_FUNCTION);
		predefinedKeywords.put("sqrt", TokenType.SYSTEM_FUNCTION);
		predefinedKeywords.put("delay", TokenType.SYSTEM_FUNCTION);
		predefinedKeywords.put("time", TokenType.SYSTEM_FUNCTION);
		predefinedKeywords.put("rnd", TokenType.SYSTEM_FUNCTION);
		predefinedKeywords.put("pow", TokenType.SYSTEM_FUNCTION);
		predefinedKeywords.put("abs", TokenType.SYSTEM_FUNCTION);

		predefinedKeywords.put("ret", TokenType.KEYWORD);
		predefinedKeywords.put("call", TokenType.KEYWORD);
		predefinedKeywords.put("end", TokenType.KEYWORD);
		predefinedKeywords.put("endif", TokenType.KEYWORD);
		predefinedKeywords.put("if", TokenType.KEYWORD);
		predefinedKeywords.put("then", TokenType.KEYWORD);
		predefinedKeywords.put("else", TokenType.KEYWORD);
		predefinedKeywords.put("goto", TokenType.KEYWORD);
		predefinedKeywords.put("loop", TokenType.KEYWORD);

	}

	public void add(String regex, TokenType tokenType)
	{
		tokenInfos.add(new TokenInfo(Pattern.compile("^[\\s]*" + regex), tokenType));
	}

	public void tokenize(String str) throws Exception
	{
		String prog = str.toLowerCase();

		tokens.clear();

		int startPosition = 0;

		while (startPosition < prog.length())
		{
			boolean match = false;

			for (TokenInfo info : tokenInfos)
			{
				Matcher matcher = info.regex.matcher(prog.substring(startPosition));

				if (matcher.find())
				{
					match = true;

					do
					{
						if (info.token == TokenType.WHITE_SPACE)
						{
							break;
						}

						String sequence = prog.substring(startPosition, startPosition + matcher.end());

						if (info.token == TokenType.IDENTIFIER)
						{
							TokenType tokenType = predefinedKeywords.get(sequence);

							if (tokenType != null)
							{
								tokens.add(new Token(tokenType, sequence, startPosition, startPosition + matcher.end()));
								break;
							}
						}

						if (info.token == TokenType.LABEL)
						{
							sequence = sequence.substring(0, sequence.length() - 1);
						}

						tokens.add(new Token(info.token, sequence, startPosition, startPosition + matcher.end()));

					}
					while (false);

					startPosition += matcher.end();
					break;
				}
			}

			if (!match)
			{
				throw new Exception("Unexpected character in input: " + prog.charAt(startPosition) + " pos = "
						+ startPosition);
			}
		}
	}

	public String format() throws Exception
	{
		StringBuilder sb = new StringBuilder();
		int ident = 0;

		for (Token token : tokens)
		{
			switch (token.token)
			{
				case ADD_SUB:
				case MUL_DIV:
				case ASSIGN:
				case RELATION:
					sb.append(' ');
					sb.append(token.sequence);

					if (token.sequence.equals("-"))
					{
						if (getTokens().indexOf(token) > 0)
						{
							Token prevToken = getTokens().get(getTokens().indexOf(token) - 1);
							if (prevToken.token == TokenType.CONST_FLOAT || prevToken.token==TokenType.CONST_INTEGER || prevToken.token == TokenType.IDENTIFIER)
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

				case LABEL:
					sb.append("\n");
					sb.append(token.sequence);
					sb.append(":");
					break;

				default:
					sb.append(token.sequence);
					break;
			}
		}
		return sb.toString();
	}

	public LinkedList<Token> getTokens()
	{
		return tokens;
	}

	private class TokenInfo
	{
		public final Pattern regex;
		public final TokenType token;

		public TokenInfo(Pattern regex, TokenType tokenType)
		{
			super();
			this.regex = regex;
			this.token = tokenType;
		}
	}

	private Map<String, TokenType> predefinedKeywords = new HashMap<String, TokenType>();

	public enum TokenType
	{
		CONST_INTEGER,
		CONST_FLOAT,
		IDENTIFIER,
		KEYWORD,
		OPEN_BRACE,
		CLOSE_BRACE,
		LABEL,
		SYSTEM_FUNCTION,
		ADD_SUB,
		MUL_DIV,
		SEMICOLON,
		ASSIGN,
		COMMA,
		COMMENT,
		WHITE_SPACE,
		RELATION,
	}
}
