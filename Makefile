setup:
	./gradlew wrapper --gradle-version 8.2.1

clean:
	./gradlew clean

build:
	./gradlew clean build

start:
	./gradlew bootRun --args='--spring.profiles.active=development'

start-prod:
	./gradlew bootRun --args='--spring.profiles.active=production'

install:
	./gradlew installDist

test:
	./gradlew test


.PHONY: build