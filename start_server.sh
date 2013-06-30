#!/bin/bash

# make sure that CLASSPATH contains paths to:
# - stanford-corenlp.jar
# - stanford-corenlp-models.jar
# - java_websocket.jar

java -Xmx200m -cp $CLASSPATH:. StanfordTaggerServer
