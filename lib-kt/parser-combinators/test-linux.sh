#!/usr/bin/env bash

docker run -u gradle \
	-v "$HOME/.konan":/home/gradle/.konan \
	-v "$PWD":/home/gradle/project \
	-v "$PWD/../util/":/home/gradle/util \
	-w /home/gradle/project gradle \
	gradle linuxX64Test
