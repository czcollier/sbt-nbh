// Copyright (c) 2018. Distributed under the MIT License (see included LICENSE file).
package de.surfice.sbt.nbh

import sbt.{Def, _}
import Keys._
import de.surfice.sbt.pconf.PConfPlugin

import scala.scalanative.sbtplugin.ScalaNativePlugin

object NBHPlugin extends AutoPlugin {

  override val requires: Plugins = ScalaNativePlugin && PConfPlugin

  object autoImport {
    val nbhLinkFrameworks: SettingKey[Seq[String]] =
      settingKey("list of frameworks to link against")

    val nbhLinkLibraries: SettingKey[Seq[String]] =
      settingKey("list of libraries to link against")

    val nbhNativeCompileOptions: TaskKey[Seq[String]] =
      taskKey[Seq[String]]("nativeCompileOptions assembled by sbt-nbh")

    val nbhNativeLinkingOptions: TaskKey[Seq[String]] =
      taskKey[Seq[String]]("nativeLinkingOptions assembled by sbt-nbh")

  }

  override def projectSettings: Seq[Def.Setting[_]] = NBHPluginInternal.projectSettings

}
