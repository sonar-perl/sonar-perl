package com.github.sonarperl.it;

import java.util.Arrays;
import java.util.List;

import org.sonarqube.ws.WsMeasures;
import org.sonarqube.ws.WsMeasures.Measure;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.issue.SearchWsRequest;
import org.sonarqube.ws.client.measure.ComponentWsRequest;

public class TestSonarClient {

    private final WsClient wsClient;
    private final String project;

    public TestSonarClient(WsClient wsClient, String project) {
        this.wsClient = wsClient;
        this.project = project;
    }

    private Measure getMeasure(String componentKey, String metricKey) {
      WsMeasures.ComponentWsResponse response = wsClient.measures().component(new ComponentWsRequest()
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
        return wsClient.issues().search(new SearchWsRequest()
                .setSeverities(Arrays.asList(severity))
                .setRules(Arrays.asList(rule)))
                .getIssuesCount();
    }
 
}
