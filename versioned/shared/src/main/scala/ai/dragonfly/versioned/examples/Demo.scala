package ai.dragonfly.versioned.examples

import ai.dragonfly.versioned.Versioned

object Demo extends App {

  val f1: `com.whatever.Foo:0.1` = `com.whatever.Foo:0.1`("Testing 1", 1, 2L, 3f, 4.0)
  println(f1.version)
  println(s"""val f1: `com.whatever.Foo:0.1` = $f1""")
  println(s"f1.upgrade=> ${f1.upgrade}")
  println(s"Versioned.upgradeToCurrentVersion[Foo](f1)} => ${Versioned.upgradeToCurrentVersion[Foo](f1)}")
  val f2: `com.whatever.Foo:0.2` = `com.whatever.Foo:0.2`("Testing 2", 1, 2L, 3f, 4.0, true)
  println(f2.version)
  println(s"""val f2: `com.whatever.Foo:0.2` = $f2""")
  println(s"f2.upgrade => ${f2.upgrade}")
  println(s"Versioned.upgradeToCurrentVersion[Foo](f2)} => ${Versioned.upgradeToCurrentVersion[Foo](f2)}")
  val f: Foo = Foo(42, 42L, 42f, 42.0, true)
  println(f)
  println(f.version)
}
