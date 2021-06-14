package ai.dragonfly.versioned.native

import ai.dragonfly.versioned
import versioned._

import scala.collection.mutable
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
        val version: Version = Version.fromString(className)
        throw UnknownVersion(version)
    }
  }
}

object LoadVersionedCompanionObject {
  val knownVersionedObjects: mutable.HashMap[String, VersionedObject[_]] = mutable.HashMap[String, VersionedObject[_]]()

  /**
   * Use lookupLoadableModuleClass in JS environments.
   * @param v an example of the versioned class.
   * @tparam T The type of the versioned class.
   * @return the version info for this class, taken from its reading object
   */

  def apply[T <: ai.dragonfly.versioned.Versioned](v: ai.dragonfly.versioned.Versioned): VersionedObject[T] = {
    val companionObjectName: String = s"${v.getClass.getName}$$"
    knownVersionedObjects.get(companionObjectName).orElse[VersionedObject[_]](Some(
      Reflect.lookupLoadableModuleClass(companionObjectName) match {
        case Some(lmc: LoadableModuleClass) =>
          lmc.loadModule() match {
            case staleObject: StaleObject[_] =>
              knownVersionedObjects.put(companionObjectName, staleObject)
              staleObject
            case currentObject: VersionedObject[_] =>
              knownVersionedObjects.put(companionObjectName, currentObject)
              currentObject
          }
        case _ => throw NoCompanionObject(v)
      }
    )).get.asInstanceOf[VersionedObject[T]]
  }
}