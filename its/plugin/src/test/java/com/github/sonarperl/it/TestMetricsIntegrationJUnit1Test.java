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
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

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
        build = SonarScanner.create()
                .setProjectDir(new File("projects/tap_junit1"))
                .setProjectKey(PROJECT_KEY)
                .setProjectName(PROJECT_KEY)
                .setProjectVersion("1.0-SNAPSHOT")
                .setProperty("sonar.perl.testHarness.junitPath", "junit_reports")
                .setSourceDirs("lib")
                .setTestDirs("t");
    }

    private final Sonar wsClient;

    public TestMetricsIntegrationJUnit1Test(Orchestrator orchestrator) {
        orchestrator.executeBuild(build);
        wsClient = orchestrator.getServer().getWsClient();
    }

    @Test
    public void file_level() {
      // test count
        assertThat(getFileMeasure("tests").getIntValue()).isEqualTo(2);
        assertThat(getFileMeasure("test_failures").getIntValue()).isEqualTo(1);
    }

    private Measure getFileMeasure(String metricKey) {
      Resource resource = wsClient.find(ResourceQuery.createForMetrics(keyFor("Project.t"), metricKey));
      return resource == null ? null : resource.getMeasure(metricKey);
    }

    private static String keyFor(String s) {
      return PROJECT_KEY + ":t/" + s;
    }

}
