FROM openjdk:8u102-jre
MAINTAINER  Oliver Trosien otrosien@epages.com

RUN echo 'Acquire::ForceIPv4 "true";' | tee /etc/apt/apt.conf.d/99force-ipv4

WORKDIR /root

# sonar-scanner
RUN curl --insecure -OL https://sonarsource.bintray.com/Distribution/sonar-scanner-cli/sonar-scanner-2.8.zip
RUN unzip sonar-scanner-2.8.zip
RUN rm sonar-scanner-2.8.zip

# env
ENV SONAR_RUNNER_HOME=/root/sonar-scanner-2.8
ENV PATH $PATH:/root/sonar-scanner-2.8/bin

COPY . /project
WORKDIR /project

ENTRYPOINT ['/root/sonar-scanner-2.8/bin/sonar-scanner','-Dsonar.host.url=http://sonarperl:9000']
