package com.github.sonarperl.lexer;


public class LexerState {

    int brackets;
    boolean joined;

    public void reset() {
        brackets = 0;
        joined = false;
    }

}
