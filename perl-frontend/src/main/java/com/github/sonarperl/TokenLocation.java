package com.github.sonarperl;


import com.sonar.sslr.api.Token;

public class TokenLocation {

    private final int startLine;
    private final int startLineOffset;
    private final int endLine;
    private final int endLineOffset;

    public TokenLocation(Token token) {
        this.startLine = token.getLine();
        this.startLineOffset = token.getColumn();

        String value = token.getValue();
        String[] lines = value.split("\r\n|\n|\r", -1);

        if (lines.length > 1) {
            endLine = token.getLine() + lines.length - 1;
            endLineOffset = lines[lines.length - 1].length();

        } else {
            this.endLine = this.startLine;
            this.endLineOffset = this.startLineOffset + token.getValue().length();
        }
    }

    public int startLine() {
        return startLine;
    }

    public int startLineOffset() {
        return startLineOffset;
    }

    public int endLine() {
        return endLine;
    }

    public int endLineOffset() {
        return endLineOffset;
    }
}
