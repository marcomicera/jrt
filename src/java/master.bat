@echo off
cls
javac jrt/master/JRTMaster.java -cp .;../../build/web/WEB-INF/classes/jrt/slave -d ../../build/web/WEB-INF/classes/
cd ../../build/web/WEB-INF/classes
java jrt.master.JRTMaster %*
cd ../../../../src/java
