FROM sonarqube:5.4

ADD https://github.com/SonarQubeCommunity/sonar-clover/releases/download/3.1-RC1/sonar-clover-plugin.jar /opt/sonarqube/extensions/plugins/sonar-clover-plugin.jar
COPY build/libs/* /opt/sonarqube/extensions/plugins/
