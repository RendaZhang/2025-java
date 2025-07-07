#!/bin/sh
set -e
# Run spotless apply for each Maven module
for pom in $(git ls-files '*pom.xml'); do
  dir=$(dirname "$pom")
  (cd "$dir" && mvn -q com.diffplug.spotless:spotless-maven-plugin:2.44.5:apply)
done
