#!/usr/bin/env bash

mvn clean compile assembly:single
java -jar ./target/redis-playground-1.0-SNAPSHOT-jar-with-dependencies.jar