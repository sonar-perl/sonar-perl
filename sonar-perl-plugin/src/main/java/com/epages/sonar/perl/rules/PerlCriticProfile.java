package com.epages.sonar.perl.rules;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.utils.ValidationMessages;

import com.epages.sonar.perl.PerlLanguage;

public final class PerlCriticProfile extends ProfileDefinition {

  @Override
  public RulesProfile createProfile(ValidationMessages validation) {
    return RulesProfile.create("PerlLint Rules", PerlLanguage.KEY);
  }
}
