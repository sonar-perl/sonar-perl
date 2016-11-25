[![Build Status](https://travis-ci.org/otrosien/sonar-perl.svg)](https://travis-ci.org/otrosien/sonar-perl)

[![Quality Gate](https://sonarqube.com/api/badges/gate?key=com.github.otrosien:sonar-perl)](https://sonarqube.com/dashboard/index/com.github.otrosien:sonar-perl)


# SonarQube Perl Plugin (alpha)

This is a spike to figure out how easy features like code coverage
and Perl::Critic and test reporting can be integrated into sonarqube. 
Given enough interest this could be upgraded into a proper open source project.

## Current State

Current plugin analyzes perl projects without having to parse the perl 
code itself, attaching the raw source as line-based text. In a 
later step there could be some proper parsing and analyzing, e.g. based 
on the ANTLR grammar from the Perl Eclipse integration (EPIC) or
the IntelliJ-Perl grammar.

What we have so far...

* LOC reporting
* Syntax highlighting of imported code
* Perl::Critic issues can be attached

## Teaser Image

![PerlCritic violation in SonarQube](img/sonar_perlcritic.png)

## Running it

The easiest way to see SonarQube with the Perl Plugin in action is to 
execute the sample perl project with sonar-scanner in docker-compose.

First, start up the latest sonar-perl

```sh
docker-compose up -d sonarperl
```

Wait for SonarQube to start-up and then open the web interface in your browser (http://localhost:9000/ - or the IP you bound your docker to).
Then, run the sonar-scanner on the sample project.

```sh
docker-compose up sampleproject
```

You should see the perl-sample-project on your SonarQube dashboard.


## Install into your SonarQube

At the moment the plugin has not been published yet. In order to test it
in your SoarQube installation, you need to build the project with a `./gradlew sonar-perl-plugin:build` and
copy the resulting jar file `sonar-perl-plugin/build/sonar-perl-plugin-0.1.3.jar` into the plugins
dir of your sonar installation (should be sth. like `/opt/sonarqube/extensions/plugins/`)

## Next steps

* The clover report does not contain line-based coverage information. This is crucial for sonarqube to calculate coverage details. Thus, either the clover report needs to be enhanced or we need to find a different solution altogether.

* Test reporting

## Links

* [Devel::Cover::Report::Clover](http://search.cpan.org/dist/Devel-Cover-Report-Clover/lib/Devel/Cover/Report/Clover.pm) 
  and [Sonar Clover Plugin](http://docs.sonarqube.org/display/SONARQUBE45/Clover+Plugin) for coverage reporting
* Perl [TAP](https://testanything.org/) and [SonarQube TAP parser](https://github.com/dbac2002/sonar-tap-parser) for test reporting
* [Perl::Critic](http://perlcritic.org/) for issue reporting
