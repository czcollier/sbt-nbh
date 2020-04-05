// Copyright (c) 2018. Distributed under the MIT License (see included LICENSE file).
package de.surfice.sbt.nbh

import sbt._
import Keys._

import collection.JavaConverters._

object NBHPluginInternal {

  lazy val nbhLinkLibrariesComputed: TaskKey[Seq[String]] =
    taskKey("libraries to be linked defined explicitly and in package.conf files")

  lazy val nbhLinkFrameworksComputed: TaskKey[Seq[String]] =
    taskKey("frameworks to be linked defined explicitly and in package.conf files")

  import NBHPlugin.autoImport._
  import de.surfice.sbt.pconf.PConfPlugin.autoImport._

  lazy val projectSettings: Seq[Setting[_]] = Seq(
    libraryDependencies += ModuleID("de.surfice","sbt-nbh-config",Versions.plugin),
      //DepBuilder.toGroupID("de.surfice") %% "sbt-nbh-config" % Versions.plugin,

    pconfDefaultConfigPrefix := "sbt-nbh-config_",

    nbhLinkLibraries := Nil,

    nbhLinkLibrariesComputed := {
      (nbhLinkLibraries.value ++ pconfConfig.value.getStringList("nbh.link.libraries").asScala)
        .flatMap(Seq("-l",_))
    },

    nbhLinkFrameworks := Nil,

    nbhLinkFrameworksComputed := {
      (nbhLinkFrameworks.value ++ pconfConfig.value.getStringList("nbh.link.frameworks").asScala)
        .flatMap(Seq("-framework",_))
    },

    nbhNativeCompileOptions := Nil,

    nbhNativeLinkingOptions := nbhLinkFrameworksComputed.value ++ nbhLinkLibrariesComputed.value

  )

//  private object DepBuilder extends DependencyBuilders
}
