sonar-scanner-2.5.1/bin/sonar-runner \
    -Dsonar.host.url=http://${DOCKER_IP}:9000 \
    -Dsonar.jdbc.url=jdbc:h2:tcp://sonarqube/sonar \
    -Dsonar.jdbc.username=sonar \
    -Dsonar.jdbc.password=sonar \
    -Dsonar.jdbc.driverClassName=org.h2.Driver \
    -Dsonar.embeddedDatabase.port=9092 \
    -X
