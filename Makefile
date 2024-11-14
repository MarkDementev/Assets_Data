setup:
	./gradlew wrapper --gradle-version 8.3

clean:
	./gradlew clean

build:
	./gradlew clean build

start:
	./gradlew bootRun --args='--spring.profiles.active=dev'

install:
	./gradlew installDist

test:
	./gradlew test

report:
	./gradlew jacocoTestReport


.PHONY: build