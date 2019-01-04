package com.github.sonarperl;

import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarRuntime;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.utils.Version;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PerlPluginTest {

    @SuppressWarnings("unchecked")
    @Test
    public void test() {
        Version v60 = Version.create(6, 0);
        assertThat(extensions(SonarRuntimeImpl.forSonarQube(v60, SonarQubeSide.SERVER))).hasSize(7);
    }

    private List extensions(SonarRuntime runtime) {
        Plugin.Context context = new Plugin.Context(runtime);
        new PerlPlugin().define(context);
        return context.getExtensions();
    }
}
