package com.github.sonarperl;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.sonar.api.config.internal.MapSettings;

public class PerlLanguageTest {

    @Test
    public void testDefaults() {
        PerlLanguage lang = new PerlLanguage(new MapSettings().asConfig());
        assertThat(lang.getFileSuffixes()).isEqualTo(new String[]{".pl", ".pm", ".t"});
        assertThat(lang.hasValidSuffixes("my.file")).isFalse();
        assertThat(lang.hasValidSuffixes("my.pm")).isTrue();
    }

    @Test
    public void testCustomSettings() {
        MapSettings settings = new MapSettings();
        settings.setProperty(PerlLanguage.FILE_SUFFIXES_KEY, "file,,other");
        PerlLanguage lang = new PerlLanguage(settings.asConfig());
        assertThat(lang.getFileSuffixes()).isEqualTo(new String[]{"file","other"});
        assertThat(lang.hasValidSuffixes("my.file")).isTrue();
    }

    @Test
    public void testEquals() {
        assertThat(new PerlLanguage(new MapSettings().asConfig())).isEqualTo(new PerlLanguage(new MapSettings().asConfig()));
    }

}
