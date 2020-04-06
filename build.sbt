name := "functionnaldatapj"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies += "net.liftweb" %% "lift-json" % "3.4.0"
libraryDependencies += "org.apache.kafka" %% "kafka" % "2.4.1"
libraryDependencies += "jp.co.bizreach" %% "aws-kinesis-scala" % "0.0.12"
// https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-cbor
libraryDependencies += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor" % "2.10.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.8.1"
