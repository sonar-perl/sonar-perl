package com.github.sonarperl;

import com.github.sonarperl.api.PerlGrammar;
import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PerlVisitorTest {

    @Test
    public void test() {
        TestVisitor visitor = new TestVisitor();
        TestPerlVisitorRunner.scanFile(new File("src/test/resources/visitor.pl"), visitor);
        // TODO: This deviates from the python impl (as we don't have a proper grammar yet)
        assertThat(visitor.atomValues).containsExactly("foo", "(", "\"x\"", "+", "42", ")");
        assertThat(visitor.fileName).isEqualTo("visitor.pl");
    }

    public class TestVisitor extends PerlVisitor {

        private final List<String> atomValues = new ArrayList<>();
        private String fileName;

        @Override
        public Set<AstNodeType> subscribedKinds() {
            return ImmutableSet.of(PerlGrammar.ATOM);
        }

        @Override
        public void visitNode(AstNode node) {
            atomValues.add(node.getTokenValue());
            fileName = getContext().perlFile().fileName();
        }

    }

}
