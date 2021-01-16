package com.github.sonarperl.it;

import java.util.Arrays;
import java.util.List;

import org.sonarqube.ws.Measures;
import org.sonarqube.ws.Measures.Measure;
import org.sonarqube.ws.client.HttpConnector;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.WsClientFactories;
import org.sonarqube.ws.client.issues.SearchRequest;
import org.sonarqube.ws.client.measures.ComponentRequest;

import com.sonar.orchestrator.Orchestrator;

public class TestSonarClient {

    private final WsClient wsClient;
    private final String project;

    public TestSonarClient(Orchestrator orchestrator, String project) {
        this.wsClient = WsClientFactories.getDefault().newClient(HttpConnector.newBuilder()
                .url(orchestrator.getServer().getUrl())
                .credentials("admin", "admin")
                .build());
        this.project = project;
    }

    private Measure getMeasure(String componentKey, String metricKey) {
      Measures.ComponentWsResponse response = wsClient.measures().component(new ComponentRequest()
        .setComponent(componentKey)
        .setMetricKeys(Arrays.asList(metricKey)));
      List<Measure> measures = response.getComponent().getMeasuresList();
      return measures.size() == 1 ? measures.get(0) : null;
    }

    private Integer getMeasureAsInt(String componentKey, String metricKey) {
        Measure measure = getMeasure(componentKey, metricKey);
      return (measure == null) ? null : Integer.parseInt(measure.getValue());
    }

    public Integer getProjectMeasure(String metricKey) {
        return getMeasureAsInt(project, metricKey);
      }

    public Integer getDirectoryMeasure(String dirName, String metricKey) {
      return getMeasureAsInt(keyFor(dirName), metricKey);
    }

    public Integer getFileMeasure(String file, String metricKey) {
      return getMeasureAsInt(keyFor(file), metricKey);
    }

    private String keyFor(String s) {
        return project + ":" + s;
    }

    public Integer issueCount(String severity, String rule) {
        return wsClient.issues().search(new SearchRequest()
                .setSeverities(Arrays.asList(severity))
                .setRules(Arrays.asList(rule)))
                .getIssuesCount();
    }
 
}
