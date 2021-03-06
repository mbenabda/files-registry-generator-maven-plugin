Generate a java registry class hierarchy from files under a given directory

Usage
-----
The plugin is not yet on maven central, but you can add the following repositoy declaration in your ```pom.xml```:
```xml
    <pluginRepositories>
        <pluginRepository>
            <id>files-registry-maven-plugin-releases</id>
            <url>http://maven.mbenabda.com/repository/files-registry-maven-plugin-releases/</url>
        </pluginRepository>
    </pluginRepositories>
```
and then, configure the plugin as such:
```xml
 <build>
        <plugins>
        ...
            <plugin>
                <groupId>com.mbenabda.maven.plugins</groupId>
                <artifactId>files-registry-generator-maven-plugin</artifactId>
                <version>1.0</version>
                <executions>
                    <execution>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>generate</goal>
                        </goals>
                    </execution>
                </executions>

                <configuration>
                    <!-- tell the plugin which files you want in the registry -->
                    <relativeFilesLocation>src/main/resources/templates</relativeFilesLocation>
                    <filenameSuffix>.html</filenameSuffix>
                    <!-- and where you want the registry to be generated -->
                    <registryPackageName>com.yourcompany.app</registryPackageName>
                    <registryClassName>TemplatesRegistry</registryClassName>
                </configuration>
            </plugin>
        ...
        <plugins>
 <build>
```

TODO
----
* conflict resolution (ie: what happens when 2 files of the same folder reduce to the same java identifier)
