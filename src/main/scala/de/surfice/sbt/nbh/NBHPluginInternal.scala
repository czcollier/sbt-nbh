// Copyright (c) 2018. Distributed under the MIT License (see included LICENSE file).
package de.surfice.sbt.nbh

import sbt._
import Keys._
import sbt.impl.DependencyBuilders
import collection.JavaConverters._

object NBHPluginInternal {
  lazy val nbhLinkFrameworksComputed: TaskKey[Seq[String]] =
    taskKey("frameworks to be linked defined explicitly and in package.conf files")

  import NBHPlugin.autoImport._
  import de.surfice.sbt.pconf.PConfPlugin.autoImport._

  lazy val projectSettings: Seq[Setting[_]] = Seq(
    libraryDependencies += DepBuilder.toGroupID("de.surfice") %% "sbt-nbh-config" % Versions.plugin,

    pconfDefaultConfigPrefix := "sbt-nbh-config_",

    nbhLinkFrameworks := Nil,

    nbhLinkFrameworksComputed := {
      (nbhLinkFrameworks.value ++ pconfConfig.value.getStringList("nbh.link.frameworks").asScala)
        .flatMap(Seq("-framework",_))
    },

    nbhNativeCompileOptions := Nil,

    nbhNativeLinkingOptions := nbhLinkFrameworksComputed.value

  )

  private object DepBuilder extends DependencyBuilders
}
