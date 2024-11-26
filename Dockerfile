#### BUILD STAGE ####
FROM eclipse-temurin:21 AS build
LABEL maintainer="Daniel Giribet - dani [at] calidos [dot] cat"

# variables build stage
ARG MAVEN_URL=https://archive.apache.org/dist/maven/maven-3/3.9.8/binaries/apache-maven-3.9.8-bin.tar.gz
ARG MAVEN_HOME=/usr/share/maven
# set a maven repo URL and a matching repo name ('central' recommended), like
# --build-arg MAVEN_CENTRAL_MIRROR=http://REPOHOSTNAME/maven-central  --add-host=REPOHOSTNAME:IP
ARG MAVEN_CENTRAL_MIRROR=none
ENV MAVEN_CENTRAL_MIRROR_=${MAVEN_CENTRAL_MIRROR}

# install maven
RUN mkdir -p ${MAVEN_HOME}
RUN curl ${MAVEN_URL} | tar zxf - -C ${MAVEN_HOME} --strip-components 1
RUN ln -s ${MAVEN_HOME}/bin/mvn /usr/bin/mvn

# we add the pom and validate the project to get some dependencies cached
# and this layer will not be re-built unless the pom is changed
# if we have set a mirror, we have a minimal settings file that will get replaced
COPY pom.xml pom.xml
COPY src/main/resources/docker/settings.xml /tmp/settings.xml
RUN if [ "${MAVEN_CENTRAL_MIRROR_}" != 'none' ]; then \
  sed -i "s^MAVEN_CENTRAL_MIRROR^${MAVEN_CENTRAL_MIRROR_}^" /tmp/settings.xml && \
  mkdir -v ${HOME}/.m2 &&  cp -v /tmp/settings.xml ${HOME}/.m2; \
fi

RUN if [ "${MAVEN_CENTRAL_MIRROR_}" != 'none' ]; then \
  /usr/bin/mvn -Daether.connector.basic.downstreamThreads=2 dependency:go-offline; \
  else \
  /usr/bin/mvn dependency:go-offline; \
 fi

 # add code
 COPY src src

 # and build (in multiple steps steps to try to reuse the lengthy maven download)
 RUN /usr/bin/mvn compile
 RUN /usr/bin/mvn package verify


#### MAIN STAGE ####
FROM eclipse-temurin:21 AS main

# arguments and variables run stage
ENV JETTY_HOME /var/lib/jetty
ENV JETTY_URL https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-home/12.0.15/jetty-home-12.0.15.tar.gz
ARG JETTY_BASE=/jetty-base

# install any further runtime dependency packages here
# RUN apt-get install 

# creating jetty home, this is where jetty unmodified binaries and configuration will sit
RUN mkdir -p ${JETTY_HOME}
RUN curl ${JETTY_URL} | tar zxf - -C ${JETTY_HOME} --strip-components 1

# creating jetty base with configuration files
COPY --from=build ./src/main/resources/jetty ${JETTY_BASE}
# create bunch of folders that jetty will need to run and adding files
RUN mkdir -p ${JETTY_BASE}/webapps ${JETTY_BASE}/resources ${JETTY_BASE}/lib/ext ${JETTY_BASE}/logs
COPY --from=build ./target/classes/jetty-logging.properties /${JETTY_BASE}/resources

# add generated war
COPY --from=build ./target/*.war ${JETTY_BASE}/webapps/root.war

# add any test data if needed
#RUN mkdir -p ${JETTY_BASE}/target/test-classes/test-resources
# COPY --from=build ./target/test-classes/test-resources ${JETTY_BASE}/target/test-classes/test-resources

# start jetty from its base folder, this way of starting it means
# we do not do a fork of the java process to run jetty, and also means ENV vars will be accessible
# received
WORKDIR ${JETTY_BASE}
ENTRYPOINT sh -c "$(java -jar ${JETTY_HOME}/start.jar --dry-run)"
