#!/bin/bash -x

set -e # Exit with nonzero exit code if anything fails

docker login -e $DOCKER_EMAIL -u $DOCKER_USER -p $DOCKER_PASS
export REPO=otrosien/sonar-perl

if [[ "$TRAVIS_TAG" =~ ^[0-9.]+$ ]] ; then
	docker tag sonar-perl $REPO:$TRAVIS_TAG
	# push tag and latest
	docker push $REPO:$TRAVIS_TAG
	docker push $REPO
fi

if [ "$TRAVIS_BRANCH" == "master" ]; then
	# push latest
	docker push $REPO
fi
