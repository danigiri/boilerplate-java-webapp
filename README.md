# boilerplate-java-webapp
Boilerplate for java webapp using Maven and Jetty


## RUNNING


### Docker

Note this example uses a Maven Central mirror, remove the buid arg and the extra host to use Maven Central directly

```shell
docker build -t hello-world \
  --build-arg MAVEN_CENTRAL_MIRROR=http://reposilite.h0.local.test/maven-central \
  --add-host=reposilite.h0.local.test:192.168.1.31 \
  .
docker run --rm -p 8080:8080 hello-world
```

Be aware that if not set, configuration env vars are being sent empty into the container, if you want them completely unset, comment them out in the dockerfile


### Cli

Creating an complete jar to execute as a cli (from project folder):

```shell
alias HelloWorldCLI="java -cp "$(ls -1 $PWD/target/*-classes.jar)":"$(ls -1 $PWD/target/*-jar-with-dependencies.jar)" cat.calidos.boilerplate.cli.HelloWorldCLI"
HelloWorldCLI 'from CLI'
```



## DEVELOPMENT

#### Installing

Upgrade depdencies (optional):


List dependencies:

```shell
 mvn org.apache.maven.plugins:maven-dependency-plugin:3.8.1:tree
```

### Configuration

- `__REQUEST_TIMEOUT_MS`: How many threads do async servlets have
- `__ASYNC_THREAD_COUNT`: How many milliseconds to wait for async request before timing out
- `__SHUTDOWN_TIMEOUT_SEC`: How many seconds to wait for async requests to finish in shutdown

Can be changed in `web.xml`, set as `-D` java properties, or as env vars, each of those will override

#### Compilation

```shell
mvn compile
```


#### Testing

Run tests:

```shell
mvn test
```

Manually testing thread async servlet:

```shell
mvn compile jetty:run -D__REQUEST_TIMEOUT_MS=10000
# on separate window
for i in `seq 10`; do curl http://localhost:8080/async-hello/\?seconds\=1 & echo ''; done
```

Manually testing virtual thread servlet
for i in `seq 10`; do curl http://localhost:8080/vt-hello/\?seconds\=1 & echo ''; done


#### Integration testing
```shell
mvn verify
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
- https://picocli.info/quick-guide.html
