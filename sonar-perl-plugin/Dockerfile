FROM sonarqube:7.9-community

ADD --chown=sonarqube:sonarqube https://bintray.com/sfeir-open-source/maven/download_file?file_path=org%2Fsfeir%2Fsonar-clover-plugin%2F4.1%2Fsonar-clover-plugin-4.1.jar /opt/sonarqube/extensions/plugins/sonar-clover-plugin-4.1.jar
COPY --chown=sonarqube:sonarqube build/libs/*all.jar /opt/sonarqube/extensions/plugins/
