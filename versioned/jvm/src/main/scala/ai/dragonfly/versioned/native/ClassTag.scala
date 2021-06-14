package ai.dragonfly.versioned.native

import ai.dragonfly.versioned._

import scala.collection.mutable
import scala.reflect.runtime.universe

/**
 * Trait for classes and companion objects
 */

trait Versioned

object ClassTag {
  //this.getClass.getName.split("\\$")(0))
  def apply[T](className: String): scala.reflect.ClassTag[T] = scala.reflect.ClassTag[T](Class.forName(className))
}

object LoadVersionedCompanionObject {
  val knownVersionedObjects: mutable.HashMap[String, VersionedObject[_]] = mutable.HashMap[String, VersionedObject[_]]()

  /**
   * Use Reflection on the JVM.
   * @param v an example of the versioned class.
   * @tparam T The type of the versioned class.
   * @return the version info for this class, taken from its reading object
   */
  def apply[T <: ai.dragonfly.versioned.Versioned](v: Versioned): VersionedObject[T] = {
    val companionObjectName: String = s"${v.getClass.getName}$$"
    knownVersionedObjects.get(companionObjectName).orElse[VersionedObject[_]](Some({
      val runtimeMirror = universe.runtimeMirror(v.getClass.getClassLoader)
      val module = runtimeMirror.staticModule(s"${v.getClass.getName}")
      val obj = runtimeMirror.reflectModule(module)

      obj.instance match {
        case staleObject: StaleObject[_] =>
          knownVersionedObjects.put(companionObjectName, staleObject)
          staleObject
        case currentObject: VersionedObject[_] =>
          knownVersionedObjects.put(companionObjectName, currentObject)
          currentObject
      }
    })).get.asInstanceOf[VersionedObject[T]]
  }
}
