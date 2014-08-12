#!/bin/bash
gzippedDoc="clean.nt.gz"
if [ -z "$1" ]; then
	echo "Parsing the clean.nt.gz document from the current directory. If you'd like to use another one, pass a reference to that document (either online or local) to this script"
else
	echo "Parsing $1"
	gzippedDoc=$1
fi
javaInstalled=0
if type java >/dev/null 2>&1 ; then
	javaInstalled=1
fi
npmInstalled=0
if type npm >/dev/null 2>&1 ; then
	npmInstalled=1
fi
python3Installed=0
if type python3 >/dev/null 2>&1 ; then
	python3Installed=1
fi
echo "Which language do you want to parse the LOD Laundromat output with?"
if type java >/dev/null 2>&1 && type javac >/dev/null 2>&1; then
	echo "1: Java"
	numToLang="java"
else
	echo "1: Java (Unavailable. Either java or javac not installed)"
fi
if type npm >/dev/null 2>&1 ; then
	echo "2: NodeJs"
else
	echo "2: NodeJs (npm is unavailable. Not installed)"
fi
if type python3 >/dev/null 2>&1 ; then
	echo "3: Python3"
else
	echo "3: Python3 (Unavailable. Not installed)"
fi

read lang

if [ "$lang" == "1" ]; then
  (javac ./java/Parse.java && java -classpath ./java Parse $gzippedDoc);
else if [ "$lang" == "2" ]; then
  (cd js && npm install)
  ./js/parse.js $gzippedDoc;
else if [ "$lang" == "3" ]; then
  ./python/parse.py $gzippedDoc;
else
	echo "invalid user input"
fi
fi
fi
