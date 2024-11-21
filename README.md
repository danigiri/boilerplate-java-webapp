# boilerplate-java-webapp
Boilerplate for java webapp using maven and jetty


#### Installing

Upgrade depdencies (optional):




#### Compilation

```shell
mvn compile
```


#### Testing

```shell
mvn test
```

Manually testing async servlet:

```shell
https://www.hackerearth.com/practice/notes/asynchronous-servlets-in-java/
```


#### Integration testing
```shell
mvn verify
```

#### Releasing


```shell
mvn -B release:prepare release:perform \
	-DscmReleaseCommitComment='@{prefix} @{releaseLabel} - RELEASE MESSAGE' \
	-Darguments=" -DaltDeploymentRepository=REPO::default::file://$HOME/.m2/repository" -U
 ```

#### Webapps



Defaults of jetty:run:
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