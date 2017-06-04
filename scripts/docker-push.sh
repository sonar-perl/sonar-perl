#!/bin/bash

set -e # Exit with nonzero exit code if anything fails


function docker_login {
	docker login -u $DOCKER_USER -p $DOCKER_PASS
}

export REPO=sonarperl/sonar-perl

if [[ "$TRAVIS_TAG" =~ ^[0-9.]+$ ]] ; then
	docker_login
	docker tag $REPO:latest $REPO:$TRAVIS_TAG
	# push version tag and latest
	docker push $REPO:$TRAVIS_TAG
	docker push $REPO
fi

if [ "$TRAVIS_BRANCH" == "master" ]; then
	docker_login
	# push latest
	docker push $REPO
fi
