package com.github.sonarperl.critic;

import com.github.sonarperl.PerlLanguage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.utils.ValidationMessages;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class SonarWayProfileTest {

    @Test
    public void should_create_perlcritic_profile() {
        BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
        ValidationMessages validation = ValidationMessages.create();
        SonarWayProfile definition = new SonarWayProfile();
        definition.define(context);
        assertThat(context.profilesByLanguageAndName()).containsOnlyKeys(PerlLanguage.KEY);

        Map<String, BuiltInQualityProfilesDefinition.BuiltInQualityProfile> perlProfiles = context.profilesByLanguageAndName().get(PerlLanguage.KEY);
        assertThat(perlProfiles).containsOnlyKeys("PerlCritic");

        BuiltInQualityProfilesDefinition.BuiltInQualityProfile profile = perlProfiles.get("PerlCritic");
        assertThat(profile.name()).isEqualTo("PerlCritic");
        assertThat(profile.rules()).extracting("repoKey").containsOnly("PerlCritic");
        assertThat(validation.hasErrors()).isFalse();
        assertThat(profile.rules().size()).isGreaterThan(87);
    }

}
