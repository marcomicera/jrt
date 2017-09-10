@echo off
cls
javac jrt/implementation/JRTSlaveImpl.java jrt/implementation/JRTDispatcher.java -cp .;../../build/web/WEB-INF/classes/jrt/slave -d ../../build/web/WEB-INF/classes/
cd ../../build/web/WEB-INF/classes
java jrt.implementation.JRTDispatcher %*
cd ../../../../src/java
