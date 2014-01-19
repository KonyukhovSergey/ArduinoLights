package ru.serjik.arduinopixels;


import ru.serjik.parser.Tokenizer;
import ru.serjik.parser.Tokenizer.Token;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

public class SourceColorFormatter
{
	public static final Spannable format(Tokenizer tokenizer) throws Exception
	{
		SpannableString prog = new SpannableString(tokenizer.format());

		for (Token token : tokenizer.getTokens())
		{
			switch (token.token)
			{
				case COMMENT:
					prog.setSpan(new ForegroundColorSpan(0xff008800), token.beginPosition, token.endPosition,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					break;

				case KEYWORD:
					prog.setSpan(new ForegroundColorSpan(0xff880088), token.beginPosition, token.endPosition,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					break;

				case SYSTEM_FUNCTION:
					prog.setSpan(new ForegroundColorSpan(0xff880000), token.beginPosition, token.endPosition,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					break;

				case IDENTIFIER:
					prog.setSpan(new ForegroundColorSpan(0xff000088), token.beginPosition, token.endPosition,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					break;

				case LABEL:
					prog.setSpan(new ForegroundColorSpan(0xff888888), token.beginPosition, token.endPosition,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					break;

				case CONST_FLOAT:
				case CONST_INTEGER:
					prog.setSpan(new ForegroundColorSpan(0xff008888), token.beginPosition, token.endPosition,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					break;

				default:
					break;
			}

		}

		return prog;
	}

}
