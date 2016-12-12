package com.github.otrosien.sonar.perl.critic;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;

import com.github.otrosien.sonar.perl.PerlLanguage;

public final class PerlCriticRulesDefinition implements RulesDefinition {

    private static final String PERLCRITIC_RULES_XML = "/perlcritic-rules.xml";
    private static final String KEY = "PerlCritic";
    private static final String NAME = "PerlCritic";

    protected String rulesDefinitionFilePath() {
        return PERLCRITIC_RULES_XML;
    }

    private void defineRulesForLanguage(Context context, String repositoryKey, String repositoryName,
            String languageKey) {
        NewRepository repository = context.createRepository(repositoryKey, languageKey).setName(repositoryName);

        InputStream rulesXml = this.getClass().getResourceAsStream(rulesDefinitionFilePath());
        if (rulesXml != null) {
            RulesDefinitionXmlLoader rulesLoader = new RulesDefinitionXmlLoader();
            rulesLoader.load(repository, rulesXml, StandardCharsets.UTF_8.name());
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

}
