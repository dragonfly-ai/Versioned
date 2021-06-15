package ai.dragonfly.versioned.examples

import ai.dragonfly.versioned._

import scala.language.implicitConversions

/** Foo exemplifies a current version, 0.3, of a versioned case class.
 *  0.3 illustrates how to handle path refactor/change from "com.whatever.Foo" to
 *  "ai.dragonfly.versionedjson.examples.test.Foo".
 *  It also removes a field: [[s: String]] from the previous version: Foo 0.2.
 *  Where other versioned serialization schemes forbid field removal for
 *  backwards compatibility, this library adopts forward-only compatibility.
 *
 *  To satisfy concerns like forensics, provenance, etc,
 *  users of this versioning scheme should backup old versions of versioned
 *  types with archival techniques.
 *
 *  If design goal changes require the reintroduction of a deleted field,
 *  it can be restored in the upgrade method, spun out into a separate type,
 *  and/or populated from archives of old versions of serialized classes.
 *
 *  @constructor create a Foo instance from various primitive types.
 *  @param i an Int primitive type
 *  @param l a Long primitive type
 *  @param f a Float primitive type
 *  @param d a Double primitive type
 *  @param b a Boolean primitive type
 */

case class Foo( i: Int, l: Long, f: Float, d: Double, b: Boolean ) extends VersionedClass[Foo] {
  override val vid: Double = 0.3
}

/**
 * older versions of Foo:
 * Instead of updating old versions of foo in place, we rename them and create a new class definition for the current version.
 * The old version starts with its original fully qualified class name, then appends its version number after a : which serves as a separator.
 * because class paths have . characters in them, we place the entire class name between quasiquotes: ``.
  */


/** `com.whatever.Foo:0.2` exemplifies the second version, 0.2, of the Foo case class.
 * Foo 0.2 added the field: [[b: Boolean]] to the Foo type and demonstrates
 * how to add fields to versioned types.
 * old versions of Foo (and other versioned classes) can not write any json.
 * Instead, they contain upgrade methods that point through the version chain
 * until they reach the current version.
 *  @constructor create a Foo instance from various primitive types.
 *  @param s a String primitive type
 *  @param i an Int primitive type
 *  @param l a Long primitive type
 *  @param f a Float primitive type
 *  @param d a Double primitive type
 *  @param b a Boolean primitive type
 */

case class `com.whatever.Foo:0.2`(s: String, i: Int, l: Long, f: Float, d: Double, b: Boolean) extends StaleVersionOf[Foo] {
  override def upgrade: Some[Foo] = Some( Foo(i, l, f, d, b) )
}

/** Foo\$0_1 exemplifies the first version, 0.1, of the Foo case class.
 * old versions of Foo (and other versioned classes) can not write any json.
 * Instead, they contain upgrade methods that point through the version chain
 * until they reach the current version.
 *  @constructor create a Foo instance from various primitive types.
 *  @param s a String primitive type
 *  @param i an Int primitive type
 *  @param l a Long primitive type
 *  @param f a Float primitive type
 *  @param d a Double primitive type
 */

case class `com.whatever.Foo:0.1` (s: String, i: Int, l: Long, f: Float, d: Double) extends StaleVersionOf[`com.whatever.Foo:0.2`] {
  override def upgrade: Some[`com.whatever.Foo:0.2`] = Some( `com.whatever.Foo:0.2`( s, i, l, f, d, false ) )
}
