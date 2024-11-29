FROM sonarsource/sonar-scanner-cli:11.1

COPY . /project
WORKDIR /project

CMD ["sonar-scanner", "-Dsonar.host.url='$SONAR_HOST_URL'"]

