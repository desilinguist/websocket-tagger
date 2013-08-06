REM make sure that CLASSPATH contains paths to:
REM - stanford-corenlp.jar
REM - stanford-corenlp-models.jar
REM - java_websocket.jar

java -Xmx200m -classpath %CLASSPATH%;%CD% StanfordTaggerServer
