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
import org.sonarqube.ws.client.HttpConnector;
import org.sonarqube.ws.client.WsClientFactories;

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
                .setShowErrors(true)
                .setEnvironmentVariable("sonar.verbose", "true")
                .setProjectDir(new File("projects/metrics"))
                .setProjectKey(PROJECT_KEY)
                .setProjectName(PROJECT_KEY)
                .setProjectVersion("1.0-SNAPSHOT")
                .setSourceDirs("lib");
    }

    private final TestSonarClient wsClient;

    public ProjectMetricsIntegrationTest(Orchestrator orchestrator) {
        orchestrator.executeBuild(build);
        wsClient = new TestSonarClient(
                WsClientFactories.getDefault().newClient(HttpConnector.newBuilder()
                .url(orchestrator.getServer().getUrl())
                .build()), PROJECT_KEY);
    }

    @Test
    public void project_level() {
      // Size
      assertThat(wsClient.getProjectMeasure("ncloc")).isEqualTo(29);
      assertThat(wsClient.getProjectMeasure("lines")).isEqualTo(79);
      assertThat(wsClient.getProjectMeasure("files")).isEqualTo(2);
      assertThat(wsClient.getProjectMeasure("functions")).isEqualTo(4);
      assertThat(wsClient.getProjectMeasure("classes")).isEqualTo(1);
      // Documentation
      assertThat(wsClient.getProjectMeasure("comment_lines")).isEqualTo(27);
    }

    @Test
    public void directory_level() {
      // Size
      assertThat(wsClient.getDirectoryMeasure("lib/Sample", "ncloc")).isEqualTo(15);
      assertThat(wsClient.getDirectoryMeasure("lib/Sample", "lines")).isEqualTo(59);
      assertThat(wsClient.getDirectoryMeasure("lib/Sample", "files")).isEqualTo(1);
      assertThat(wsClient.getDirectoryMeasure("lib/Sample", "functions")).isEqualTo(3);
      assertThat(wsClient.getDirectoryMeasure("lib/Sample", "classes")).isEqualTo(1);
      // Documentation
      assertThat(wsClient.getDirectoryMeasure("lib/Sample", "comment_lines")).isEqualTo(26);
    }

    @Test
    public void file_level() {
      // Size
      assertThat(wsClient.getFileMeasure("lib/Sample/Project.pm", "ncloc")).isEqualTo(15);
      assertThat(wsClient.getFileMeasure("lib/Sample/Project.pm", "lines")).isEqualTo(59);
      assertThat(wsClient.getFileMeasure("lib/Sample/Project.pm", "files")).isEqualTo(1);
      assertThat(wsClient.getFileMeasure("lib/Sample/Project.pm", "functions")).isEqualTo(3);
      assertThat(wsClient.getFileMeasure("lib/Sample/Project.pm", "classes")).isEqualTo(1);
      // Documentation
      assertThat(wsClient.getFileMeasure("lib/Sample/Project.pm", "comment_lines")).isEqualTo(26);
      // heredoc
      assertThat(wsClient.getFileMeasure("lib/heredoc.pl", "lines")).isEqualTo(20);
    }

}