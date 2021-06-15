# Versioned
This experiment aims to provide Scala and Scala.js with a common library for incorporating version information into classes.&nbsp;&nbsp;In particular, this library provides traits and utilities for encoding version information and upgrading old versions of classes into current ones.&nbsp;&nbsp;The main intended usecase supports versioned serialization which can dramatically reduce boiler plate.

To use this library with SBT:
<pre>
resolvers += "dragonfly.ai" at "https://code.dragonfly.ai/"
libraryDependencies += "ai.dragonfly.code" %% "versioned" % "0.01"
</pre><br />
