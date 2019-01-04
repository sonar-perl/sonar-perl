package com.github.sonarperl.api;


import com.sonar.sslr.api.AstNode;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class PerlTokenTypeTest {

    @Test
    public void test() {
        assertThat(PerlTokenType.values()).hasSize(3);

        AstNode astNode = mock(AstNode.class);
        for (PerlTokenType tokenType : PerlTokenType.values()) {
            assertThat(tokenType.getName()).isEqualTo(tokenType.name());
            assertThat(tokenType.getValue()).isEqualTo(tokenType.name());
            assertThat(tokenType.hasToBeSkippedFromAst(astNode)).isFalse();
        }
    }

}
