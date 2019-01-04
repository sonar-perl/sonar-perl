FROM mercuriete/sonar-scanner:latest

ADD . /project
WORKDIR /project

CMD sonar-scanner -Dsonar.host.url=$SONAR_HOST_URL

