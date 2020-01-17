package com.github.sonarperl.highlighter;

import com.github.sonarperl.PerlVisitor;
import com.github.sonarperl.TokenLocation;
import com.github.sonarperl.api.PerlGrammar;
import com.github.sonarperl.api.PerlKeyword;
import com.github.sonarperl.api.PerlTokenType;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.highlighting.NewHighlighting;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.sslr.grammar.GrammarRuleKey;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PerlHighlighter extends PerlVisitor {

    private static final Logger log = Loggers.get(PerlHighlighter.class);

    private NewHighlighting newHighlighting;

    public PerlHighlighter(SensorContext context, InputFile inputFile) {
        newHighlighting = context.newHighlighting();
        newHighlighting.onFile(inputFile);
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        Set<GrammarRuleKey> s = new HashSet<>();
        s.add(PerlGrammar.FILE_INPUT);
        return Collections.unmodifiableSet(s);
    }

    @Override
    public void visitToken(Token token) {
        super.visitToken(token);
        if (token.getType().equals(PerlTokenType.NUMBER)) {
            highlight(token, TypeOfText.CONSTANT);

        } else if (token.getType() instanceof PerlKeyword) {
            highlight(token, TypeOfText.KEYWORD);

        } else if (token.getType().equals(PerlTokenType.STRING)) {
            highlight(token, TypeOfText.STRING);
        }

        for (Trivia trivia : token.getTrivia()) {
            highlight(trivia.getToken(), TypeOfText.COMMENT);
        }
    }

    @Override
    public void leaveFile(@Nullable AstNode astNode) {
        super.leaveFile(astNode);
        newHighlighting.save();
    }

    private void highlight(Token token, TypeOfText typeOfText) {
        try {
            TokenLocation tokenLocation = new TokenLocation(token);
            newHighlighting.highlight(tokenLocation.startLine(), tokenLocation.startLineOffset(), tokenLocation.endLine(), tokenLocation.endLineOffset(), typeOfText);
        } catch (IllegalArgumentException e) {
            log.warn("Error highlighting token in file: " + token.getURI().toString(), e);
        }
    }


}
