package com.github.sonarperl;

import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarEdition;
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
        Version LTS = Version.create(7, 9);
        assertThat(extensions(SonarRuntimeImpl.forSonarQube(LTS, SonarQubeSide.SERVER, SonarEdition.COMMUNITY))).hasSize(7);
    }

    @SuppressWarnings("rawtypes")
	private List extensions(SonarRuntime runtime) {
        Plugin.Context context = new Plugin.Context(runtime);
        new PerlPlugin().define(context);
        return context.getExtensions();
    }
}
