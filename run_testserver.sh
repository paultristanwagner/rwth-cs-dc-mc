#!/usr/bin/env bash
LATEST_PAPER_VERSION="paper-1.18.1-175.jar"

# Create directory for testserver
if [ ! -d "testserver" ]; then
  mkdir testserver
fi
cd testserver

# Download the latest version of paper spigot
if [ ! -f "${LATEST_PAPER_VERSION}" ]; then
  wget https://papermc.io/api/v2/projects/paper/versions/1.18.1/builds/175/downloads/${LATEST_PAPER_VERSION}
fi

# Runs the paper server
java -jar ${LATEST_PAPER_VERSION}
