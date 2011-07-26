#!/bin/bash

# turn off wildcards
set -f

# make sure GSHOME is defined
if [ ! -n "$GSHOME" ] ; then
    echo Environment variable GSHOME not defined.
    echo Please set GSHOME to the home directory of your GigaSpaces installation.
    exit 1
fi

# establish where the script is running from, so we can use relative paths
SCRIPTDIR=`dirname $0`

# detect Cygwin
cygwin=false;
case "`uname`" in
  CYGWIN*) cygwin=true;
esac
  

export BASECLASSPATH=$SCRIPTDIR/../ece-client/target/ece-client-1.0.jar
export GSCLASSPATH="$GSHOME/lib/required/"
export SEPARATOR=":"
if $cygwin; then
   export SEPARATOR=";"
   export GSCLASSPATH=`cygpath --windows $GSCLASSPATH`
   export BASECLASSPATH=`cygpath --windows $BASECLASSPATH`
fi

java -cp "$GSCLASSPATH*$SEPARATOR$BASECLASSPATH" org.openspaces.ece.client.ConsoleClient
