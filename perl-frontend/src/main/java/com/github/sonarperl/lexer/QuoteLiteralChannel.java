package com.github.sonarperl.lexer;

import com.github.sonarperl.api.PerlTokenType;
import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.Lexer;
import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.CodeReader;

import java.util.Set;

// https://perldoc.perl.org/perlop.html "Quote and Quote-like Operators"

public class QuoteLiteralChannel extends Channel<Lexer> {

    private static final char EOF = (char) -1;

    private final StringBuilder sb = new StringBuilder();

    private int index;
    private char ch;

    private static final Set<Character> delims = ImmutableSet.of('/', '[', '{', '(', '|', '#');

    @Override
    public boolean consume(CodeReader code, Lexer output) {

        int line = code.getLinePosition();
        int column = code.getColumnPosition();
        index = 0;

        ch = code.charAt(index);

        if (ch != 'q') {
            return false;
        }

        readSecondQ(code);

        if (!readQuoteBeginChar(code)) {
            return false;
        }

        char quoteEnd = getQuoteEndChar(ch);

        if (!readString(code, quoteEnd)) {
            return false;
        }

        for (int i = 0; i < index; i++) {
            sb.append((char) code.pop());
        }
        output.addToken(Token.builder()
                .setLine(line)
                .setColumn(column)
                .setURI(output.getURI())
                .setValueAndOriginalValue(sb.toString())
                .setType(PerlTokenType.STRING)
                .build());
        sb.setLength(0);
        return true;
    }


    private boolean readString(CodeReader code, char quoteEnd) {
        index++;
        while (code.charAt(index) != quoteEnd) {
            if (code.charAt(index) == EOF) {
                return false;
            }
            index++;
        }
        index++;
        return true;
    }

    private void readSecondQ(CodeReader code) {
        if(code.charAt(index+1) == 'q') {
            index++;
        }
        return;
    }

    private boolean readQuoteBeginChar(CodeReader code) {
        index++;
        ch = code.charAt(index);
        return delims.contains(Character.valueOf(ch));
    }

    private char getQuoteEndChar(char quoteBeginChar) {
        switch (quoteBeginChar) {
            case '{':
                return '}';
            case '[':
                return ']';
            case '(':
                return ')';
            case '<':
                return '>';
            default:
                return quoteBeginChar;
        }
    }
}
