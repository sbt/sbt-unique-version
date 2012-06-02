import UniqueVersionKeys._

sbtPlugin := true

name := "sbt-unique-version"

organization := "com.eed3si9n"

version := "0.1.0-SNAPSHOT"

uniqueVersionSettings

uniqueVersion := true

ivyStatus := IvyStatus.Milestone

CrossBuilding.crossSbtVersions := Seq("0.11.3", "0.11.2" ,"0.12.0-Beta2")

description := "sbt plugin to publish snapshot jars with unique version"

licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php"))

scalacOptions := Seq("-deprecation", "-unchecked")

publishArtifact in (Compile, packageBin) := true

publishArtifact in (Test, packageBin) := false

publishArtifact in (Compile, packageDoc) := false

publishArtifact in (Compile, packageSrc) := false

lsSettings

LsKeys.tags in LsKeys.lsync := Seq("sbt", "jar")

(externalResolvers in LsKeys.lsync) := Seq(
  "sbt-plugin-releases" at "http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases")

// CrossBuilding.scriptedSettings
// ScriptedPlugin.scriptedSettings

// scriptedBufferLog := false

publishMavenStyle := false

publishTo <<= (version) { version: String =>
   val scalasbt = "http://scalasbt.artifactoryonline.com/scalasbt/"
   val (name, u) = if (version.contains("-SNAPSHOT")) ("sbt-plugin-snapshots", scalasbt+"sbt-plugin-snapshots")
                   else ("sbt-plugin-releases", scalasbt+"sbt-plugin-releases")
   Some(Resolver.url(name, url(u))(Resolver.ivyStylePatterns))
}

credentials += Credentials(Path.userHome / ".ivy2" / ".sbtcredentials")
