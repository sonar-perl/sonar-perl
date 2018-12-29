package com.github.sonarperl.api;

import org.sonar.sslr.grammar.GrammarRuleKey;
import org.sonar.sslr.grammar.LexerfulGrammarBuilder;

public enum PerlGrammar implements GrammarRuleKey {

    // Top-level components
    FILE_INPUT;

    public static LexerfulGrammarBuilder create() {
        LexerfulGrammarBuilder b = LexerfulGrammarBuilder.create();
        b.rule(FILE_INPUT).is(b.zeroOrMore(b.anyToken()));
        b.setRootRule(FILE_INPUT);
        return b;
    }

}
