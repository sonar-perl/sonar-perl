package com.github.sonarperl.parser.toplevel;


import com.github.sonarperl.api.PerlGrammar;
import com.github.sonarperl.parser.RuleTest;
import org.junit.Before;
import org.junit.Test;

import static org.sonar.sslr.tests.Assertions.assertThat;

public class FileInputTest extends RuleTest {

    @Before
    public void init() {
        setRootRule(PerlGrammar.FILE_INPUT);
    }

    @Test
    public void ok() {
        p.getGrammar().rule(PerlGrammar.ATOM).mock();

        assertThat(p).matches("ATOM");
        assertThat(p).matches("ATOM ATOM");
        assertThat(p).matches("\n");
        assertThat(p).matches("ATOM\nATOM");
    }

}
