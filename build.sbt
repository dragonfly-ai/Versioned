ThisBuild / scalaVersion := "2.13.3"

lazy val root = project.in(file(".")).
  aggregate(versioned.js, versioned.jvm).
  settings(
    publishTo := Some( Resolver.file("file",  new File( "/var/www/maven" ) ) )
  )

lazy val versioned = crossProject(JSPlatform, JVMPlatform).
  settings(
    name := "versioned",
    version := "0.01",
    organization := "ai.dragonfly.code",
    resolvers += "code.dragonfly.ai" at "https://code.dragonfly.ai:4343",
    scalacOptions ++= Seq("-feature", "-deprecation"),
    mainClass in (Compile, run) := Some("ai.dragonfly.versioned.examples.Demo"),
    publishTo := Some(Resolver.file("file",  new File("/var/www/maven")))
  ).
  jvmSettings(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scala-js" %% "scalajs-stubs" % "1.0.0"
    )
  ).
  jsSettings(
    scalaJSUseMainModuleInitializer := true
  )
