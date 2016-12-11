package com.github.otrosien.sonar.perl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.sonar.api.config.Settings;

public class PerlLanguageTest {

    @Test
    public void testDefaults() {
        PerlLanguage lang = new PerlLanguage(new Settings());
        assertThat(lang.getFileSuffixes()).isEqualTo(new String[] {"pm","pl","t"});
        assertThat(lang.hasValidSuffixes("my.file")).isFalse();
        assertThat(lang.hasValidSuffixes("my.pm")).isTrue();
    }

    @Test
    public void testCustomSettings() {
        Settings settings = new Settings();
        settings.setProperty(PerlPlugin.FILE_SUFFIXES_KEY, "file,,other");
        PerlLanguage lang = new PerlLanguage(settings);
        assertThat(lang.getFileSuffixes()).isEqualTo(new String[] {"file","other"});
        assertThat(lang.hasValidSuffixes("my.file")).isTrue();
    }

    @Test
    public void testEquals() {
        assertThat(new PerlLanguage(new Settings())).isEqualTo(new PerlLanguage(new Settings()));
    }

}
