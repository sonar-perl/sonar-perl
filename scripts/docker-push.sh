#!/bin/bash

set -e # Exit with nonzero exit code if anything fails

export REPO=ghcr.io/sonar-perl/sonar-perl

RAW_REF=${GITHUB_REF##*/}

if [[ "$GITHUB_REF" =~ ^refs/tags/[0-9.]+$ ]] ; then
	docker tag $REPO:latest $REPO:$RAW_REF
	# push version tag and latest
	docker push $REPO:$RAW_REF
	docker push $REPO
fi

if [ "$GITHUB_REF" == "refs/heads/master" ]; then
	# push latest
	docker push $REPO
fi
