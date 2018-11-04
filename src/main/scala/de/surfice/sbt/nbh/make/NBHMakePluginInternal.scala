// Copyright (c) 2018. Distributed under the MIT License (see included LICENSE file).
package de.surfice.sbt.nbh.make

import sbt._
import Keys._
import de.surfice.sbt.nbh.Utils
import de.surfice.sbt.nbh.Utils._

import collection.JavaConverters._

object NBHMakePluginInternal {
  import de.surfice.sbt.nbh.make.NBHMakePlugin.autoImport._
  import de.surfice.sbt.nbh.NBHPlugin.autoImport._
  import de.surfice.sbt.pconf.PConfPlugin.autoImport._

  val nbhMakeLinkingFlags: TaskKey[Seq[String]] =
    taskKey[Seq[String]]("result of nbhMake")

  val nbhMakeProjectsComputed: TaskKey[Seq[NBHMakeProject]] =
    taskKey[Seq[NBHMakeProject]]("nbhMake projects defined explicitly and in package-conf files")

  val nbhMakeCopyProjects: TaskKey[Seq[NBHMakeProject]] =
    taskKey[Seq[NBHMakeProject]]("copies the sources of all nbhMake projects to the target dir")

  lazy val projectSettings: Seq[Setting[_]] = Seq(
    nbhMakeCmd := discover("make"),
    nbhMakeProjects := Nil,

    nbhMake := {
      val make = nbhMakeCmd.value.absolutePath
      nbhMakeCopyProjects.value flatMap { p =>
        Process(Seq(make) ++ p.flags,p.targetPath)
            .lines_!
        p.artifacts.map(_.copy(targetDir = Some(p.targetPath)))
      }
    },

    nbhNativeLinkingOptions := {
      nbhNativeLinkingOptions.value ++ nbhMake.value.map(_.path.get.absolutePath)
    },

    nbhMakeProjectsComputed := {
      val config = pconfConfig.value
       nbhMakeProjects.value ++ config.getObject("nbh.make.projects").keySet().asScala
        .map("nbh.make.projects."+_)
        .map(config.getConfig)
        .map{ proj =>
          val srcPath = proj.getString("srcPath")
          val artifacts = if(proj.hasPath("artifacts")) proj.getStringList("artifacts").asScala else Nil
          val flags = if(proj.hasPath("flags")) proj.getStringList("flags").asScala else Nil
          NBHMakeProject(file(srcPath),artifacts.map(NBHMakeArtifact.apply),flags)
        }
    },

    nbhMakeCopyProjects := {
      val target = crossTarget.value / "nbh"
      val jars = Utils.resourceJars((dependencyClasspath in Compile).value)

      nbhMakeProjectsComputed.value.map{ p =>
        val upd = p.copy(targetPrefix = Some(target))
        Utils.copyResourceDir(jars,upd.srcPath,target)
        upd
      }
    }
  )

}
