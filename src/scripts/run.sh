#!/bin/bash

QUERY=$1
DATE=$2
LIMIT=$3
OUTPUT=$4

java -cp twitterbot-0.0.1-SNAPSHOT.jar:twitter4j-core-4.0.4.jar wpy.twitterbot.sentiment.Runner \
    $QUERY \
    $DATE \
    $LIMIT \
    $OUTPUT

