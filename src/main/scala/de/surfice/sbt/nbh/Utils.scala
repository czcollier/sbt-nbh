// Copyright (c) 2018. Distributed under the MIT License (see included LICENSE file).
package de.surfice.sbt.nbh

import java.net.JarURLConnection
import java.nio.file.{CopyOption, Files, Path, StandardCopyOption}

import sbt.Keys.Classpath
import sbt._

import scala.scalanative.sbtplugin.Utilities.SilentLogger

object Utils {
  import scala.collection.JavaConverters._

  def discover(binaryName: String, suffixes: Seq[String] = Seq("")): File = {
    val envName = binaryName.toUpperCase

    sys.env.get(s"${envName}_PATH") match {
      case Some(path) => file(path)
      case None => {
        val binaryNames = suffixes.map(binaryName + _)
        Process("which" +: binaryNames)
          .lines_!(SilentLogger)
          .map(file(_))
          .headOption
          .getOrElse{
            throw new MessageOnlyException(
              s"no ${binaryNames.mkString(", ")} found in $$PATH!"
            )
          }
      }
    }
  }

  def resourceJars(cp: Classpath): Seq[File] = cp.files.filter(!_.isDirectory)

  def copyResourceDir(jars: Seq[File], srcPath: File, targetPath: File): Unit =
    if(! targetPath.exists())
      if(srcPath.exists())
        IO.copyDirectory(srcPath,targetPath)
      else {
        jars.find { f =>
          val path = f.toPath
          copyFromJar(path,srcPath.toString,targetPath.toPath)
        } match {
          case None => sys.error("Could not copy resource directory: "+srcPath)
          case _ =>
        }
      }


  def copyFromJar(resource: java.nio.file.Path, source: String, target: java.nio.file.Path): Boolean = {

    import java.nio.file.{
      Files,
      FileSystems,
      FileVisitResult,
      SimpleFileVisitor,
      Paths,
      Path
    }
    import java.nio.file.attribute.BasicFileAttributes

    val fileSystem = FileSystems.newFileSystem(resource, null)
    val jarPath = fileSystem.getPath(source)
    if(!Files.exists(jarPath))
      return false
    Files.walkFileTree(jarPath, new SimpleFileVisitor[java.nio.file.Path]() {

      override def preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult = {
        val dest = Paths.get(target.toString, dir.toString)
        Files.createDirectories(dest)
        FileVisitResult.CONTINUE
      }

      override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
        val dest = Paths.get(target.toString, file.toString)
        Files.copy(file, dest, StandardCopyOption.REPLACE_EXISTING)
        FileVisitResult.CONTINUE
      }
    })
    true
  }

}
