package ru.serjik.light.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LightParser
{
	private static final Map<String, TokenType> TOKEN_TYPES = new HashMap<String, TokenType>();

	private final List<Token> tokens = new LinkedList<Token>();
	private final ContentReader contentReader;

	public LightParser(ContentReader contentReader)
	{
		this.contentReader = contentReader;
	}

	public void parse(String fileName)
	{
		String content = contentReader.getFileContent(fileName);
	}

	static
	{
		TOKEN_TYPES.put("sin", TokenType.SYSTEM_FUNCTION);
		TOKEN_TYPES.put("cos", TokenType.SYSTEM_FUNCTION);

		TOKEN_TYPES.put("if", TokenType.KEYWORD);
		TOKEN_TYPES.put("else", TokenType.KEYWORD);
	}
}
