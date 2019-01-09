package com.github.sonarperl.lexer;

import com.github.sonarperl.PerlPunctuator;
import com.github.sonarperl.api.PerlTokenType;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.Lexer;
import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.CodeReader;

import java.util.List;
import java.util.regex.Pattern;

public class HeredocChannel extends Channel<Lexer> {

    private final Token.Builder tokenBuilder = Token.builder();

    public boolean consume(CodeReader code, Lexer output) {

        List<Token> tokens = output.getTokens();
        if (tokens.size() < 4) return false;

        // TODO ignore whitespace...
        Token newline = tokens.get(tokens.size() - 1);
        Token semi = tokens.get(tokens.size() - 2);
        Token heredocDelimiter = tokens.get(tokens.size() - 3);
        Token ltlt = tokens.get(tokens.size() - 4);

        if (newline.getType() != PerlTokenType.NEWLINE) {
            return false;
        }

        if (ltlt.getType() != PerlPunctuator.LTLT || semi.getType() != PerlPunctuator.SEMICOLON) {
            return false;
        }

        String lookFor = null;
        if (heredocDelimiter.getType() == GenericTokenType.IDENTIFIER) {
            lookFor = heredocDelimiter.getValue();
        }
        if (heredocDelimiter.getType() == PerlTokenType.STRING) {
            lookFor = heredocDelimiter.getValue().substring(1, heredocDelimiter.getValue().length() - 2);
        }
        if (lookFor == null) {
            return false;
        }

        StringBuilder sb = new StringBuilder();
        if (code.popTo(Pattern.compile(".*?[\n\r]\\Q" + lookFor + "\\E").matcher(""), sb) == -1) {
            return false;
        }

       Token token = tokenBuilder
          .setType(PerlTokenType.STRING)
          .setValueAndOriginalValue(sb.toString())
          .setURI(output.getURI())
          .setLine(code.getLinePosition())
          .setColumn(code.getColumnPosition())
          .build();

        output.addToken(token);
        return true;
    }

}
