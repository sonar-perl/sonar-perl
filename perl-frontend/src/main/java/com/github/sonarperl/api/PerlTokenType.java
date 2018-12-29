package com.github.sonarperl.api;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.TokenType;

public enum PerlTokenType implements TokenType {
    NEWLINE,
    NUMBER,
    STRING;

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getValue() {
        return name();
    }

    @Override
    public boolean hasToBeSkippedFromAst(AstNode node) {
        return false;
    }
}
