#!/bin/bash

if [ ! -n "$GSHOME" ] ; then
    echo Environment variable GSHOME not defined.
    echo Please set GSHOME to the home directory of your GigaSpaces installation.
    exit 1
fi

#export CLASSPATH=
#export SEPARATOR=
#for i in $GSHOME/lib/required/*; do
#   export CLASSPATH=${CLASSPATH}${SEPARATOR}$i
#   export SEPARATOR=:
#done

java -cp "$GSHOME/lib/required/*":../ece-client/target/classes org.openspaces.ece.client.ConsoleClient $@
