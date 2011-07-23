#!/bin/bash

# This script is designed to start the GS-Agent, in one of two modes:
# 1. Static GSC mode (which starts four GSCs and doesn't include an ESM)
# 2. ESM mode (which starts no GSCs and includes an ESM)
#
# The default is to use static GSC mode.
#
# To use the ESM mode, add the -esm parameter.

# Check to see if path conversion is needed
toNative() {
    # Check for Cygwin
    case $OS in
        Windows*)
           toWindows "$@";;
           *) echo $* ;;
    esac
}
echo $GSHOME

if [ ! -n "$GSHOME" ] ; then
    echo Environment variable GSHOME not defined.
    echo Please set GSHOME to the home directory of your GigaSpaces installation.
    exit 1
fi

# invoke GS environment
pushd $GSHOME/bin
. ./setenv.sh

ARGS="gsa.gsc 4"
if [ "$1" = "-esm" ] ; then
   echo ESM MODE
   ARGS=gsa.global.esm 1 gsa.gsc 0 gsa.global.gsm 2 gsa.global.lus 2
fi

. ./gs-agent.sh $ARGS
popd

