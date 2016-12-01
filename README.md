[![Build Status](https://travis-ci.org/otrosien/sonar-perl.svg?branch=master)](https://travis-ci.org/otrosien/sonar-perl)
[![Quality Gate](https://sonarqube.com/api/badges/gate?key=com.github.otrosien:sonar-perl)](https://sonarqube.com/dashboard/index/com.github.otrosien:sonar-perl)


# SonarQube Perl Plugin (alpha)

This is a SonarQube plugin to integrate popular features like code coverage,
Perl::Critic and test reporting for perl projects.

## Current State

Current plugin is still at a very early stage. It analyzes perl projects without having to parse the perl code itself, attaching the raw source as line-based text. In a later step there could be some proper grammar-based parsing and analyzing.

What we have so far...

* LOC reporting (incl. comment and POD lines)
* Structural metrics (classes and functions)
* Syntax highlighting of imported code
* Perl::Critic issues can be attached

## Teaser Image

![PerlCritic violation in SonarQube](img/sonar_perlcritic.png)

## Running it

The easiest way to see SonarQube with the Perl Plugin in action is to 
execute our sample perl project with `sonar-scanner` against
a local SonarQube installation using `docker-compose`.

First, start up the SonarQube docker container, that has the
latest sonar-perl installed.

```sh
docker-compose up -d sonarperl
```

Wait for SonarQube to start-up and then open the web interface in your browser
(http://localhost:9000/ - or the IP you bound your docker to).
Then, run the `sonar-scanner` on the sample project.

```sh
docker-compose run sample
```

You should see a "perl sample project" on your SonarQube dashboard.


## Installing into your SonarQube

Plugin releases can be downloaded from our [github releases page](https://github.com/otrosien/sonar-perl/releases). 
If you want to build a snapshot version from source, you can create it via `./gradlew sonar-perl-plugin:jar` and
copy the resulting file (usually suffixed -SNAPSHOT, unless you checked out a release tag) 
e.g. `sonar-perl-plugin/build/sonar-perl-plugin-0.2.2-SNAPSHOT.jar` into the plugins
directory of your SonarQube installation (should be something like `/opt/sonarqube/extensions/plugins/`)


### Compatibility

We build and test against the current LTS release (5.6), SonarQube 6.x should work as well. Please report bugs
or incompatibilities in our [bugtracker](https://github.com/otrosien/sonar-perl/issues).

## Building all from source

Running a full build includes creation of the plugin-jar and a docker image.
This is achieved by running `./gradlew build`. Note that you 
need to have a local docker daemon running for this.

## Next steps

Please look at the issues in our [bugtracker](https://github.com/otrosien/sonar-perl/issues) for the planning and status of the next steps.

## Links

* [Devel::Cover::Report::Clover](http://search.cpan.org/dist/Devel-Cover-Report-Clover/lib/Devel/Cover/Report/Clover.pm) 
  and [Sonar Clover Plugin](http://docs.sonarqube.org/display/SONARQUBE45/Clover+Plugin) for coverage reporting
* Perl [TAP](https://testanything.org/) and [SonarQube TAP parser](https://github.com/dbac2002/sonar-tap-parser) for test reporting
* [Perl::Critic](http://perlcritic.org/) for issue reporting
