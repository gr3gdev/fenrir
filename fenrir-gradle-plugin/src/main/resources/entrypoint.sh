#!/bin/sh
# JVM Size
echo "Run with Fenrir"
echo "- JVM Size: $(du -sh /jre | cut -f1)"
echo "- Libraries Size : $(du -sh /libs | cut -f1)"
java -cp /libs/ [mainClass]