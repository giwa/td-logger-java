# Treasure Data Logger for Java

## Overview

Many web/mobile applications generate huge amount of event logs (c,f. login,
logout, purchase, follow, etc).  Analyzing these event logs can be quite
valuable for improving services.  However, collecting these logs easily and 
reliably is a challenging task.

Treasure Data Logger solves the problem by having: easy installation, small 
footprint, plugins reliable buffering, log forwarding, etc.

  * Treasure Data website: [http://treasure-data.com/](http://treasure-data.com/)
  * Treasure Data GitHub: [https://github.com/treasure-data/](https://github.com/treasure-data/)

**td-logger-java** is a Java library, to record events from Java application.

## Requirements

Java >= 1.6

## Install

### Install with all-in-one jar file

You can download all-in-one jar file for Treasure Data Logger.

    $ wget http://maven.treasure-data.com/maven2/com/treasure_data/td-logger/${logger.version}/td-logger-${logger.version}-jar-with-dependencies.jar

To use Treasure Data Logger for Java, set the above jar file to your classpath.

### Install from Maven2 repository

Treasure Data Logger for Java is released on Treasure Data's Maven2 repository.
You can configure your pom.xml as follows to use it:

    <dependencies>
      ...
      <dependency>
        <groupId>com.treasure_data</groupId>
        <artifactId>td-logger</artifactId>
        <version>${logger.version}</version>
      </dependency>
      ...
    </dependencies>

    <repositories>
      <repository>
        <id>treasure-data.com</id>
        <name>Treasure Data's Maven2 Repository</name>
        <url>http://maven.treasure-data.com/maven2</url>
      </repository>
      <repository>
        <id>fluentd.org</id>
        <name>Fluentd's Maven2 Repository</name>
        <url>http://fluentd.org/maven2</url>
      </repository>
    </repositories>
    
### Install with SBT (Build tool Scala)

To install td-logger From SBT (a build tool for Scala), please add the following lines to your build.sbt.

    /* in build.sbt */
    // Repositories
    resolvers ++= Seq(
      "td-logger     Maven2 Repository" at "http://treasure-data.com/maven2/",
      "fluent-logger Maven2 Repository" at "http://fluentd.org/maven2/"
    )
    // Dependencies
    libraryDependencies ++= Seq(
      "com.treasure_data" % "td-logger" % "${logger.version}"
    )

### Install from GitHub repository

You can get latest source code using git.

    $ git clone https://github.com/treasure-data/td-logger-java.git
    $ cd td-logger-java
    $ mvn package

You will get the td-logger jar file in td-logger-java/target 
directory.  File name will be td-logger-${logger.version}-jar-with-dependencies.jar.
For more detail, see pom.xml.

**Replace ${logger.version} with the current version of Treasure Data Logger for Java.**
**The current version is 0.1.3.**

## Quickstart

### Small example with Treasure Data Logger

The following program is a small example of td-logger.

    import java.io.IOException;
    import java.util.HashMap;
    import java.util.Map;
    import java.util.Properties;
    import com.treasure_data.logger.TreasureDataLogger;

    public class Main {
        private static TreasureDataLogger LOG;

        static {
            try {
                Properties props = System.getProperties();
                props.load(Main.class.getClassLoader().getResourceAsStream("treasure-data.properties"));
                LOG = TreasureDataLogger.getLogger("my_database");
            } catch (IOException e) {
                // do something
            }
        }

        public void doApp() {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("from", "userA");
            data.put("to", "userB");
            LOG.log("my_follow_table", data);
        }
    }

See static initializer in Main class.  To create TreasureDataLogger objects,
you need to invoke getLogger method in TreasureDataLogger class like
well-known logging libraries.  The method should be called only once.

Close method in TreasureDataLogger class should be called explicitly when 
your application is finished (or undeployed).  Once the method is executed,
all TreasureDataLogger objects that you created are closed.

    TreasureDataLogger.close();

See doApp method in Main class.  log method enables you to upload event logs.
Event logs should be declared as variables of Map\<String, Object\> type.
The key is String type.  The type of value is one of the followings: int,
long, string, float, double.

### Direct upload and In-direct upload

You can choose how to upload event logs from the following methods.
Installing td-agent is [here](http://help.treasure-data.com/kb/installing-td-agent-daemon).

  * Direct upload from your applications
  * In-direct upload from td-agent

To switch between them, you can specify Java options and system properties on
the command line, or by using an options file.  If you want to directly upload
event logs from your application, you should set your own values to the
following properties in treasure-data.properties file.

    td.logger.agentmode=false
    td.logger.api.key=<your API key>

On the other hand if you want to upload data via td-agent, you should declare
the following properties in the file.

    td.logger.agentmode=true
    td.logger.agent.host=<your td-agent host>
    td.logger.agent.port=<your td-agent port>

## License

Apache License, Version 2.0
