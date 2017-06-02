package com.github.sonarperl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.utils.Version;

import com.github.sonarperl.PerlPlugin;

public class PerlPluginTest {

    @SuppressWarnings("unchecked")
    @Test
    public void test() {
        Plugin.Context context = new Plugin.Context(Version.create(5, 6));
        new PerlPlugin().define(context);
        assertThat(context.getExtensions()).hasSize(7);
    }

}
