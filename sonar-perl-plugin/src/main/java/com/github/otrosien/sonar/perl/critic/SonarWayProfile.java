package com.github.otrosien.sonar.perl.critic;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.utils.ValidationMessages;

public final class SonarWayProfile extends ProfileDefinition {

    private static final String PERL_PROFILE_XML = "com/github/otrosien/sonarperl/sonar-way-profile.xml";
    private final XMLProfileParser xmlProfileParser;

    public SonarWayProfile(XMLProfileParser xmlProfileParser) {
        this.xmlProfileParser = xmlProfileParser;
    }

    @Override
    public RulesProfile createProfile(ValidationMessages validation) {
        RulesProfile parsedResource = xmlProfileParser.parseResource(getClass().getClassLoader(),
                PERL_PROFILE_XML, validation);
        parsedResource.setDefaultProfile(true);
        return parsedResource;
    }
}
