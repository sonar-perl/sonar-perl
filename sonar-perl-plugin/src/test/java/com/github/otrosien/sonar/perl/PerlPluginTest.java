package com.github.otrosien.sonar.perl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.utils.Version;

public class PerlPluginTest {

    @SuppressWarnings("unchecked")
    @Test
    public void test() {
        Plugin.Context context = new Plugin.Context(Version.create(5, 6));
        new PerlPlugin().define(context);
        assertThat(context.getExtensions()).hasSize(7);
    }

}
