FROM        perl:latest
MAINTAINER  Oliver Trosien otrosien@epages.com

# perl Tooling
RUN curl -L http://cpanmin.us | perl - App::cpanminus
RUN cpanm Devel::Cover::Report::Clover

# TODO sonar-scanner
# ADD https://sonarsource.bintray.com/Distribution/sonar-scanner-cli/sonar-scanner-2.5.1.zip
# RUN unzip sonar-scanner-2.5.1.zip

COPY . /project
WORKDIR /project

RUN perl Makefile.PL
RUN cpanm --installdeps .
RUN make test > test_report.tap
RUN cover -test -report clover