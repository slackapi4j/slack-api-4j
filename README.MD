#Slack Api 4 Java 

![Maven Badge](https://img.shields.io/maven-metadata/v/https/repo1.maven.org/maven2/io/github/slackapi4j/slack-api-4j/maven-metadata.xml.svg)
![Nexus SnapShots](https://img.shields.io/nexus/s/https/oss.sonatype.org/io.github.slackapi4j/slack-api-4j.svg)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/44d12e126c7d446d93611327a31fdc2c)](https://app.codacy.com/app/Narimm/slack-api-4j?utm_source=github.com&utm_medium=referral&utm_content=slackapi4j/slack-api-4j&utm_campaign=Badge_Grade_Dashboard)
[![Join the chat at https://gitter.im/slack-api-4j/community](https://badges.gitter.im/slack-api-4j/community.svg)](https://gitter.im/slack-api-4j/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
![Last Commit](https://img.shields.io/github/last-commit/slackapi4j/slack-api-4j.svg)
![Travis CI](https://travis-ci.com/slackapi4j/slack-api-4j.svg?branch=master)

Is an implementation of the Slack  api 

It uses both realtime sessions plus the post/get Api to recieve and send messages to Slack 

You can use it to create slack aware bots in Java.

We are working on adding the Event - API as well 

Thanks
Narimm

###Stable

For the Stable Release
        
        <repositories>
          <repository>
            <id>ossrh</id>
            <url>https://oss.sonatype.org/content/repositories/snapshots</url>
            <snapshots>
              <enabled>true</enabled>
            </snapshots>
          </repository>
        </repositories>
        <dependency>
          <groupId>io.github.slackapi4j</groupId>
          <artifactId>slack-api-4j</artifactId>
          <version>0.0.2</version>
        </dependency>

###Snapshots

        <dependency>
          <groupId>io.github.slackapi4j</groupId>
          <artifactId>slack-api-4j</artifactId>
          <version>0.0.3-SNAPSHOT</version>
        </dependency>