// Copyright (c) 2018. Distributed under the MIT License (see included LICENSE file).
package de.surfice.sbt.nbh.pkgconfig

import de.surfice.sbt.nbh.NBHPlugin
import sbt.{Def, _}

object NBHPkgConfigPlugin extends AutoPlugin {
  override def requires = NBHPlugin

  object autoImport {
    val nbhPkgConfig: SettingKey[File] =
      settingKey[File]("Path to pkg-config")

    val nbhPkgConfigModules: SettingKey[Seq[String]] =
      settingKey[Seq[String]]("pkg-config modules included when assembling nbhNativeCompileOptions and nbhNativeLinkingOptions")

    val nbhPkgConfigLinkingFlags: TaskKey[Seq[String]] =
      taskKey[Seq[String]]("result of `pkg-config --libs ${nbhPkgConfigModules}`")

    val nbhPkgConfigCFlags: TaskKey[Seq[String]] =
      taskKey[Seq[String]]("result of `pkg-config --cflags ${nbhPkgConfigModules}")

    val nbhPkgConfigModulesComputed: TaskKey[Seq[String]] =
      taskKey[Seq[String]]("pkg-config modules defined explicitly and in package-conf files")

  }


  override def projectSettings: Seq[Def.Setting[_]] = NBHPkgConfigPluginInternal.projectSettings
}
