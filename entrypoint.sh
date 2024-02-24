#!/bin/sh

JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"

if [ ! -z "$JAVA_OPTS_ARGS" ]; then
	JAVA_OPTS="$JAVA_OPTS $JAVA_OPTS_ARGS"
fi

exec java $JAVA_OPTS -jar /app.jar
