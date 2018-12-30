package com.github.sonarperl.lexer;

import com.github.sonarperl.PerlConfiguration;
import com.github.sonarperl.PerlPunctuator;
import com.github.sonarperl.api.PerlKeyword;
import com.github.sonarperl.api.PerlTokenType;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.impl.Lexer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasComment;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasToken;

import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class PerlLexerTest {
    private static Lexer lexer;

    @BeforeClass
    public static void init() {
        lexer = PerlLexer.create(new PerlConfiguration(StandardCharsets.UTF_8));
    }

    @Test
    public void comments() {
        assertThat(lexer.lex("# My comment \n new line"), hasComment("# My comment "));
    }

    @Test
    public void shortstring_literals() {
        assertThat("empty", lexer.lex("''"), hasToken("''", PerlTokenType.STRING));
        assertThat("empty", lexer.lex("\"\""), hasToken("\"\"", PerlTokenType.STRING));
        assertThat(lexer.lex("'hello world'"), hasToken("'hello world'", PerlTokenType.STRING));
        assertThat(lexer.lex("\"hello world\""), hasToken("\"hello world\"", PerlTokenType.STRING));
    }

    @Test
    public void integer_literals() {
        assertThat(lexer.lex("7"), hasToken("7", PerlTokenType.NUMBER));
    }

    @Test
    public void identifiers_and_keywords() {
        assertThat(lexer.lex("sub"), hasToken("sub", PerlKeyword.SUB));
        assertThat(lexer.lex("$identifier"), hasToken("$identifier", GenericTokenType.IDENTIFIER));
    }

    @Test
    public void operators_and_delimiters() {
        assertThat(lexer.lex("<<"), hasToken("<<", PerlPunctuator.LEFT_OP));
        assertThat(lexer.lex("+="), hasToken("+=", PerlPunctuator.PLUS_ASSIGN));
        assertThat(lexer.lex("@="), hasToken("@=", PerlPunctuator.MATRIX_MULT_ASSIGN));
    }

    @Test
    public void blank_lines() {
        assertThat(lexer.lex("    # comment\n")).hasSize(1);
        assertThat(lexer.lex("    \n")).hasSize(1);
        assertThat(lexer.lex("    ")).hasSize(1);
        assertThat(lexer.lex("line\n\n")).hasSize(3);
    }

    @Test
    public void implicit_line_joining() {
        assertThat(lexer.lex("['January', \n 'December']"), not(hasToken("\n", PerlTokenType.NEWLINE)));
    }

    @Test
    public void explicit_line_joining() {
        assertThat(lexer.lex("line\r\nline"), hasToken(PerlTokenType.NEWLINE));
        assertThat(lexer.lex("line\rline"), hasToken(PerlTokenType.NEWLINE));
        assertThat(lexer.lex("line\nline"), hasToken(PerlTokenType.NEWLINE));

        assertThat(lexer.lex("line\\\r\nline"), not(hasToken(PerlTokenType.NEWLINE)));
        assertThat(lexer.lex("line\\\rline"), not(hasToken(PerlTokenType.NEWLINE)));
        assertThat(lexer.lex("line\\\nline"), not(hasToken(PerlTokenType.NEWLINE)));

        assertThat(lexer.lex("line\\\n    line")).hasSize(3);
    }

}
