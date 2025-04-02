package com.github.sonarperl.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Collection;

import com.sonar.orchestrator.junit4.OrchestratorRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarScanner;

@RunWith(Parameterized.class)
public class TestMetricsIntegrationTest {

    private static final String PROJECT_KEY = "tap";

    @ClassRule
    public static TestRule RESOURCES = IntegrationTests.RESOURCES;

    @Parameters
    public static Collection<OrchestratorRule> orchestrators() {
        return IntegrationTests.orchestrators();
    }

    private SonarScanner scanner(OrchestratorRule orchestrator) {
        return TestSonarScanner.create(orchestrator)
                .setProjectDir(new File("projects/tap"))
                .setProjectKey(PROJECT_KEY)
                .setProjectName(PROJECT_KEY)
                .setProjectVersion("1.0-SNAPSHOT")
                .setProperty("sonar.perl.testHarness.archivePath", "testReport.tgz")
                .setSourceDirs("lib")
                .setTestDirs("t");
    }

    private static TestSonarClient wsClient;

    public TestMetricsIntegrationTest(OrchestratorRule orchestrator) {
        orchestrator.executeBuild(scanner(orchestrator));
		wsClient = new TestSonarClient(orchestrator, PROJECT_KEY);
    }

    @Test
    public void file_level() {
      // test count
        assertThat(wsClient.getFileMeasure("t/Project.t","tests")).isEqualTo(2);
        assertThat(wsClient.getFileMeasure("t/Project.t","test_failures")).isEqualTo(1);
    }

}