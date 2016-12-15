FROM sonarqube:6.2-alpine

ADD https://repo1.maven.org/maven2/org/sonarsource/clover/sonar-clover-plugin/3.1/sonar-clover-plugin-3.1.jar /opt/sonarqube/extensions/plugins/sonar-clover-plugin-3.1.jar
COPY build/libs/* /opt/sonarqube/extensions/plugins/
