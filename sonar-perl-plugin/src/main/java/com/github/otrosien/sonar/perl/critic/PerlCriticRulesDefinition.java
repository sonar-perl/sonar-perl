package com.github.otrosien.sonar.perl.critic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.sonar.api.server.debt.DebtRemediationFunction;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.api.utils.MessageException;

import com.github.otrosien.sonar.perl.PerlLanguage;

public final class PerlCriticRulesDefinition implements RulesDefinition {

    private static final String PERLCRITIC_RULES_XML = "/com/github/otrosien/sonarperl/perlcritic-rules.xml";
    private static final String COST_FILE_CSV = "/com/github/otrosien/sonarperl/cost.csv";
    private static final String KEY = "PerlCritic";
    private static final String NAME = "PerlCritic";

    private String rulesDefinitionFilePath() {
        return PERLCRITIC_RULES_XML;
    }

    private void defineRulesForLanguage(Context context, String repositoryKey, String repositoryName,
            String languageKey) {
        NewRepository repository = context.createRepository(repositoryKey, languageKey).setName(repositoryName);

        try(InputStream rulesXml = this.getClass().getResourceAsStream(rulesDefinitionFilePath())) {
            if (rulesXml != null) {
                RulesDefinitionXmlLoader rulesLoader = new RulesDefinitionXmlLoader();
                rulesLoader.load(repository, rulesXml, StandardCharsets.UTF_8.name());
                addRemediationCost(repository.rules());
            }
        } catch (IOException e) {
            throw MessageException.of("Unable to load rules defintion", e);
        }

        repository.done();
    }

    @Override
    public void define(Context context) {
        String repositoryKey = PerlCriticRulesDefinition.getRepositoryKey();
        String repositoryName = PerlCriticRulesDefinition.getRepositoryName();
        defineRulesForLanguage(context, repositoryKey, repositoryName, PerlLanguage.KEY);
    }

    public static String getRepositoryKey() {
        return KEY;
    }

    public static String getRepositoryName() {
        return NAME;
    }

    private static void addRemediationCost(Collection<NewRule> rules) {
        Map<String, String> costByRule = getCostByRule();
        for (NewRule newRule : rules) {
            String ruleKey = newRule.key();
            if (costByRule.containsKey(ruleKey)) {
                DebtRemediationFunction linear = newRule.debtRemediationFunctions().linear(costByRule.get(ruleKey));
                newRule.setDebtRemediationFunction(linear);
            }
        }
    }

    private static Map<String, String> getCostByRule() {
        Map<String, String> result = new HashMap<>();

        try (InputStream stream = PerlCriticRulesDefinition.class.getResourceAsStream(COST_FILE_CSV);
                Stream< String>lines = new BufferedReader(new InputStreamReader(stream)).lines()) {
            lines //
                    .skip(1) // header line
                    .forEach(line -> PerlCriticRulesDefinition.completeCost(line, result));
        } catch (IOException e) {
            throw MessageException.of("Unable to load rules remediation function/factor", e);
        }
        return result;
    }

    private static void completeCost(String line, Map<String, String> costByRule) {
        String[] blocks = line.split(";");
        String ruleKey = blocks[0];
        // block 1 contains the function (always linear)
        String ruleCost = blocks[2];
        costByRule.put(ruleKey, ruleCost);
    }

}
