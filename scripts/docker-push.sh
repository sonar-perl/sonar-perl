#!/bin/bash

set -e # Exit with nonzero exit code if anything fails

export REPO=ghcr.io/sonar-perl/sonar-perl

RAW_REF=${GITHUB_REF##*/}

cd sonar-perl-plugin
# check if the plugin is built
ls -l build/libs

TAGS=""

if [[ "$GITHUB_REF" =~ ^refs/tags/[0-9.]+$ ]] ; then
	TAGS="--tag ${REPO}:latest --tag ${REPO}:${RAW_REF} --push"
fi

if [ "$GITHUB_REF" == "refs/heads/master" ]; then
	TAGS="--tag ${REPO}:latest --push"
fi

# build multi-arch docker image
docker buildx build --platform linux/amd64,linux/arm64 $TAGS .
