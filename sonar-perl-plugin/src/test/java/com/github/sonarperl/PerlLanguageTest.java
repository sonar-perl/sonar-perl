package com.github.sonarperl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.config.Settings;

public class PerlLanguageTest {

    @Test
    public void testDefaults() {
        PerlLanguage lang = new PerlLanguage(new MapSettings());
        assertThat(lang.getFileSuffixes()).isEqualTo(new String[] {"pm","pl","t"});
        assertThat(lang.hasValidSuffixes("my.file")).isFalse();
        assertThat(lang.hasValidSuffixes("my.pm")).isTrue();
    }

    @Test
    public void testCustomSettings() {
        Settings settings = new MapSettings();
        settings.setProperty(PerlPlugin.FILE_SUFFIXES_KEY, "file,,other");
        PerlLanguage lang = new PerlLanguage(settings);
        assertThat(lang.getFileSuffixes()).isEqualTo(new String[] {"file","other"});
        assertThat(lang.hasValidSuffixes("my.file")).isTrue();
    }

    @Test
    public void testEquals() {
        assertThat(new PerlLanguage(new MapSettings())).isEqualTo(new PerlLanguage(new MapSettings()));
    }

}
