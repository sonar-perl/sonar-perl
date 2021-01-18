package com.github.sonarperl.lexer;

import com.github.sonarperl.PerlPunctuator;
import com.github.sonarperl.api.PerlTokenType;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.Lexer;
import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.CodeReader;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeredocChannel extends Channel<Lexer> {

    private final Token.Builder tokenBuilder = Token.builder();

    private String lookFor = null;

    public boolean consume(CodeReader code, Lexer output) {

        List<Token> tokens = output.getTokens();

        if (lookFor != null && tokens.size() > 0) {
            Token newline = tokens.get(tokens.size() - 1);
            if (newline.getType() != PerlTokenType.NEWLINE) {
                return false;
            }
            return consumeUntil(code, output);
        } else {

            if (tokens.size() < 2) return false;

            // TODO ignore whitespace...
            Token heredocDelimiter = tokens.get(tokens.size() - 1);
            Token ltlt = tokens.get(tokens.size() - 2);

            if (ltlt.getType() != PerlPunctuator.LTLT) {
                return false;
            }

            if (heredocDelimiter.getType() == GenericTokenType.IDENTIFIER) {
                lookFor = heredocDelimiter.getValue();
            }
            if (heredocDelimiter.getType() == PerlTokenType.STRING) {
                lookFor = heredocDelimiter.getValue().substring(1, heredocDelimiter.getValue().length() - 2);
            }
        }

        return false;

    }

    private boolean consumeUntil(CodeReader code, Lexer output) {
        int line = code.getLinePosition();
        int column = code.getColumnPosition();

        StringBuilder sb = new StringBuilder();
        Matcher matcher = Pattern.compile(".*?(?=\\Q" + lookFor + "\\E)", Pattern.DOTALL).matcher("");
        lookFor = null;

        if (code.popTo(matcher, sb) == -1) {
            return false;
        }

       Token token = tokenBuilder
          .setType(PerlTokenType.STRING)
          .setValueAndOriginalValue(sb.toString())
          .setURI(output.getURI())
          .setLine(line)
          .setColumn(column)
          .build();

        output.addToken(token);
        return true;
    }

}
