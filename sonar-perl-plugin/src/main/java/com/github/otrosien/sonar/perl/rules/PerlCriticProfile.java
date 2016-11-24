package com.github.otrosien.sonar.perl.rules;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.utils.ValidationMessages;

public final class PerlCriticProfile extends ProfileDefinition {

    private final XMLProfileParser xmlProfileParser;

    public PerlCriticProfile(XMLProfileParser xmlProfileParser) {
      this.xmlProfileParser = xmlProfileParser;
    }

    @Override
    public RulesProfile createProfile(ValidationMessages validation) {
      return xmlProfileParser.parseResource(getClass().getClassLoader(), "perlcritic-profile.xml", validation);
    }
}
