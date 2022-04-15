package com.github.sonarperl.critic;

import org.sonar.api.rule.Severity;

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
        StringBuilder s = new StringBuilder();
        s.append(type);
        s.append("|");
        s.append(description);
        s.append("|");
        s.append(filePath);
        s.append("(");
        s.append(line);
        s.append(")");
        return s.toString();
    }
}