package com.github.sonarperl.lexer;

import com.github.sonarperl.PerlPunctuator;
import com.github.sonarperl.api.PerlTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import com.sonar.sslr.impl.Lexer;
import org.sonar.sslr.channel.Channel;
import org.sonar.sslr.channel.CodeReader;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// https://perldoc.perl.org/perlop.html#Quote-Like-Operators

public class QuoteLikeChannel extends Channel<Lexer> {

    private static final char EOF = (char) -1;
    private static final Set<Character> QUOTELIKE_CHARS = new HashSet<>(Arrays.asList('q', 'x', 'w', 'r'));

    private final StringBuilder sb = new StringBuilder();

    private int index;

    private static final Matcher delimiter = Pattern.compile("[^a-zA-Z ]").matcher("");

    // first opening brace is consumed outside
    private int nesting = 0;

    private char ch;

    @Override
    public boolean consume(CodeReader code, Lexer output) {

        int line = code.getLinePosition();
        int column = code.getColumnPosition();
        index = 0;
        nesting = 0;
        // used for $a =~ /test/
        boolean rawRegex = false;
        boolean twoIterations = false;

        ch = code.charAt(index);

        switch (ch) {
            case '/':
                if (output.getTokens().isEmpty()) {
                    return false;
                }
                TokenType previousTokenType = output.getTokens().get(output.getTokens().size()-1).getType();
                if (previousTokenType != PerlPunctuator.EQU_TILD && previousTokenType != PerlPunctuator.NOT_TILD) {
                    return false;
                }
                rawRegex = true;
                break;
            case 'q':
                maybeNext(code, QUOTELIKE_CHARS);
                break;
            case 'm':
                break;
            case 'y':
                twoIterations = true;
                break;
            case 's':
                twoIterations = true;
                break;
            case 't':
                if (! expectNext(code, new HashSet<>(Arrays.asList('r')))) {
                    return false;
                }
                twoIterations = true;
                break;
            default:
                return false;
        }

        if (!rawRegex && !expectNext(code, delimiter)) {
            return false;
        }

        char quoteStart = ch;
        char quoteEnd = getQuoteEndChar(ch);

        nesting = 1;
        if (!readString(code, quoteStart, quoteEnd, quoteStart != quoteEnd)) {
            return false;
        }
        if (twoIterations) {
            if (quoteStart != quoteEnd) {
                if (! expectNext(code, new HashSet<>(Arrays.asList(quoteStart)))) {
                    return false;
                }
            }
            nesting = 1;
            if (!readString(code, quoteStart, quoteEnd, quoteStart != quoteEnd)) {
                return false;
            }
        }

        for (int i = 0; i <= index; i++) {
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

    private boolean readString(CodeReader code, char quoteStart, char quoteEnd, boolean nestingAllowed) {
        index++;
        while (true) {
            if (code.charAt(index) == EOF) {
                return false;
            }
            if (nestingAllowed && code.charAt(index) == quoteStart) {
                    nesting++;
            }
            if (code.charAt(index) == quoteEnd && code.charAt(index-1) != '\\') {
                nesting--;
            }
            if (nesting == 0) {
                return true;
            }
            index++;
        }
    }

    private void maybeNext(CodeReader code, Set<Character> validChars) {
        expectNext(code, validChars);
    }

    private boolean expectNext(CodeReader code, Set<Character> validChars) {
        if(validChars.contains(code.charAt(index+1))) {
            index++;
            return true;
        }
        return false;
    }

    private boolean expectNext(CodeReader code, Matcher matcher) {
        index++;
        ch = code.charAt(index);
        return matcher.reset(String.valueOf(ch)).matches();
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
