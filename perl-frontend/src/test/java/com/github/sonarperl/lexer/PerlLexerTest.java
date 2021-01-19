package com.github.sonarperl.lexer;

import com.github.sonarperl.PerlConfiguration;
import com.github.sonarperl.api.PerlKeyword;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.impl.Lexer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static com.github.sonarperl.PerlPunctuator.*;
import static com.github.sonarperl.api.PerlKeyword.EXIT;
import static com.github.sonarperl.api.PerlKeyword.IF;
import static com.github.sonarperl.api.PerlTokenType.*;
import static com.sonar.sslr.api.GenericTokenType.COMMENT;
import static com.sonar.sslr.api.GenericTokenType.IDENTIFIER;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasComment;
import static com.sonar.sslr.test.lexer.LexerMatchers.hasToken;
import static org.assertj.core.api.Assertions.assertThat;
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
        assertThat("empty", lexer.lex("''"), hasToken("''", STRING));
        assertThat("empty", lexer.lex("\"\""), hasToken("\"\"", STRING));
        assertThat(lexer.lex("'hello world'"), hasToken("'hello world'", STRING));
        assertThat(lexer.lex("\"hello world\""), hasToken("\"hello world\"", STRING));
        assertThat(lexer.lex("`hello world`"), hasToken("`hello world`", STRING));
    }

    @Test
    public void quote_q() {
        assertThat("empty", lexer.lex("q{}"), hasToken("q{}", STRING));
        assertThat("empty", lexer.lex("q//"), hasToken("q//", STRING));
        assertThat("simple", lexer.lex("$foo = q!I said, \"You said, 'She said it.'\"!;"), hasToken("q!I said, \"You said, 'She said it.'\"!", STRING));
        assertThat(lexer.lex("q['hello]"), hasToken("q['hello]", STRING));
    }

    @Test
    public void quote_m() {
        assertThat("empty", lexer.lex("$a =~ m//"), hasToken("m//", STRING));
        assertThat("raw", lexer.lex("$a =~ //"), hasToken("//", STRING));
        assertThat("negative_match_raw", lexer.lex("$a !~ //"), hasToken("//", STRING));
        assertThat("no_false_positive", lexer.lex("$a / $b / $c"), not(hasToken("/ $b /", STRING)));
        assertThat("no_false_positive", lexer.lex("$a / $b / $c"), hasToken("/", DIV));
    }

    @Test
    public void quote_qq() {
        assertThat("empty", lexer.lex("qq{}"), hasToken("qq{}", STRING));
        assertThat("empty", lexer.lex("qq//"), hasToken("qq//", STRING));
        assertThat(lexer.lex("qq['hello]"), hasToken("qq['hello]", STRING));
        assertThat(lexer.lex("qq['hello\n]"), hasToken("qq['hello\n]", STRING));
        assertThat(lexer.lex("qq[[]nested[]]"), hasToken("qq[[]nested[]]", STRING));
        assertThat(lexer.lex("qq[[[double nested]]]"), hasToken("qq[[[double nested]]]", STRING));
    }

    @Test
    public void quote_s() {
        assertThat("empty", lexer.lex("s///"), hasToken("s///", STRING));
        assertThat("simple", lexer.lex("s/a/b/"), hasToken("s/a/b/", STRING));
        assertThat("generic", lexer.lex("s{a}{b}"), hasToken("s{a}{b}", STRING));
        assertThat("escape", lexer.lex("s/\\/\\//|/"), hasToken("s/\\/\\//|/", STRING));
        assertThat("suffix", lexer.lex("s/\\/\\//|/; # some bla"), hasToken("s/\\/\\//|/", STRING));
    }

    @Test
    public void quote_y() {
        assertThat("empty", lexer.lex("y[][]"), hasToken("y[][]", STRING));
        assertThat("simple", lexer.lex("y#a-z#A-Z#"), hasToken("y#a-z#A-Z#", STRING));
        assertThat("suffix", lexer.lex("y#a-z#A-Z#; $x"), hasToken("$x", IDENTIFIER));
        assertThat("second", lexer.lex("y#a-z#A-Z#; y#m#n#;"), hasToken("y#m#n#", STRING));
    }

    @Test
    public void quote_qx() {
        assertThat("empty", lexer.lex("qx<>"), hasToken("qx<>", STRING));
        assertThat("simple", lexer.lex("qx/a.*b/"), hasToken("qx/a.*b/", STRING));
    }

    @Test
    public void quote_tr() {
        assertThat("empty", lexer.lex("tr{}{}"), hasToken("tr{}{}", STRING));
        assertThat("simple", lexer.lex("tr/a/b/"), hasToken("tr/a/b/", STRING));
    }

    @Test
    public void number_literals() {
        assertThat(lexer.lex("7"), hasToken("7", NUMBER));
        assertThat(lexer.lex("12.34e-56"), hasToken("12.34e-56", NUMBER));
    }

    @Test
    public void identifiers_and_keywords() {
        assertThat(lexer.lex("sub"), hasToken("sub", PerlKeyword.SUB));
        assertThat(lexer.lex("$identifier"), hasToken("$identifier", GenericTokenType.IDENTIFIER));
    }

    @Test
    public void operators_and_delimiters() {
        assertThat(lexer.lex("<<"), hasToken("<<", LTLT));
        assertThat(lexer.lex("+="), hasToken("+=", PLUS_ASSIGN));
        assertThat(lexer.lex("&.="), hasToken("&.=", BL_ASSIGN3));
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
        assertThat(lexer.lex("line\r\nline"), hasToken(NEWLINE));
        assertThat(lexer.lex("line\rline"), hasToken(NEWLINE));
        assertThat(lexer.lex("line\nline"), hasToken(NEWLINE));

        assertThat(lexer.lex("line\\\r\nline"), not(hasToken(NEWLINE)));
        assertThat(lexer.lex("line\\\rline"), not(hasToken(NEWLINE)));
        assertThat(lexer.lex("line\\\nline"), not(hasToken(NEWLINE)));

        assertThat(lexer.lex("line\\\n    line")).hasSize(3);
    }

    @Test
    public void pod_lines() {
        assertThat(lexer.lex("=pod\n=cut"), hasToken(COMMENT));
        assertThat(lexer.lex("=pod\nbablabla"), hasToken(COMMENT));
        assertThat(lexer.lex("=pod\ntest\n=cut\n=pod\n=cut\nblabla"), hasToken(IDENTIFIER));
        assertThat(lexer.lex("=head1 Test\ntest\n=cut"), hasToken(COMMENT));
    }

    @Test
    public void heredoc() {
        assertThat(lexer.lex("print << EOL;\nbla blub\nEOL"), hasToken(STRING));
        assertThat(lexer.lex("<<EOL;\nbla blub\nEOL\n<<EOL;\nbla blu\nEOL")).hasSize(14);
        assertThat(lexer.lex(
                "my $code = <<EOCODE if @_ > 5;\n" +
                "sub { use utf8;\n" +
                "EOCODE")).extracting(Token::getType)
                .containsSequence(LTLT, IDENTIFIER, IF,
                        IDENTIFIER, GT, NUMBER, SEMICOLON, NEWLINE,
                        STRING, IDENTIFIER);
        assertThat(lexer.lex("<<'EOL';\nbla blub\nEOL\n")).extracting(Token::getType)
                .containsSequence(LTLT, STRING, SEMICOLON, NEWLINE,
                        STRING, IDENTIFIER);
        assertThat(lexer.lex("<<\"EOL\";\nbla blub\nEOL\n")).extracting(Token::getType)
                .containsSequence(LTLT, STRING, SEMICOLON, NEWLINE,
                        STRING, IDENTIFIER);
    }

    @Test
    public void heredoc2() {
        assertThat(lexer.lex(
                "sub usage {\n" +
                "    print <<EOTEXT;\n" +
                        "Usage: $0 --file=<filename>\n" +
                        "Some more text\n" +
                        "EOTEXT\n" +
                        "\n" +
                        "    exit 1;\n" +
                        "}\n")).extracting(Token::getType)
                .containsSequence(LTLT, IDENTIFIER, SEMICOLON,
                        NEWLINE, STRING, IDENTIFIER, NEWLINE, EXIT, NUMBER, SEMICOLON, NEWLINE, RCURLYBRACE);
    }

    @Test
    public void heredocWithPodNested() {
        assertThat(lexer.lex(
                        "print <<EOTEXT;\n" +
                        "=head1 Test \n" +
                        "=cut\n" +
                        "EOTEXT\n" +
                        "1;\n")).extracting(Token::getType)
                .containsSequence(LTLT, IDENTIFIER, SEMICOLON,
                        NEWLINE, STRING, IDENTIFIER, NEWLINE, NUMBER, SEMICOLON);
    }

    @Test
    public void podWithHeredocNested() {
        assertThat(lexer.lex(
                "=head1 Some Test\n" +
                "print <<EOL;\n" +
                "bla \n" +
                "EOL\n" +
                "=cut\n" +
                "1;\n")).extracting(Token::getType)
                .containsSequence(COMMENT, NEWLINE, NUMBER, SEMICOLON);
    }
}
