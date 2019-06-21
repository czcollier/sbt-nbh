
val Version = new {
  val plugin       = "0.1.2"
  val sbt13        = "0.13.17"
  val sbt10        = "1.2.0"
  val scala_native = "0.3.8"
  val package_conf = "0.1.1"
  val scala211     = "2.11.12"
}

val commonSettings = Seq(
  version := Version.plugin,
  organization := "de.surfice",
  scalacOptions ++= Seq("-deprecation","-unchecked","-feature","-Xlint"),
  crossSbtVersions := Seq(Version.sbt13, Version.sbt10)
)

lazy val plugin = project
  .in(file("."))
  .aggregate(config)
  .settings(commonSettings ++ publishingSettings: _*)
  .settings(
    name := "sbt-nbh",
    sbtPlugin := true,
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
    scalaVersion := Version.scala211
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
    com.typesafe.sbt.pgp.PgpKeys.publishSigned := {},
    com.typesafe.sbt.pgp.PgpKeys.publishLocalSigned := {},
    publishArtifact := false,
    publishTo := Some(Resolver.file("Unused transient repository",file("target/unusedrepo")))
  )

lazy val scriptedSettings = ScriptedPlugin.scriptedSettings ++ Seq(
  scriptedLaunchOpts += "-Dplugin.version=" + version.value,
  scriptedBufferLog := false
)
