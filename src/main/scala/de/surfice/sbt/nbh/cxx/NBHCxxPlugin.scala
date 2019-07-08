// Copyright (c) 2019. Distributed under the MIT License (see included LICENSE file).
package de.surfice.sbt.nbh.cxx

import java.nio.file.Files

import de.surfice.sbt.nbh.NBHPlugin
import sbt._
import Keys._
import de.surfice.sbt.nbh.make.NBHMakePlugin
import de.surfice.sbt.nbh.pkgconfig.NBHPkgConfigPlugin

import scala.scalanative.build.Config.InlineSourceHook
import scala.scalanative.nir.Attr.InlineSource

object NBHCxxPlugin extends AutoPlugin {
  override def requires = NBHMakePlugin // && NBHPkgConfigPlugin


  object autoImport {
    val nbhCxxBuildDir = settingKey[File]("Build directory for compilation of embedded C/C++ sources")

//    val nbhCCompiler = taskKey[File]("Compiler used to compile embedded C source snippets")

//    val nbhCxxCompiler = taskKey[File]("Compiler used to compile embedded C++ source snippets")

    val nbhCxxCFlags = settingKey[Seq[String]]("CFLAGS used to compile embedded C source")

    val nbhCxxCXXFlags = settingKey[Seq[String]]("CXXFLAGS used to compile embedded C++ sources")

    val nbhCxxLDFlags = settingKey[Seq[String]]("LDFLAGS used to link code generated from embedded sources")

    val nbhCxxMakePreamble = settingKey[String]("Preamble added to makefile")
  }

  import scala.scalanative.sbtplugin.ScalaNativePlugin.autoImport._
  import NBHMakePlugin.autoImport._
  import autoImport._

  override def projectSettings = Seq(

    nbhCxxBuildDir := crossTarget.value / "NBHCxxPlugin",

    nbhCxxCFlags := Nil,
    nbhCxxCXXFlags := Nil,
    nbhCxxLDFlags := Nil,

    nbhCxxMakePreamble := {
      val cflags = nbhCxxCFlags.value.mkString(" ")
      val cxxflags = nbhCxxCXXFlags.value.mkString(" ")
      val ldflags = nbhCxxLDFlags.value.mkString(" ")
      s"""CFLAGS=$cflags
         |CXXFLAGS=$cxxflags
       """.stripMargin
    },

    nativeInlineSourceHooks += {
      val buildDir = nbhCxxBuildDir.value
      val makeCmd = nbhMakeCmd.value.absolutePath
      new NBHCxxPluginInternal(buildDir,makeCmd,nbhCxxMakePreamble.value)
    },

    nativeLinkingOptions ++= Seq(
      (nbhCxxBuildDir.value / "clib.o").getAbsolutePath,
      (nbhCxxBuildDir.value / "cxxlib.o").getAbsolutePath,
      (nbhCxxBuildDir.value / "mlib.o").getAbsolutePath
    ) ++ nbhCxxLDFlags.value
  )
}
