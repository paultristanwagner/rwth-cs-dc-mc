#!/usr/bin/env bash
MC_VERSION="1.18.2"
BUILD_ID="277"
LATEST_PAPER_VERSION="paper-${MC_VERSION}-${BUILD_ID}.jar"

# Create directory for testserver
if [ ! -d "testserver" ]; then
  mkdir testserver
fi
cd testserver

# Download the latest version of paper spigot
if [ ! -f "${LATEST_PAPER_VERSION}" ]; then
  wget https://papermc.io/api/v2/projects/paper/versions/${MC_VERSION}/builds/${BUILD_ID}/downloads/${LATEST_PAPER_VERSION}
fi

# Runs the paper server
java -jar ${LATEST_PAPER_VERSION}
