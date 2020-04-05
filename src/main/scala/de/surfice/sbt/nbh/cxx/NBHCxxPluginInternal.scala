// Copyright (c) 2019. Distributed under the MIT License (see included LICENSE file).
package de.surfice.sbt.nbh.cxx

import sbt._

import scala.collection.mutable
import scala.scalanative.build
import scala.scalanative.build.Config.InlineSourceHook
import scala.scalanative.nir.Attr.InlineSource
import scala.scalanative.sbtplugin.process.Process

class NBHCxxPluginInternal(buildDir: File, makeCmd: String, makePreamble: String) extends InlineSourceHook {

  val name = "compile embedded C/C++/Objective-C sources"

  override def process(srcs: Seq[InlineSource], logger: build.Logger): Option[String] =
    Run()(logger)
      .collectSnippets(srcs)
      .prepareMakefile()
      .prepareCSources()
      .prepareCxxSources()
      .prepareMSources()
      .writeFiles()
      .make()
      .stats()

  case class Run(cSnippets: Seq[InlineSource] = Nil,
                 cxxSnippets: Seq[InlineSource] = Nil,
                 mSnippets: Seq[InlineSource] = Nil,
                 makeFile: Option[String] = None,
                 cSources: Seq[(File,String)] = Nil,
                 cxxSources: Seq[(File,String)] = Nil,
                 mSources: Seq[(File,String)] = Nil)(implicit logger: build.Logger) {

    def collectSnippets(srcs: Seq[InlineSource]) = {
      val cSources = mutable.UnrolledBuffer.empty[InlineSource]
      val cxxSources = mutable.UnrolledBuffer.empty[InlineSource]
      val mSources = mutable.UnrolledBuffer.empty[InlineSource]
      srcs.foreach( src => src.language match {
        case "C" => cSources += src
        case "Cxx" | "C++" => cxxSources += src
        case "ObjC" | "Objective-C" => mSources += src
        case _ =>
      })
      copy(cSnippets = cSources, cxxSnippets = cxxSources, mSnippets = mSources)
    }

    def prepareMakefile() = {
      val ctgt = "clib.o: clib.c\n\n"
      val cxxtgt = "cxxlib.o: cxxlib.cpp\n\n"
      val mtgt = "mlib.o: mlib.m\n\n"
      val makefile = makePreamble + "\n\nall: clib.o cxxlib.o mlib.o\n\n" + ctgt + cxxtgt + mtgt
      copy(makeFile = Some(makefile))
    }

    def prepareCSources() = {
      val clib = cSnippets.map(_.source).mkString("\n")
      copy(cSources = Seq(buildDir / "clib.c" -> clib))
    }

    def prepareCxxSources() = {
      val cxxlib = cxxSnippets.map(_.source).mkString("\n")
      copy(cxxSources = Seq(buildDir / "cxxlib.cpp" -> cxxlib))
    }

    def prepareMSources() = {
      val mlib = mSnippets.map(_.source).mkString("\n")
      copy(mSources = Seq(buildDir / "mlib.m" -> mlib))
    }

    def writeFiles() = {
      IO.createDirectory(buildDir)
      IO.write(buildDir / "Makefile", makeFile.get)
      (cSources++cxxSources++mSources).foreach { p =>
        IO.write(p._1,p._2)
      }
      this
    }

    def make() = {
      Process(Seq(makeCmd),buildDir).lines_!
      this
    }

    def stats(): Option[String] =
      Some(s"${cSnippets.size} C snippets, ${cxxSnippets.size} C++ snippets, ${mSnippets.size} ObjC snippets")
  }



}
