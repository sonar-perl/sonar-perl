package com.github.sonarperl.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarScanner;
import com.sonar.orchestrator.locator.FileLocation;

public class TestMetricsIntegrationTest {

    private static final String PROJECT_KEY = "tap";

    @ClassRule
    public static Orchestrator orchestrator = Orchestrator.builderEnv()
      .setSonarVersion("6.4")
      .addPlugin(FileLocation.byWildcardMavenFilename(new File("../../sonar-perl-plugin/build/libs"), "sonar-perl-plugin-*.jar"))
      .build();

    private static Sonar wsClient;

    @BeforeClass
    public static void startServer() {
        SonarScanner build = SonarScanner.create()
                .setProjectDir(new File("projects/tap"))
                .setProjectKey(PROJECT_KEY)
                .setProjectName(PROJECT_KEY)
                .setProjectVersion("1.0-SNAPSHOT")
                .setProperty("sonar.perl.testHarness.archivePath", "testReport.tgz")
                .setSourceDirs("lib")
                .setTestDirs("t");
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