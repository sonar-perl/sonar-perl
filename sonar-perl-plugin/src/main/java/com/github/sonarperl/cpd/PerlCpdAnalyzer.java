package com.github.sonarperl.cpd;

import com.github.sonarperl.PerlVisitor;
import com.github.sonarperl.PerlVisitorContext;
import com.github.sonarperl.TokenLocation;
import com.github.sonarperl.api.PerlGrammar;
import com.github.sonarperl.api.PerlTokenType;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.GenericTokenType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.TokenType;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.cpd.NewCpdTokens;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PerlCpdAnalyzer {

    private static final Logger LOG = Loggers.get(PerlCpdAnalyzer.class);

    private final SensorContext context;

    public PerlCpdAnalyzer(SensorContext context) {
        this.context = context;
    }

    public void pushCpdTokens(InputFile inputFile, PerlVisitorContext visitorContext) {
        NewCpdTokens cpdTokens = context.newCpdTokens().onFile(inputFile);
        for (Token token : new TokenVisitor(visitorContext).tokens()) {
            try {
                if (!isIgnoredType(token.getType())) {
                    TokenLocation location = new TokenLocation(token);
                    cpdTokens.addToken(location.startLine(), location.startLineOffset(), location.endLine(), location.endLineOffset(), token.getValue());
                }
            } catch (Exception ex) {
                int tokenOffset = token.getColumn() + token.getValue().length();
                LOG.warn("Token error at line: " + token.getLine() +
                                ", columns: " + token.getColumn() +
                                ", " + tokenOffset + " is not a valid line offset for pointer",
                        ex);
            }
        }
        cpdTokens.save();
    }

    private static boolean isIgnoredType(TokenType type) {
        return type.equals(PerlTokenType.NEWLINE) || type.equals(GenericTokenType.EOF) || type.equals(GenericTokenType.COMMENT);
    }

    static class TokenVisitor extends PerlVisitor {

        private final PerlVisitorContext visitorContext;
        private final List<Token> tokenValues = new ArrayList<>();
        private static final Set<AstNodeType> subscribedKinds = new HashSet<>(List.of(PerlGrammar.ATOM));

        private TokenVisitor(PerlVisitorContext visitorContext) {
            this.visitorContext = visitorContext;
        }

        @Override
        public Set<AstNodeType> subscribedKinds() {
            return subscribedKinds;
        }

        @Override
        public void visitToken(Token token) {
            tokenValues.add(token);
        }

        private List<Token> tokens() {
            scanFile(visitorContext);
            return tokenValues;
        }

    }
}
