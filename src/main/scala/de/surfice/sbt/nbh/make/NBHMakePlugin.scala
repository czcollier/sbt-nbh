// Copyright (c) 2018. Distributed under the MIT License (see included LICENSE file).
package de.surfice.sbt.nbh.make

import de.surfice.sbt.nbh.NBHPlugin
import sbt._

object NBHMakePlugin extends AutoPlugin {
  override def requires = NBHPlugin

  object autoImport {
    val nbhMakeCmd: SettingKey[File] =
      settingKey[File]("path to make")

    val nbhMakeProjects: SettingKey[Seq[NBHMakeProject]] =
      settingKey[Seq[NBHMakeProject]]("List of projects to be compiled with make")

    val nbhMake: TaskKey[Seq[NBHMakeArtifact]] =
      taskKey[Seq[NBHMakeArtifact]]("call make for all defined nbhMakeProjects and returns a list of artifacts built by these projects")

    case class NBHMakeArtifact(relPath: File, targetDir: Option[File] = None) {
      def path: Option[File] = targetDir.map(p => IO.resolve(p,relPath))
    }
    object NBHMakeArtifact {
      def apply(relPath: String): NBHMakeArtifact = NBHMakeArtifact(file(relPath))
    }

    case class NBHMakeProject(srcPath: File, artifacts: Seq[NBHMakeArtifact], flags: Seq[String] = Nil, targetPrefix: Option[File] = None) {
      def targetPath: File = targetPrefix match {
        case None => srcPath.getAbsoluteFile
        case Some(prefix) => IO.resolve(prefix.getAbsoluteFile,srcPath)
      }
    }
  }

  override def projectSettings: Seq[Def.Setting[_]] = NBHMakePluginInternal.projectSettings
}
