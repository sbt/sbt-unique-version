package sbtuniqueversion

import sbt._
import Keys._

object Plugin extends sbt.Plugin {
  object UniqueVersionKeys {
    lazy val uniqueVersion = SettingKey[Boolean]("unique-version", "Enables unique version.")
    lazy val ivyStatus = SettingKey[IvyStatus]("ivy-status", "Status of the build.")
  }

  import UniqueVersionKeys._

  trait IvyStatus
  case object IvyStatus {
    case object Integration extends IvyStatus { override def toString: String = "integration" }
    case object Milestone extends IvyStatus { override def toString: String = "milestone" }
    case object Release extends IvyStatus { override def toString: String = "release" }
  }

  private def isSnapshot(v: String): Boolean = v.endsWith("-SNAPSHOT")
  private def replaceModule(m: ModuleID): ModuleID = m.copy(revision = replaceVersion(m.revision))
  private def replaceVersion(v: String): String = """SNAPSHOT$""".r.replaceFirstIn(v, uniqueString)
  private def uniqueString: String = {
    import java.{util => ju}
    val sf = new java.text.SimpleDateFormat("yyyyMMdd-HHmmss")
    sf.setTimeZone(ju.TimeZone.getTimeZone("UTC"))
    sf.format(new ju.Date())
  }

  // override val settings = uniqueVersionSettings
  lazy val uniqueVersionSettings: Seq[sbt.Project.Setting[_]] = Seq(
    uniqueVersion := false,
    ivyStatus <<= (version) { (v) =>
      if (isSnapshot(v)) IvyStatus.Integration
      else IvyStatus.Release
    },
    moduleSettings <<= (moduleSettings, uniqueVersion, version) map { (old, uv, v) =>
      if (uv && isSnapshot(v))
        old match {
          case ic: InlineConfiguration =>
            new InlineConfiguration(replaceModule(ic.module), ic.moduleInfo, ic.dependencies, ic.ivyXML, ic.configurations,
              ic.defaultConfiguration, ic.ivyScala, ic.validate)
          case _ => old 
        }
      else old
    },
    deliverLocalConfiguration <<= (deliverLocalConfiguration, ivyStatus) map { (old, status) =>
      new DeliverConfiguration (old.deliverIvyPattern, status.toString, old.configurations, old.logging)
    },
    deliverConfiguration <<= (deliverConfiguration, ivyStatus) map { (old, status) =>
      new DeliverConfiguration (old.deliverIvyPattern, status.toString, old.configurations, old.logging)
    }
  )
  
  implicit def moduleID2RichModuleID(m: ModuleID): RichModuleID = RichModuleID(m)
  case class RichModuleID(m: ModuleID) {
    def latestIntegration: ModuleID = m.copy(revision = "latest.integration")
    def latestMilestone: ModuleID = m.copy(revision = "latest.milestone")
    def latestRelease: ModuleID = m.copy(revision = "latest.release") 
  }
}
