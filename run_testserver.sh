#!/usr/bin/env bash
VERSION_ID="187"
LATEST_PAPER_VERSION="paper-1.18.1-${VERSION_ID}.jar"

# Create directory for testserver
if [ ! -d "testserver" ]; then
  mkdir testserver
fi
cd testserver

# Download the latest version of paper spigot
if [ ! -f "${LATEST_PAPER_VERSION}" ]; then
  wget https://papermc.io/api/v2/projects/paper/versions/1.18.1/builds/${VERSION_ID}/downloads/${LATEST_PAPER_VERSION}
fi

# Runs the paper server
java -jar ${LATEST_PAPER_VERSION}
