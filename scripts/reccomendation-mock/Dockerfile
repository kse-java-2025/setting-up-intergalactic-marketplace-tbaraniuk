FROM ubuntu:latest

ENV WIREMOCK_VERSION 3.9.1

RUN mkdir /var/wiremock
RUN mkdir /var/wiremock/mappings

WORKDIR /var/wiremock

RUN wget -O /var/wiremock/wiremock-standalone.jar https://repo1.maven.org/maven2/org/wiremock/wiremock-standalone/$WIREMOCK_VERSION/wiremock-standalone-$WIREMOCK_VERSION.jar

ADD ./stubs.json /var/wiremock/mappings

EXPOSE 8080 8443

CMD java -jar /var/wiremock/wiremock-standalone.jar -global-response-templating