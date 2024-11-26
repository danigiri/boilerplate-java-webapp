# boilerplate-java-webapp
Boilerplate for java webapp using maven and jetty


#### Installing

Upgrade depdencies (optional):




#### Compilation

```shell
mvn compile
```


#### Testing

Run tests:

```shell
mvn test
```

Manually testing async servlet (many of the requests will time out):

```shell
mvn compile jetty:run
# on separate window
for i in `seq 10`; do curl http://localhost:8080/async-hello/\?seconds\=1 & echo ''; done
```


#### Integration testing
```shell
mvn verify
```

### Docker

Note this example uses a Maven Central mirror, remove the buid arg and the extra host to use Maven Centrall

```shell
docker build -t hello-world \
  --build-arg MAVEN_CENTRAL_MIRROR=http://reposilite.h0.local.test/maven-central \
  --add-host=reposilite.h0.local.test:192.168.1.31 \
  .
```


#### Releasing source


```shell
mvn -B release:prepare release:perform \
	-DscmReleaseCommitComment='@{prefix} @{releaseLabel} - RELEASE MESSAGE' \
	-Darguments=" -DaltDeploymentRepository=REPO::default::file://$HOME/.m2/repository" -U
 ```

#### Webapps with Jetty

Jetty configuration:

List modules with: `java -jar ./start.jar --list-modules`
Generate module ini files: `java -jar ../jetty-home-12.0.15/start.jar --add-modules=server,deploy,ee10-deploy,ext,http,requestlog,resources,rewrite`

which generates this structure:

```
├── etc
│   └── jetty-rewrite-rules.xml
├── lib
│   └── ext
├── logs
├── resources
│   └── jetty-logging.properties
├── start.d
│   ├── deploy.ini
│   ├── ee10-deploy.ini
│   ├── ext.ini
│   ├── http.ini
│   ├── requestlog.ini
│   ├── resources.ini
│   ├── rewrite.ini
│   └── server.ini
└── webapps
```


Default paths of maven jetty plugin jetty:run:
- resources in `${project.basedir}/src/main/webapp`
- classes in `${project.build.outputDirectory}`
- web.xml in `${project.basedir}/src/main/webapp/WEB-INF/`



#### References

- https://maven.apache.org/ref/3.9.9/maven-model/maven.html
- https://maven.apache.org/surefire/maven-failsafe-plugin/examples/junit-platform.html
- https://junit.org/junit5/docs/current/user-guide/
- https://site.mockito.org/
- https://jetty.org/docs/jetty/12/index.html
- https://jetty.org/docs/jetty/12/programming-guide/client/index.html
- https://jakarta.ee/learn/docs/jakartaee-tutorial/current/web/webapp/webapp.html
- https://www.hackerearth.com/practice/notes/asynchronous-servlets-in-java/
