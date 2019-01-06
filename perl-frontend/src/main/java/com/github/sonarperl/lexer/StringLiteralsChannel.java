package com.github.sonarperl.lexer;


import com.github.sonarperl.api.PerlTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.Lexer;
import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.CodeReader;

public class StringLiteralsChannel extends Channel<Lexer> {

    private static final char EOF = (char) -1;

    private final StringBuilder sb = new StringBuilder();

    private int index;
    private char ch;

    @Override
    public boolean consume(CodeReader code, Lexer output) {
        int line = code.getLinePosition();
        int column = code.getColumnPosition();
        index = 0;

        ch = code.charAt(index);

        if ((ch != '\'') && (ch != '\"')) {
            return false;
        }
        if (!read(code)) {
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

    private boolean read(CodeReader code) {
        return readString(code);
    }

    private boolean readString(CodeReader code) {
        index++;
        while (code.charAt(index) != ch) {
            if (code.charAt(index) == EOF) {
                return false;
            }
            if (code.charAt(index) == '\\') {
                // escape
                index++;
            }
            index++;
        }
        index++;
        return true;
    }

}

