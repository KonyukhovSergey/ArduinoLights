package ru.serjik.light.parser;

public class Token
{
	public TokenType type;
	public String sequence;
	public int beginPosition;
	public int endPosition;

	public Token(TokenType type, String sequence, int begin, int end)
	{
		super();

		this.type = type;
		this.sequence = sequence;
		this.beginPosition = begin;
		this.endPosition = end;

		DebugOutput.println(sequence + " " + type);
	}

	public int length()
	{
		return endPosition - beginPosition;
	}
}
