#!/bin/bash
javadoc -encoding UTF-8 -sourcepath ./src -cp ./src/jfreechart-1.5.3.jar -d doc/javadoc -version -author ./src/*.java
