#!/bin/bash

set -e # Exit with nonzero exit code if anything fails

export REPO=ghcr.io/sonar-perl/sonar-perl

RAW_REF=${GITHUB_REF##*/}

cd sonar-perl-plugin
# check if the plugin is built
ls -l build/libs
# build multi-arch docker image
docker buildx build --platform linux/amd64,linux/arm64 -t $REPO:latest .

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
