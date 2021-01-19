FROM sonarsource/sonar-scanner-cli:latest

ADD . /project
WORKDIR /project
CMD sonar-scanner -Dsonar.host.url=$SONAR_HOST_URL
