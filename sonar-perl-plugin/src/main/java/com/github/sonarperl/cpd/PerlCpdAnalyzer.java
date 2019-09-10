package com.github.sonarperl.cpd;

import com.github.sonarperl.PerlVisitorContext;
import com.github.sonarperl.TokenLocation;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Token;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.cpd.NewCpdTokens;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class PerlCpdAnalyzer {

    private static final Logger LOG = LoggerFactory.getLogger(PerlCpdAnalyzer.class);

    private final SensorContext context;

    public PerlCpdAnalyzer(SensorContext context) {
        this.context = context;
    }

    public void pushCpdTokens(InputFile inputFile, PerlVisitorContext visitorContext){
        NewCpdTokens cpdTokens = context.newCpdTokens().onFile(inputFile);
        AstNode root = visitorContext.rootTree();
        if (root != null) {
            List<Token> tokens = root.getTokens();
            for (Token token : tokens) {
                try {
                    TokenLocation location = new TokenLocation(token);
                    cpdTokens.addToken(location.startLine(), location.startLineOffset(), location.endLine(), location.endLineOffset(), token.getValue());
                } catch (Exception ex) {
                    int tokenOffset = token.getColumn() + token.getValue().length();
                    LOG.warn("Token error at line: " + token.getLine() +
                            ", columns: " + token.getColumn() +
                            ", " + tokenOffset + " is not a valid line offset for pointer",
                            ex );
                }
            }
        }
        synchronized (this) {
            cpdTokens.save();
        }
    }
}
