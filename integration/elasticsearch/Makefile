SMT_MODULE_NAME := kafka-connect-elasticsearch-transforms

.PHONY: help
help:
	@echo "Usage: <up,down>"
	@echo " "
	@echo "Commands:"
	@echo "  up      Build, create and start containers"
	@echo "  down    Stop and remove containers, networks, images, and volumes"
	@echo " "

.PHONY: up
up:
	mvn clean package -f $(SMT_MODULE_NAME)/pom.xml
	cp $(SMT_MODULE_NAME)/target/$(SMT_MODULE_NAME)*.jar infrastructure/docker/files/$(SMT_MODULE_NAME).jar
	docker-compose -f infrastructure/docker-compose.yml up -d --build
	rm -rf infrastructure/docker/files/$(SMT_MODULE_NAME).jar

.PHONY: down
down:
	mvn clean -f ./$(SMT_MODULE_NAME)/pom.xml
	rm -rf  infrastructure/docker/files/$(SMT_MODULE_NAME).jar
	docker-compose -f infrastructure/docker-compose.yml down
