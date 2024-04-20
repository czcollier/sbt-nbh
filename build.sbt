
val Version = new {
  val plugin       = "0.2.0-SNAPSHOT"
  val sbt10        = "1.9.9"
  val scala_native = "0.4.16"
  val package_conf = "0.2.0-SNAPSHOT"
  val scala211     = "2.11.12"
  val scala212     = "2.12.10"
  val scala3       = "3.4.1"
}

lazy val supportedScalaVersions = List(Version.scala212, Version.scala211) 

val commonSettings = Seq(
  version := Version.plugin,
  organization := "de.surfice",
  scalacOptions ++= Seq("-deprecation","-unchecked","-feature","-Xlint"),
  crossSbtVersions := Seq(Version.sbt10)
)


lazy val embed = project
  .settings(commonSettings ++ publishingSettings: _*)
  .settings(
    name := "scalanative-embed"
  )


lazy val plugin = project
  .in(file("."))
  .enablePlugins(SbtPlugin)
  .aggregate(config,embed)
  .dependsOn(embed)
  .settings(commonSettings ++ publishingSettings: _*)
  .settings(
    name := "sbt-nbh",
    sbtPlugin := true,
    scriptedLaunchOpts += "-Dplugin.version=" + version.value,
    scriptedBufferLog := false,
    addSbtPlugin("org.scala-native" % "sbt-scala-native" % Version.scala_native),
    addSbtPlugin("de.surfice" % "sbt-package-conf" % Version.package_conf),
    sourceGenerators in Compile += Def.task {
      val file = (sourceManaged in Compile).value / "Version.scala"
      IO.write(file,
        s"""package de.surfice.sbt.nbh
        |object Versions { 
        |  val plugin = "${version.value}"
        |}
        """.stripMargin)
      Seq(file)
    }.taskValue
  )


lazy val config = project
  .settings(commonSettings ++ publishingSettings: _*)
  .settings(
    name := "sbt-nbh-config",
    crossScalaVersions := supportedScalaVersions //scalaVersion := Version.scala211
  )

lazy val publishingSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra := (
    <url>https://github.com/jokade/sbt-nbh</url>
    <licenses>
      <license>
        <name>MIT License</name>
        <url>http://www.opensource.org/licenses/mit-license.php</url>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:jokade/sbt-nbh</url>
      <connection>scm:git:git@github.com:jokade/sbt-nbh.git</connection>
    </scm>
    <developers>
      <developer>
        <id>jokade</id>
        <name>Johannes Kastner</name>
        <email>jokade@karchedon.de</email>
      </developer>
    </developers>
  )
)
 
lazy val dontPublish = Seq(
    publish := {},
    publishLocal := {},
    publish / skip := true,
    publishArtifact := false,
    publishTo := Some(Resolver.file("Unused transient repository",file("target/unusedrepo")))
  )

