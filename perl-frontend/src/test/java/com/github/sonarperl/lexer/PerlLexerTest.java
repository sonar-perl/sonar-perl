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
    public void string_literals() {
        assertThat("empty", lexer.lex("''"), hasToken("''", PerlTokenType.STRING));
        assertThat("empty", lexer.lex("\"\""), hasToken("\"\"", PerlTokenType.STRING));
        assertThat(lexer.lex("'hello world'"), hasToken("'hello world'", PerlTokenType.STRING));
        assertThat(lexer.lex("\"hello world\""), hasToken("\"hello world\"", PerlTokenType.STRING));
    }

    @Test
    public void quote_q() {
        assertThat("empty", lexer.lex("q{}"), hasToken("q{}", PerlTokenType.STRING));
        assertThat("empty", lexer.lex("q//"), hasToken("q//", PerlTokenType.STRING));
        assertThat(lexer.lex("q['hello]"), hasToken("q['hello]", PerlTokenType.STRING));
    }

    @Test
    public void quote_qq() {
        assertThat("empty", lexer.lex("qq{}"), hasToken("qq{}", PerlTokenType.STRING));
        assertThat("empty", lexer.lex("qq//"), hasToken("qq//", PerlTokenType.STRING));
        assertThat(lexer.lex("qq['hello]"), hasToken("qq['hello]", PerlTokenType.STRING));
        assertThat(lexer.lex("qq['hello\n]"), hasToken("qq['hello\n]", PerlTokenType.STRING));
        assertThat(lexer.lex("qq[[]nested[]]"), hasToken("qq[[]nested[]]", PerlTokenType.STRING));
        assertThat(lexer.lex("qq[[[double nested]]]"), hasToken("qq[[[double nested]]]", PerlTokenType.STRING));
    }

    @Test
    public void quote_s() {
        assertThat("empty", lexer.lex("s///"), hasToken("s///", PerlTokenType.STRING));
        assertThat("simple", lexer.lex("s/a/b/"), hasToken("s/a/b/", PerlTokenType.STRING));
        assertThat("generic", lexer.lex("s{a}{b}"), hasToken("s{a}{b}", PerlTokenType.STRING));
        assertThat("escape", lexer.lex("s/\\/\\//|/"), hasToken("s/\\/\\//|/", PerlTokenType.STRING));
        assertThat("suffix", lexer.lex("s/\\/\\//|/; # some bla"), hasToken("s/\\/\\//|/", PerlTokenType.STRING));
    }

    @Test
    public void quote_y() {
        assertThat("empty", lexer.lex("y[][]"), hasToken("y[][]", PerlTokenType.STRING));
        assertThat("simple", lexer.lex("y#a-z#A-Z#"), hasToken("y#a-z#A-Z#", PerlTokenType.STRING));
        assertThat("suffix", lexer.lex("y#a-z#A-Z#; $x"), hasToken("$x", GenericTokenType.IDENTIFIER));
        assertThat("second", lexer.lex("y#a-z#A-Z#; y#m#n#;"), hasToken("y#m#n#", PerlTokenType.STRING));
    }

    @Test
    public void quote_qx() {
        assertThat("empty", lexer.lex("qx<>"), hasToken("qx<>", PerlTokenType.STRING));
        assertThat("simple", lexer.lex("qx/a.*b/"), hasToken("qx/a.*b/", PerlTokenType.STRING));
    }

    @Test
    public void quote_tr() {
        assertThat("empty", lexer.lex("tr{}{}"), hasToken("tr{}{}", PerlTokenType.STRING));
        assertThat("simple", lexer.lex("tr/a/b/"), hasToken("tr/a/b/", PerlTokenType.STRING));
    }

    @Test
    public void number_literals() {
        assertThat(lexer.lex("7"), hasToken("7", PerlTokenType.NUMBER));
        assertThat(lexer.lex("12.34e-56"), hasToken("12.34e-56", PerlTokenType.NUMBER));
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
        assertThat(lexer.lex("&.="), hasToken("&.=", PerlPunctuator.BL_ASSIGN3));
    }

    @Test
    public void blank_lines() {
        assertThat(lexer.lex("    # comment\n")).hasSize(1);
        assertThat(lexer.lex("    \n")).hasSize(1);
        assertThat(lexer.lex("    ")).hasSize(1);
        assertThat(lexer.lex("line\n\n")).hasSize(3);
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

    @Test
    public void pod_lines() {
        assertThat(lexer.lex("=pod\n=cut"), hasToken(GenericTokenType.COMMENT));
        assertThat(lexer.lex("=pod\nbablabla"), hasToken(GenericTokenType.COMMENT));
        assertThat(lexer.lex("=pod\ntest\n=cut\n=pod\n=cut\nblabla"), hasToken(GenericTokenType.IDENTIFIER));
        assertThat(lexer.lex("=head1 Test\ntest\n=cut"), hasToken(GenericTokenType.COMMENT));
    }

}
