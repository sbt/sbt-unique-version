sbt-unique-version
==================

sbt-unique-version emulates Maven's `uniqueVersion` snapshots on Ivy repos.

PSA
---

Try using git SHA. Josh Suereth in [Effective sbt](http://jsuereth.com/scala/2013/06/11/effective-sbt.html) at Scala Days 2013 came up with an alternative approach. This could be the approach after sbt 0.13 and up.

```scala
val gitHeadCommitSha = settingKey[String]("current git commit SHA")

gitHeadCommitSha in ThisBuild := Process("git rev-parse HEAD").lines.head

version in ThisBuild := "1.0-" + gitHeadCommitSha.value
```

Latest
------

```scala
resolvers += Resolver.url("sbt-plugin-releases", url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

resolvers += Resolver.url("sbt-plugin-snapshots", url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-snapshots"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.eed3si9n" % "sbt-unique-version" % "latest.integration") // or "0.1.0"
```

Usage
-----

Add the following in  your `build.sbt`:

```scala
import UniqueVersionKeys._ // put this at the top

uniqueVersionSettings

uniqueVersion := true
```

Set the `version` to a snapshot one such as `"0.1.0-SNAPSHOT"`.
This would substitute the artifact revision to something like `"0.1.0-20120602-065305"`.

Optionally you can also specify the `IvyStatus` as follows:

```scala
ivyStatus <<= (version) { (v) =>
  if (v endsWith "-SNAPSHOT") IvyStatus.Integration
  else IvyStatus.Release
}
```

### How to point at it

- `"0.1.0"` or `"0.1.0-20120602-073010"` you can always use the static version number.
- `"0.1.0-+"` selects the latest 0.1.0 snapshot.
- `"latest.integration"` selects the latest revision regardless of its status.
- `"latest.milestone"` selects the latest revision with either `Milestone` or `Release` status.
- `"latest.release"` selects the latest with the `Release` status.

For example,

```scala
addSbtPlugin("com.eed3si9n" % "sbt-unique-version" % "0.1.0-+")
```

This adds the latest 0.1.0 snapshot of sbt-unique-version to your build.

### What's the point of this?

The current way sbt does the snapshots is to overwrite the jar file with the same resolution URL.
That is a cause of several issues like caching issues and security settings at the repository.

### Shortcomings

Unlike Maven's `uniqueVersion`, or just plain "x.x.x-SNAPSHOT", this approach takes over the revision.
