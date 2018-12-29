package com.github.sonarperl;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.TokenType;

public enum PerlPunctuator implements TokenType {

    PLUS("+"),
    MINUS("-"),
    MUL("*"),
    MUL_MUL("**"),
    DIV("/"),
    DIV_DIV("//"),
    MOD("%"),
    LEFT_OP("<<"),
    RIGHT_OP(">>"),

    /**
     * Bitwise AND.
     */
    AND("&"),

    /**
     * Bitwise OR.
     */
    OR("|"),
    XOR("^"),
    TILDE("~"),
    LT("<"),
    GT(">"),
    LT_EQU("<="),
    GT_EQU(">="),
    EQU("=="),
    NOT_EQU("!="),
    NOT_EQU2("<>"),

    // Delimiters

    BACKTICK("`"),
    LPARENTHESIS("("),
    RPARENTHESIS(")"),
    LBRACKET("["),
    RBRACKET("]"),
    LCURLYBRACE("{"),
    RCURLYBRACE("}"),
    COMMA(","),
    COLON(":"),
    DOT("."),
    SEMICOLON(";"),
    AT("@"),
    ASSIGN("="),
    PLUS_ASSIGN("+="),
    MINUS_ASSIGN("-="),
    MUL_ASSIGN("*="),
    DIV_ASSIGN("/="),
    DIV_DIV_ASSIGN("//="),
    MOD_ASSIGN("%="),
    AND_ASSIGN("&="),
    OR_ASSIGN("|="),
    XOR_ASSIGN("^="),
    RIGHT_ASSIGN(">>="),
    LEFT_ASSIGN("<<="),
    MUL_MUL_ASSIGN("**="),
    MATRIX_MULT_ASSIGN("@=")

    ;

    private final String value;

    PerlPunctuator(String word) {
        this.value = word;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean hasToBeSkippedFromAst(AstNode node) {
        return false;
    }
}
