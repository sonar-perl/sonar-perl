package com.github.sonarperl.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Collection;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarScanner;

@RunWith(Parameterized.class)
public class TestMetricsIntegrationJUnit1Test {

    private static final String PROJECT_KEY = "tap_junit1";

    @ClassRule
    public static TestRule RESOURCES = IntegrationTests.RESOURCES;

    @Parameters
    public static Collection<Orchestrator> orchestrators() {
        return IntegrationTests.orchestrators();
    }

    private static final SonarScanner build;

    static {
        build = TestSonarScanner.create()
                .setProjectDir(new File("projects/tap_junit1"))
                .setProjectKey(PROJECT_KEY)
                .setProjectName(PROJECT_KEY)
                .setProjectVersion("1.0-SNAPSHOT")
                .setProperty("sonar.perl.testHarness.junitPath", "junit_reports")
                .setSourceDirs("lib")
                .setTestDirs("t");
    }

    private final TestSonarClient wsClient;

    public TestMetricsIntegrationJUnit1Test(Orchestrator orchestrator) {
        orchestrator.executeBuild(build);
        wsClient = new TestSonarClient(orchestrator, PROJECT_KEY);
    }

    @Test
    public void file_level() {
      // test count
        assertThat(wsClient.getFileMeasure("t/Project.t", "tests")).isEqualTo(2);
        assertThat(wsClient.getFileMeasure("t/Project.t", "test_failures")).isEqualTo(1);
    }

}
