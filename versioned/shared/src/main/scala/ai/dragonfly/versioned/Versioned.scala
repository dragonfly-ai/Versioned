package ai.dragonfly.versioned

import ai.dragonfly.versioned.native

import scala.language.postfixOps
import scala.reflect.ClassTag
import scala.collection.mutable
import scala.annotation.tailrec

/**
 * Trait for classes and companion objects
 */

trait Versioned extends native.Versioned {
  val version: Version
}

/**
  * traits for current version classes
  */

trait VersionedClass[T <: Versioned] extends Versioned {
  val vid:Double
  override lazy val version:Version = Version(
    this.getClass.getName,
    vid,
    native.ClassTag[T](this.getClass.getName)
  )
}

/**
 * Past Versions
 * @tparam T type parameter of next most current version.
 */

trait StaleClass[T <: Versioned] extends Versioned {
  def upgrade: Option[T]
  override lazy val version:Version = Version.fromString[T](this.getClass.getName)
}

/**
  * traits for companion objects
  */

sealed trait VersionedObject[T <: Versioned] extends Versioned {
  val vid:Double
  override lazy val version:Version = Version(
    this.getClass.getName,
    vid,
    native.ClassTag[T](this.getClass.getName)
  )
}

/**
  Only intended for past versions of objects.
 */

trait StaleObject[T <: Versioned] extends VersionedObject[T] {
  override lazy val version:Version = Version.fromString[T](this.getClass.getName)
}

/**
 * A class to represent Version Info
 */

object Version {
  val pattern:StringContext = new StringContext("", ":", "") //s"""$cls:$vid"""
  def fromString[T <: Versioned](vs: String): Version = {
    val seq:Option[Seq[String]] = try { pattern.s.unapplySeq(vs.trim()) } catch { case _:Throwable => throw UnknownVersionString(vs) }
    seq match {
      case Some( Seq( cls:String, vid:String ) ) =>
        Version(
          cls,
          java.lang.Double.parseDouble(vid),
          native.ClassTag[T](vs)
        )
      case _ => throw UnknownVersionString(vs)
    }
  }
}

/**
 * stores version information
 *
 * @param cls fully qualified class name of a versioned class.
 * @param vid version id of a versioned class.
 * @param tag ClassTag of a versioned class.
 */

case class Version(cls: String, vid: Double, tag: ClassTag[_ <: Versioned]) {
  override def toString: String = Version.pattern.s(cls, vid)
}

/**
 * Manages an ephemeral dictionary that compresses version information for nested versioned objects and collections.
 */

class VersionIndex {

  val hist: mutable.HashMap[Version, Int] = mutable.HashMap[Version, Int]()

  def apply(version: Version): Int = {

    hist.getOrElse(
      version,
      {
          val index = this.size
          hist.put(version, index)
          index
      }
    )
  }

  def apply(value: Any): Int = {
    value match {
      case versioned: Versioned => apply(versioned.version)
      case _ => throw TypeNotVersioned(value)
    }
  }

  def size: Int = hist.size

  override def toString:String = {
    val sb = new StringBuilder("VersionIndex:")
    for ((version, index) <- hist) sb.append(s"$index -> $version")
    sb.toString()
  }
}

/**
  Versioned registry
*/

object Versioned {

  @tailrec
  def upgradeToCurrentVersion[T <: Versioned](o: StaleClass[_])(implicit tag: ClassTag[T]): T = {
    val ou = o.upgrade
    ou match {
      case Some(nv: T) => nv
      case Some(ov: StaleClass[_]) => upgradeToCurrentVersion[T](ov)
      case _ => throw UpgradeFailure(o, ou.get)(tag)
    }
  }

}

case class NoCompanionObject(v: Versioned) extends Exception(s"Can't find a Companion Object for Versioned Class: $v")
case class UpgradeFailure(oldVersion:Versioned, upgrade: Any)(implicit tag: ClassTag[_]) extends Exception(s"$tag upgrade failure.  Lost the upgrade path after upgrading $oldVersion to $upgrade.")
case class UnknownVersion(version: Version) extends Exception(s"Unknown Versioned Class: $version")
case class UnknownVersionString(vs: String) extends Exception(s"Unknown Versioned String: $vs")
case class TypeNotVersioned(o: Any) extends Exception(s"Type of: $o, ${o.getClass} is not versioned.")