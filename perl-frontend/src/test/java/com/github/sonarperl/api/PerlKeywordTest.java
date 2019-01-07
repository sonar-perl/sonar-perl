package com.github.sonarperl.api;

import org.junit.Test;


import static org.assertj.core.api.Assertions.assertThat;

public class PerlKeywordTest {

    @Test
    public void test() {
        assertThat(PerlKeyword.values()).hasSize(228);
        assertThat(PerlKeyword.keywordValues()).hasSize(PerlKeyword.values().length);
    }
}
