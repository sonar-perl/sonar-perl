#!/bin/bash -x

set -e # Exit with nonzero exit code if anything fails

if [[ "$TRAVIS_TAG" =~ ^[0-9.]+$ ]] ; then
	tools/github-release upload --security-token "$GITHUB_TOKEN" --user otrosien --repo sonar-perl --tag "$TRAVIS_TAG" --name sonar-perl-plugin-${TRAVIS_TAG}.jar --file sonar-perl-plugin/build/libs/sonar-perl-plugin-${TRAVIS_TAG}.jar
	tools/github-release release --security-token "$GITHUB_TOKEN" --user otrosien --repo sonar-perl --tag "$TRAVIS_TAG" --description "Release $TRAVIS_TAG"
fi
