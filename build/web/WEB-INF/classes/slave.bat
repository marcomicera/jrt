@echo off
cls
javac jrt/implementation/JRTSlaveImpl.java -cp .;../build/classes/jrt/slave -d ../build/classes/
cd ../build/classes
java jrt.implementation.JRTSlaveImpl %*
cd ../../src
