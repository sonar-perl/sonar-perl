package com.github.sonarperl.parser;

import com.github.sonarperl.PerlConfiguration;
import com.github.sonarperl.api.PerlGrammar;
import com.github.sonarperl.lexer.PerlLexer;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;

public final class PerlParser {
    private PerlParser(){}

    public static Parser<Grammar> create(PerlConfiguration conf) {
        return Parser.builder(PerlGrammar.create().build())
                .withLexer(PerlLexer.create(conf)).build();
    }
}
