// Copyright (c) 2018. Distributed under the MIT License (see included LICENSE file).
package de.surfice.sbt.nbh

import sbt.{Def, _}
import de.surfice.sbt.nbh.make.NBHMakePlugin
import de.surfice.sbt.nbh.pkgconfig.NBHPkgConfigPlugin

object NBHAutoPlugin extends AutoPlugin {
  override def requires = NBHPkgConfigPlugin && NBHMakePlugin

  import scala.scalanative.sbtplugin.ScalaNativePlugin.autoImport._
  import NBHPlugin.autoImport._

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    nativeCompileOptions ++= nbhNativeCompileOptions.value,
    nativeLinkingOptions ++= nbhNativeLinkingOptions.value
  )
}
