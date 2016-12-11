package com.github.otrosien.sonar.perl.critic;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Rule;

import com.github.otrosien.sonar.perl.PerlLanguage;

public class PerlCriticRulesDefinitionTest {

    @Test
    public void test() {
        PerlCriticRulesDefinition def = new PerlCriticRulesDefinition();
        RulesDefinition.Context context = new RulesDefinition.Context();
        def.define(context);
        RulesDefinition.Repository repository = context.repository(PerlCriticRulesDefinition.KEY);

        assertThat(repository.name()).isEqualTo(PerlCriticRulesDefinition.NAME);
        assertThat(repository.language()).isEqualTo(PerlLanguage.KEY);

        List<Rule> rules = repository.rules();
        assertThat(rules).hasSize(271);
    }

}
