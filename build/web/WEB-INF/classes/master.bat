@echo off
cls
javac jrt/master/JRTMaster.java -cp .;../build/classes/jrt/slave -d ../build/classes/
cd ../build/classes
java jrt.master.JRTMaster %*
cd ../../src
