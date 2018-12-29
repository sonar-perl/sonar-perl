package com.github.sonarperl.lexer;



import java.util.ArrayDeque;
import java.util.Deque;

public class LexerState {

    final Deque<Integer> indentationStack = new ArrayDeque<>();

    int brackets;
    boolean joined;

    public void reset() {
        indentationStack.clear();
        indentationStack.push(0);

        brackets = 0;
        joined = false;
    }

}
