package com.github.sonarperl.critic;

import com.github.sonarperl.PerlLanguage;
import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Rule;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonar.api.server.debt.DebtRemediationFunction.Type.LINEAR;

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
        assertThat(rules).hasSize(291);

        for (Rule rule : rules) {
            assertThat(rule.debtRemediationFunction()).describedAs(rule.key()).isNotNull();
            assertThat(rule.debtRemediationFunction().type()).describedAs(rule.key()).isEqualTo(LINEAR);
        }
    }

}
