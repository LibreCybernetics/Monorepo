#!/usr/bin/env bash

docker run -u gradle \
	-v "$HOME/.konan":/home/gradle/.konan \
	-v "$PWD":/home/gradle/project \
	-v "$PWD/../util/":/home/gradle/util \
	-v "$PWD/../parser-combinators/":/home/gradle/parser-combinators \
	-w /home/gradle/project gradle \
	gradle linuxX64Test
