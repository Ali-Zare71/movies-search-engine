FROM openjdk:8-slim

RUN /bin/sh -c set -eux ; \
    apt-get update ; \
    apt-get upgrade -y --no-install-recommends ; \
    rm -rf /var/lib/apt/lists/*

ENV APP_HOME /opt/search-service
WORKDIR $APP_HOME
EXPOSE 8080

ARG JAR_FILE=*.jar
ENV JAR_FILE=$JAR_FILE \
    MEMORY_OPTS="-Xms1g -Xmx2g"

# sh -c 'exec ...' is a workaround to resolve $VARs in runtime
CMD ["sh", "-c", "exec java $MEMORY_OPTS -jar $APP_HOME/$JAR_FILE"]

#COPY target/libs/*.jar $APP_HOME/libs/
COPY target/$JAR_FILE $APP_HOME
