name := "adhoc-java"

organization := "com.appadhoc"

version := "0.0.10"

description := "http://www.appadhoc.com"

crossPaths := false

autoScalaLibrary := false

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

libraryDependencies ++= Seq(
  "junit" % "junit" % "3.8.1" % "test",
  "org.json" % "json" % "20090211"
)

useGpg := true

pgpSecretRing := file("/Users/gmi/.gnupg/secring.gpg")
pgpPublicRing := file("/Users/gmi/.gnupg/pubring.gpg")

pomExtra := (
  <url>https://github.com/AppAdhoc/adhoc-java.git/</url>
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/AppAdhoc/adhoc-java.git/</url>
      <connection>scm:git:https://github.com/AppAdhoc/adhoc-java.git</connection>
      <developerConnection>scm:git:https://github.com/AppAdhoc/adhoc-java.git</developerConnection>
  </scm>
  <developers>
    <developer>
      <organization>appadhoc</organization>
      <organizationUrl>http://www.appadhoc.com</organizationUrl>
      <timezone>8</timezone>
    </developer>
  </developers>
)
