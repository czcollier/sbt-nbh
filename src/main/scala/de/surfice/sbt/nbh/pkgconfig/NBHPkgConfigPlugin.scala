// Copyright (c) 2018. Distributed under the MIT License (see included LICENSE file).
package de.surfice.sbt.nbh.pkgconfig

import de.surfice.sbt.nbh.{NBHPlugin, Utils}
import sbt.{Def, _}

object NBHPkgConfigPlugin extends AutoPlugin {
  override def requires = NBHPlugin

  object autoImport {
    val nbhPkgConfig: SettingKey[File] =
      settingKey[File]("Path to pkg-config")

    val nbhPkgConfigModules: SettingKey[Seq[String]] =
      settingKey[Seq[String]]("pkg-config modules included when assembling nbhNativeCompileOptions and nbhNativeLinkingOptions")
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = NBHPkgConfigPluginInternal.projectSettings
}
