package com.github.sonarperl.critic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonar.api.server.debt.DebtRemediationFunction.Type.LINEAR;

import java.util.List;

import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Rule;

import com.github.sonarperl.PerlLanguage;
import com.github.sonarperl.critic.PerlCriticRulesDefinition;

public class PerlCriticRulesDefinitionTest {

    @Test
    public void test() {
        PerlCriticRulesDefinition def = new PerlCriticRulesDefinition();
        RulesDefinition.Context context = new RulesDefinition.Context();
        def.define(context);
        RulesDefinition.Repository repository = context.repository(PerlCriticRulesDefinition.getRepositoryKey());

        assertThat(repository.name()).isEqualTo(PerlCriticRulesDefinition.getRepositoryName());
        assertThat(repository.language()).isEqualTo(PerlLanguage.KEY);

        List<Rule> rules = repository.rules();
        assertThat(rules).hasSize(272);

        for (Rule rule : rules) {
            assertThat(rule.debtRemediationFunction()).describedAs(rule.key()).isNotNull();
            assertThat(rule.debtRemediationFunction().type()).describedAs(rule.key()).isEqualTo(LINEAR);
        }
    }

}
