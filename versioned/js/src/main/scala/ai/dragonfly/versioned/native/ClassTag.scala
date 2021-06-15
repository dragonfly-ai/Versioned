package ai.dragonfly.versioned.native

import ai.dragonfly.versioned
import versioned._

import scala.scalajs.reflect.annotation.EnableReflectiveInstantiation
import scala.scalajs.reflect.{InstantiatableClass, LoadableModuleClass, Reflect}

/**
 * Trait for classes and companion objects
 */

@EnableReflectiveInstantiation
trait Versioned

object ClassTag {
  def apply[T <: Versioned](className:String): scala.reflect.ClassTag[T] = {
    Reflect.lookupInstantiatableClass(className) match {
      case Some(ic: InstantiatableClass) => scala.reflect.ClassTag[T](ic.runtimeClass)
      case _ =>
        val version: Version = Version.fromString(className, null)
        throw UnknownVersion(version)
    }
  }
}
