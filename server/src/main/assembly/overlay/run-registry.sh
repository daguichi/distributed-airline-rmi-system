#!/bin/bash

CLASSPATH="."
for dep in `ls lib/jars/*.jar`
do
	CLASSPATH="$CLASSPATH:$dep"
done

export CLASSPATH

rmiregistry -J-Djava.rmi.server.logCalls=true $*
