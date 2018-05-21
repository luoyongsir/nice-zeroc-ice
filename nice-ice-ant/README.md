# ice-ant
用来替代ant-ice，兼容ZeroC ICE各个版本

maven 使用示例

    <properties>
        <src.code.dir>src/main/java</src.code.dir>
        <ice.file.dir>src/main/resources</ice.file.dir>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.zeroc</groupId>
            <artifactId>ice</artifactId>
            <version>your.version</version>
        </dependency>
        <dependency>
            <groupId>com.zeroc</groupId>
            <artifactId>ice-ant</artifactId>
            <version>latest.version</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-antrun-plugin</artifactId>
                <version>1.8</version>
                <executions>
                    <execution>
                        <id>deleteJavaCode</id>
                        <phase>clean</phase>
                        <goals>
                            <goal>run</goal>
                        </goals>
                        <configuration>
                            <target name="delete" description="删除java代码">
                                <delete dir="${src.code.dir}/com"/>
                            </target>
                        </configuration>
                    </execution>
                    <execution>
                        <id>slice2java</id>
                        <phase>clean</phase>
                        <goals>
                            <goal>run</goal>
                        </goals>
                        <configuration>
                            <target name="slice2java">
                                <taskdef name="slice2java" classname="Slice2JavaTask"
                                         classpathref="maven.plugin.classpath"/>
                                <slice2java outputdir="${src.code.dir}">
                                    <fileset dir="${ice.file.dir}" includes="**/*.ice"/>
                                </slice2java>
                            </target>
                        </configuration>
                    </execution>
                </executions>
                <dependencies>
                    <dependency>
                        <groupId>com.zeroc</groupId>
                        <artifactId>ice-ant</artifactId>
                        <version>latest.version</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
        <sourceDirectory>src/main/java</sourceDirectory>
    </build>