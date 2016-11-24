#!/bin/bash

set -e # Exit with nonzero exit code if anything fails

if [ "$TRAVIS_BRANCH" == "master" ]; then
	docker login -e $DOCKER_EMAIL -u $DOCKER_USER -p $DOCKER_PASS
	export REPO=otrosien/sonar-perl
	docker push $REPO
fi
