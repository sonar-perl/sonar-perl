package com.github.sonarperl.parser;


import com.github.sonarperl.PerlConfiguration;
import com.sonar.sslr.api.Grammar;
import com.sonar.sslr.impl.Parser;
import org.sonar.sslr.grammar.GrammarRuleKey;

import java.nio.charset.StandardCharsets;

abstract public class RuleTest {

    protected Parser<Grammar> p = PerlParser.create(new PerlConfiguration(StandardCharsets.UTF_8));

    protected void setRootRule(GrammarRuleKey ruleKey) {
        p.setRootRule(p.getGrammar().rule(ruleKey));
    }
}

