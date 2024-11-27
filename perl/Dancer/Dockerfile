FROM sonarsource/sonar-scanner-cli

COPY . /project
WORKDIR /project

CMD ["sonar-scanner", "-Dsonar.host.url='$SONAR_HOST_URL'"]

