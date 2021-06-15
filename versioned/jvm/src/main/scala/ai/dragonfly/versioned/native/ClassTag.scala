package ai.dragonfly.versioned.native

/**
 * Trait for classes and companion objects
 */

trait Versioned

object ClassTag {
  def apply[T](className: String): scala.reflect.ClassTag[T] = scala.reflect.ClassTag[T](Class.forName(className))
}
