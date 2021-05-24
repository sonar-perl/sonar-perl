FROM sonarqube:7.9-community

ADD --chown=sonarqube:sonarqube https://s01.oss.sonatype.org/service/local/repo_groups/public/content/io/github/sfeir-open-source/sonar-clover-plugin/4.1-SNAPSHOT/sonar-clover-plugin-4.1-20210511.125508-2.jar /opt/sonarqube/extensions/plugins/sonar-clover-plugin-4.1.jar
COPY --chown=sonarqube:sonarqube build/libs/*all.jar /opt/sonarqube/extensions/plugins/
