#!/bin/bash

# This script is designed to start the GS-Agent, in one of two modes:
# 1. Static GSC mode (which starts four GSCs and doesn't include an ESM)
# 2. ESM mode (which starts no GSCs and includes an ESM)
#
# The default is to use static GSC mode.
#
# To use the ESM mode, add the -esm parameter.

# echo $GSHOME

if [ ! -n "$GSHOME" ] ; then
    echo Environment variable GSHOME not defined.
    echo Please set GSHOME to the home directory of your GigaSpaces installation.
    exit 1
fi

if [ "$1" = "-help" ] ; then
    echo This script starts the GS-Agent for the ElasticCalculationEngine demonstrations.
    echo 
    echo It is able to start two types of GS-Agents:
    echo     1. A normal, statically-allocated Agent with four GSCs
    echo     2. An elastic Agent with no GSCs but an Elastic Service Manager
    echo
    echo The static allocation mode is the default.
    echo 
    echo To invoke the Elastic mode, add -esm as an argument to the script.
    echo
    exit 0
fi

# invoke GS environment
pushd $GSHOME/bin > /dev/null
. ./setenv.sh

ARGS="gsa.gsc 4"
if [ "$1" = "-esm" ] ; then
   echo ESM MODE
   ARGS="gsa.global.esm 1 gsa.gsc 0 gsa.global.gsm 2 gsa.global.lus 2"
fi

echo Starting GS-Agent with arguments $ARGS
sleep 4
. ./gs-agent.sh $ARGS
popd > /dev/null

exit 0