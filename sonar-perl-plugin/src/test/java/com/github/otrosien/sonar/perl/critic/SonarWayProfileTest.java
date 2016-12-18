package com.github.otrosien.sonar.perl.critic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.ValidationMessages;

import com.github.otrosien.sonar.perl.PerlLanguage;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("deprecation")
public class SonarWayProfileTest {

    @Mock
    RuleFinder ruleFinder;

    @Test
    public void should_create_perlcritic_profile() {
      ValidationMessages validation = ValidationMessages.create();
      SonarWayProfile definition = new SonarWayProfile(new XMLProfileParser(ruleFinder()));
      RulesProfile profile = definition.createProfile(validation);
      assertThat(profile.getLanguage()).isEqualTo(PerlLanguage.KEY);
      assertThat(profile.getName()).isEqualTo("Sonar way");
      assertThat(profile.getActiveRules()).extracting("repositoryKey").containsOnly("PerlCritic", "common-perl");
      assertThat(validation.hasErrors()).isFalse();
      assertThat(profile.getActiveRules().size()).isGreaterThan(87);

    }

    RuleFinder ruleFinder() {
      return when(ruleFinder.findByKey(anyString(), anyString())).thenAnswer(new Answer<Rule>() {
        @Override
        public Rule answer(InvocationOnMock invocation) {
          Object[] arguments = invocation.getArguments();
          return Rule.create((String) arguments[0], (String) arguments[1], (String) arguments[1]);
        }
      }).getMock();
    }

}
