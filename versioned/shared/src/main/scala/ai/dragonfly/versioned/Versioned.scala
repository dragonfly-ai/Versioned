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
  val tag:scala.reflect.ClassTag[T] = scala.reflect.ClassTag[T](this.getClass)
  override lazy val version:Version = Version(
    this.getClass.getName,
    vid,
    tag
  )
}

/**
 * Past Versions
 * @tparam T type parameter of next most current version.
 */

trait StaleVersionOf[T <: Versioned] extends Versioned {
  def upgrade: Option[T]
  val tag:scala.reflect.ClassTag[StaleVersionOf[T]] = scala.reflect.ClassTag[StaleVersionOf[T]](this.getClass)
  println(s"tag = $tag")
  override lazy val version:Version = Version.fromString[StaleVersionOf[T]]({
    val t = Version.getStaleClassName[T](tag)
    println(t)
    t
  }, tag)
  println(version)
}


object Versioned {

  @tailrec
  def upgradeToCurrentVersion[T <: Versioned](o: StaleVersionOf[_])(implicit tag: ClassTag[T]): T = {
    val ou = o.upgrade
    ou match {
      case Some(nv: T) => nv
      case Some(ov: StaleVersionOf[_]) => upgradeToCurrentVersion[T](ov)
      case _ => throw UpgradeFailure(o, ou.get)(tag)
    }
  }

}

case class NoCompanionObject(v: Versioned) extends Exception(s"Can't find a Companion Object for Versioned Class: $v")
case class UpgradeFailure(oldVersion:Versioned, upgrade: Any)(implicit tag: ClassTag[_]) extends Exception(s"$tag upgrade failure.  Lost the upgrade path after upgrading $oldVersion to $upgrade.")
case class UnknownVersion(version: Version) extends Exception(s"Unknown Versioned Class: $version")
case class UnknownVersionString(vs: String) extends Exception(s"Unknown Versioned String: $vs")
case class TypeNotVersioned(o: Any) extends Exception(s"Type of: $o, ${o.getClass} is not versioned.")