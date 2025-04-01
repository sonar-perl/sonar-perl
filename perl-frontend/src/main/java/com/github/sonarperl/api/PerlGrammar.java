package com.github.sonarperl.api;

import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerfulGrammarBuilder;

import static com.github.sonarperl.api.PerlTokenType.NEWLINE;
import static com.sonar.sslr.api.GenericTokenType.EOF;

public enum PerlGrammar implements GrammarRuleKey {

    ATOM,
    // Top-level components
    FILE_INPUT;

    public static LexerfulGrammarBuilder create() {
        LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();
        b.rule(FILE_INPUT).is(b.zeroOrMore(b.firstOf(NEWLINE, ATOM)), EOF);
        b.rule(ATOM).is(b.firstOf(
                PerlTokenType.NUMBER,
                PerlTokenType.STRING,
                b.anyTokenButNot(EOF)
                ));
        b.setRootRule(FILE_INPUT);
        b.buildWithMemoizationOfMatchesForAllRules();
        return b;
    }

}
