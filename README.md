# SonarQube Perl Plugin

This is a spike to figure out how easy features like code coverage
and Perl::Critic and test reporting can be integrated into sonarqube.

First of all without having to parse the perl code itself, and just
attaching the raw source as line-based text. In a later step there
could be some parsing and analyzing e.g. based on the ANTLR grammar
from the Perl Eclipse integration (EPIC)

## Current State

Nothing to see yet :)

## Links

* [Devel::Cover::Report::Clover](http://search.cpan.org/dist/Devel-Cover-Report-Clover/lib/Devel/Cover/Report/Clover.pm) 
  and [Sonar Clover Plugin](http://docs.sonarqube.org/display/SONARQUBE45/Clover+Plugin) for coverage reporting
* Perl [TAP](https://testanything.org/) and [SonarQube TAP parser](https://github.com/dbac2002/sonar-tap-parser) for test reporting
* [Perl::Critic](http://perlcritic.org/) for issue reporting
