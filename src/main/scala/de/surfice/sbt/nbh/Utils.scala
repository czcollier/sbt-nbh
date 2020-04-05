// Copyright (c) 2018. Distributed under the MIT License (see included LICENSE file).
package de.surfice.sbt.nbh

import java.nio.file.StandardCopyOption

import sbt.Keys.Classpath
import sbt._

import scala.scalanative.sbtplugin.Utilities.SilentLogger
import scala.scalanative.sbtplugin.process.Process

object Utils {

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

  def resourceDirs(cp: Classpath): Seq[File] = cp.files.filter(_.isDirectory)

  def copyResourceDir(dirs: Seq[File], srcPath: File, targetPath: File): Unit =
    if(! targetPath.exists())
      if(srcPath.exists())
        IO.copyDirectory(srcPath,targetPath)
      else {
        dirs.find { f =>
          val path = f.toPath
          if(f.isDirectory)
            copyFromDir(path,srcPath.toString,targetPath.toPath)
          else
            copyFromJar(path,srcPath.toString,targetPath.toPath)
        } match {
          case None => sys.error("Could not copy resource directory: "+srcPath)
          case _ =>
        }
      }

  def copyFromDir(resource: java.nio.file.Path, source: String, target: java.nio.file.Path): Boolean = {
    val srcdir = resource.resolve(source).toFile
    val tgtdir = target.resolve(source).toFile
    if(srcdir.exists()) {
      IO.copyDirectory(srcdir,tgtdir,true,true)
      true
    }
    else false
  }

  def copyFromJar(resource: java.nio.file.Path, source: String, target: java.nio.file.Path): Boolean = {

    import java.nio.file.attribute.BasicFileAttributes
    import java.nio.file.{FileSystems, FileVisitResult, Files, Path, Paths, SimpleFileVisitor}

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
