package com.github.sonarperl.critic;

class PerlCriticViolation {

    private final String type;
    private final String description;
    private final String filePath;
    private final int line;
    private final String severity;

    public PerlCriticViolation(final String type, final String description, final String filePath, final int line, final String severity) {
        this.type = type;
        this.description = description;
        this.filePath = filePath;
        this.line = line;
        this.severity = severity;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getLine() {
        return line;
    }

    public String getSeverity() {
        return severity;
    }

    @Override
    public String toString() {
        String s = type +
                "|" +
                description +
                "|" +
                filePath +
                "(" +
                line +
                ")";
        return s;
    }
}