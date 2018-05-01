FROM maven:3-jdk-8

LABEL maintainer "Alexander Malic <alexander.malic@maastrichtuniversity.nl>"

ENV APP_DIR /app
ENV TMP_DIR /tmp/build

WORKDIR $TMP_DIR

COPY . .

RUN mvn clean install && \
    mkdir $APP_DIR && \
    mv target/r2rml-1.0.0-jar-with-dependencies.jar $APP_DIR/r2rml.jar && \
    rm -rf $TMP_DIR
    
WORKDIR $APP_DIR

ENTRYPOINT ["java","-jar","r2rml.jar"]
