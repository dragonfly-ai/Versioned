package ai.dragonfly.versioned

import scala.reflect.ClassTag

/**
 * A class to represent Version Info
 */

object Version {
  def getStaleClassName[T <: Versioned](tag:scala.reflect.ClassTag[StaleVersionOf[T]]):String = {
    val tokens = tag.toString().split("[.]")
    val cls:String = tokens(tokens.length - 1)
    val rgx1 = "[$]u002E".r
    val s:String = rgx1.replaceAllIn(cls, ".")
    val rgx2 = "[$]colon".r
    rgx2.replaceAllIn(s, ":")
  }

  val pattern:StringContext = new StringContext("", ":", "")
  def fromString[T <: Versioned](vs: String, tag: ClassTag[T]): Version = {
    val seq:Option[Seq[String]] = try { pattern.s.unapplySeq(vs.trim()) } catch { case _:Throwable => throw UnknownVersionString(vs) }
    seq match {
      case Some( Seq( cls:String, vid:String ) ) =>
        Version(
          cls,
          java.lang.Double.parseDouble(vid),
          tag
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
  override def toString: String = s"""Version($cls, $vid, $tag)"""
}
