addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.4.0-SNAPSHOT")

addSbtPlugin("de.surfice" % "sbt-package-conf" % "0.1.1")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")


libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value
