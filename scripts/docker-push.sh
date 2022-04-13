#!/bin/bash

set -e # Exit with nonzero exit code if anything fails

function docker_login {
	docker login -u $DOCKER_USER -p $DOCKER_PASS
}

export REPO=sonarperl/sonar-perl

RAW_REF=${GITHUB_REF##*/}

if [[ "$GITHUB_REF" =~ ^refs/tags/[0-9.]+$ ]] ; then
	docker_login
	docker tag $REPO:latest $REPO:$RAW_REF
	# push version tag and latest
	docker push $REPO:$RAW_REF
	docker push $REPO
fi

if [ "$GITHUB_REF" == "refs/heads/master" ]; then
	docker_login
	# push latest
	docker push $REPO
fi
