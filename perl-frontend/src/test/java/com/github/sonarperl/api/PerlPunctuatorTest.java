package com.github.sonarperl.api;

import com.github.sonarperl.PerlPunctuator;
import com.sonar.sslr.api.AstNode;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class PerlPunctuatorTest {

    @Test
    public void test() {

        assertThat(PerlPunctuator.values()).hasSize(57);

        AstNode astNode = mock(AstNode.class);
        for (PerlPunctuator punctuator : PerlPunctuator.values()) {
            assertThat(punctuator.hasToBeSkippedFromAst(astNode)).isFalse();
        }
    }

}
