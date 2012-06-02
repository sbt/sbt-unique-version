sbt-unique-version
==================

sbt-unique-version emulates Maven's `uniqueVersion` snapshots on Ivy repos.

Latest
------

```scala
resolvers += Resolver.url("sbt-plugin-releases", url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

resolvers += Resolver.url("sbt-plugin-snapshots", url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-snapshots"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.eed3si9n" % "sbt-unique-version" % "latest.integration")
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

By setting `"latest.integration"` as the version, sbt selects the latest revision. Setting `"latest.milestone"` selects the latest revision with either `"milestone"` or `"release"` status. Setting `"latest.release"` selects the latest with the `"release"` status.

### Shortcomings

Unlike Maven's `uniqueVersion`, or just plain "x.x.x-SNAPSHOT", this approach only takes over the revision.
In addition, it allows only single track of snapshots due to the limited way Ivy allows dynamic revision.

### Then what's the point?

The current way sbt does the snapshots is to overwrite the jar file with the same resolution URL.
That is a cause of several issues like caching issues and security settings at the repository.
