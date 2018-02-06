
organization := "net.xrrocha"
name := "spellbound-ngram-scala"
version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

resolvers += Resolver.mavenLocal

val sparkVersion = "2.2.1"
val amazonAwsVersion = "1.11.271"
val stringSimilarityVersion = "1.0.1"
val luceneSpellCheckerVersion = "3.6.2"
val configVersion = "1.3.1"
val scalaLoggingVersion = "3.7.2"
val scalaJava8CompatVersion = "0.8.0"
val scalatestVersion = "3.0.4"
val ammoniteReplVersion = "1.0.3"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "com.amazonaws" % "aws-java-sdk-s3" % amazonAwsVersion,
  "info.debatty" % "java-string-similarity" % stringSimilarityVersion,
  "org.apache.lucene" % "lucene-spellchecker" % luceneSpellCheckerVersion,
  "com.typesafe" % "config" % configVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
  "org.scala-lang.modules" %% "scala-java8-compat" % scalaJava8CompatVersion,
  "org.scalatest" %% "scalatest" % scalatestVersion % Test,
  "com.lihaoyi" % "ammonite" % ammoniteReplVersion % Test cross CrossVersion.full
)

fork in Test := true
connectInput := true
outputStrategy := Some(StdoutOutput)
sourceGenerators in Test += Def.task {
  val file = (sourceManaged in Test).value / "amm.scala"
  IO.write(file, """object amm extends App { ammonite.Main().run() }""")
  Seq(file)
}.taskValue

scalacOptions ++= Seq(
  "-encoding", "UTF-8", // source files are in UTF-8
  "-deprecation", // warn about use of deprecated APIs
  "-unchecked", // warn about unchecked type parameters
  "-feature", // warn about misused language features
  "-language:higherKinds", // allow higher kinded types without `import scala.language.higherKinds`
  "language.postfixOps", // allow postfix operators
  "-language:implicitConversions", // allow definition of implicit functions called views
  "-Xlint", // enable handy linter warnings
  "-Xfatal-warnings" // turn compiler warnings into errors
)
