package com.epages.sonar.perl.rules;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;

import com.epages.sonar.perl.PerlLanguage;

public final class PerlCriticRulesDefinition implements RulesDefinition {

  protected static final String KEY = "perlcritic";
  protected static final String NAME = "PerlCritic";

  protected String rulesDefinitionFilePath() {
    return "/perlcritic-rules.xml";
  }

  private void defineRulesForLanguage(Context context, String repositoryKey, String repositoryName, String languageKey) {
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
    String repositoryKey = PerlCriticRulesDefinition.getRepositoryKeyForLanguage(PerlLanguage.KEY);
    String repositoryName = PerlCriticRulesDefinition.getRepositoryNameForLanguage(PerlLanguage.KEY);
    defineRulesForLanguage(context, repositoryKey, repositoryName, PerlLanguage.KEY);
  }

  public static String getRepositoryKeyForLanguage(String languageKey) {
    return languageKey.toLowerCase() + "-" + KEY;
  }

  public static String getRepositoryNameForLanguage(String languageKey) {
    return languageKey.toUpperCase() + " " + NAME;
  }

}
