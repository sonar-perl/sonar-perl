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
public class ProjectMetricsIntegrationTest {

    private static final String PROJECT_KEY = "metrics";

    @Parameters
    public static Collection<Orchestrator> orchestrators() {
        return IntegrationTests.orchestrators();
    }

    @ClassRule
    public static TestRule RESOURCES = IntegrationTests.RESOURCES;

    private static final SonarScanner build;

    static {
        build = SonarScanner.create()
                .setProjectDir(new File("projects/metrics"))
                .setProjectKey(PROJECT_KEY)
                .setProjectName(PROJECT_KEY)
                .setProjectVersion("1.0-SNAPSHOT")
                .setSourceDirs("lib");
    }

    private final Sonar wsClient;

    public ProjectMetricsIntegrationTest(Orchestrator orchestrator) {
        orchestrator.executeBuild(build);
        wsClient = orchestrator.getServer().getWsClient();
    }

    @Test
    public void project_level() {
      // Size
      assertThat(getProjectMeasure("ncloc").getIntValue()).isEqualTo(15);
      assertThat(getProjectMeasure("lines").getIntValue()).isEqualTo(59);
      assertThat(getProjectMeasure("files").getIntValue()).isEqualTo(1);
      assertThat(getProjectMeasure("functions").getIntValue()).isEqualTo(3);
      assertThat(getProjectMeasure("classes").getIntValue()).isEqualTo(1);
      // Documentation
      assertThat(getProjectMeasure("comment_lines").getIntValue()).isEqualTo(26);
    }

    @Test
    public void directory_level() {
      // Size
      assertThat(getDirectoryMeasure("ncloc").getIntValue()).isEqualTo(15);
      assertThat(getDirectoryMeasure("lines").getIntValue()).isEqualTo(59);
      assertThat(getDirectoryMeasure("files").getIntValue()).isEqualTo(1);
      assertThat(getDirectoryMeasure("functions").getIntValue()).isEqualTo(3);
      assertThat(getDirectoryMeasure("classes").getIntValue()).isEqualTo(1);
      // Documentation
      assertThat(getDirectoryMeasure("comment_lines").getIntValue()).isEqualTo(26);
    }

    @Test
    public void file_level() {
      // Size
      assertThat(getFileMeasure("ncloc").getIntValue()).isEqualTo(15);
      assertThat(getFileMeasure("lines").getIntValue()).isEqualTo(59);
      assertThat(getFileMeasure("files").getIntValue()).isEqualTo(1);
      assertThat(getFileMeasure("functions").getIntValue()).isEqualTo(3);
      assertThat(getFileMeasure("classes").getIntValue()).isEqualTo(1);
      // Documentation
      assertThat(getFileMeasure("comment_lines").getIntValue()).isEqualTo(26);
    }

    private Measure getProjectMeasure(String metricKey) {
        Resource resource = wsClient.find(ResourceQuery.createForMetrics(PROJECT_KEY, metricKey));
        return resource == null ? null : resource.getMeasure(metricKey);
      }

      private Measure getDirectoryMeasure(String metricKey) {
        Resource resource = wsClient.find(ResourceQuery.createForMetrics(keyFor("Sample"), metricKey));
        return resource == null ? null : resource.getMeasure(metricKey);
      }

      private Measure getFileMeasure(String metricKey) {
        Resource resource = wsClient.find(ResourceQuery.createForMetrics(keyFor("Sample/Project.pm"), metricKey));
        return resource == null ? null : resource.getMeasure(metricKey);
      }

      private static String keyFor(String s) {
        return PROJECT_KEY + ":lib/" + s;
      }

}