FROM sonarqube:7.9-community

ADD --chown=sonarqube:sonarqube https://repo1.maven.org/maven2/org/sonarsource/clover/sonar-clover-plugin/3.1/sonar-clover-plugin-3.1.jar /opt/sonarqube/extensions/plugins/sonar-clover-plugin-3.1.jar
COPY --chown=sonarqube:sonarqube build/libs/*all.jar /opt/sonarqube/extensions/plugins/
