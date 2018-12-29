package com.github.sonarperl;


import com.github.sonarperl.lexer.PerlLexer;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.Lexer;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenLocationTest {

    private Lexer lexer = PerlLexer.create(new PerlConfiguration(StandardCharsets.UTF_8));

    @Test
    public void test_multiline() throws Exception {
        TokenLocation tokenLocation = new TokenLocation(lex("'''first line\nsecond'''").get(0));
        assertOffsets(tokenLocation, 1, 0, 2, 9);

        tokenLocation = new TokenLocation(lex("'''first line\rsecond'''").get(0));
        assertOffsets(tokenLocation, 1, 0, 2, 9);

        tokenLocation = new TokenLocation(lex("'''first line\r\nsecond'''").get(0));
        assertOffsets(tokenLocation, 1, 0, 2, 9);
    }

    @Test
    public void test_newline_token() throws Exception {
        TokenLocation tokenLocation = new TokenLocation(lex("foo\n").get(1));
        assertOffsets(tokenLocation, 1, 3, 2, 0);
    }

    @Test
    public void test_one_line() throws Exception {
        TokenLocation tokenLocation = new TokenLocation(lex("foo").get(0));
        assertOffsets(tokenLocation, 1, 0, 1, 3);
    }

    @Test
    public void test_comment() throws Exception {
        TokenLocation commentLocation = new TokenLocation(lex("#comment\n").get(0).getTrivia().get(0).getToken());
        assertOffsets(commentLocation, 1, 0, 1, 8);
    }

    private void assertOffsets(TokenLocation tokenLocation, int startLine, int startLineOffset, int endLine, int endLineOffset) {
        assertThat(tokenLocation.startLine()).as("start line").isEqualTo(startLine);
        assertThat(tokenLocation.startLineOffset()).as("start line offset").isEqualTo(startLineOffset);
        assertThat(tokenLocation.endLine()).as("end line").isEqualTo(endLine);
        assertThat(tokenLocation.endLineOffset()).as("end line offset").isEqualTo(endLineOffset);
    }

    private List<Token> lex(String toLex) {
        return lexer.lex(toLex);
    }

}
