#!/bin/bash
rm -r releases
mkdir releases
for i in lin32 lin64 win32 win64 mac32 mac64; do 
	mvn -P $i clean
	mvn -P $i package
	mv target/*.zip releases
	mv target/*.tar.bz2 releases
done

mvn clean
