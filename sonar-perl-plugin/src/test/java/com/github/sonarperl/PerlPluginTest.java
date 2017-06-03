package com.github.sonarperl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarRuntime;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.utils.Version;

public class PerlPluginTest {

    @SuppressWarnings("unchecked")
    @Test
    public void test() {
        SonarRuntime runtime = SonarRuntimeImpl.forSonarQube(Version.create(5, 6), SonarQubeSide.SERVER);
        Plugin.Context context = new Plugin.Context(runtime);
        new PerlPlugin().define(context);
        assertThat(context.getExtensions()).hasSize(7);
    }

}
