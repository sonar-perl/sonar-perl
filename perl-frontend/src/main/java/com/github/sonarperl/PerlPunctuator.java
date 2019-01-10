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
    LTLT("<<"),
    RIGHT_OP(">>"),
    TILDE_TILDE("~~"),

    /**
     * Bitwise AND.
     */
    AND("&"),

    /**
     * Bitwise OR.
     */
    OR("|"),
    XOR("^"),
    NOT("!"),
    TILDE("~"),
    LT("<"),
    GT(">"),
    LT_EQU("<="),
    GT_EQU(">="),
    EQU_TILD("=~"),
    EQU("=="),
    NOT_EQU("!="),
    NULL_FH("<>"),
    QUES_COLON("?:"),
    EQU_GT("=>"),
    MINUS_GT("->"),

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
    X_ASSIGN("x="),
    BW_ASSIGN1("^.="),
    BL_ASSIGN2("|.="),
    BL_ASSIGN3("&.=")
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
