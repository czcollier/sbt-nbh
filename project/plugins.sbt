addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.3.6")

addSbtPlugin("de.surfice" % "sbt-package-conf" % "0.1.0")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")


libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value
