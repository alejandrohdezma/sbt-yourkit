ThisBuild / scalaVersion                  := "2.13.6"
ThisBuild / organization                  := "com.alejandrohdezma"
ThisBuild / publish / skip                := true
ThisBuild / pluginCrossBuild / sbtVersion := "1.2.8"

addCommandAlias("ci-test", "fix --check; mdoc; scripted")
addCommandAlias("ci-docs", "github; mdoc; headerCreateAll")
addCommandAlias("ci-publish", "github; ci-release")

lazy val documentation = project
  .enablePlugins(MdocPlugin)
  .settings(mdocOut := file("."))

lazy val `sbt-yourkit` = project
  .enablePlugins(SbtPlugin)
  .settings(publish / skip := false)
  .settings(scriptedLaunchOpts += s"-Dplugin.version=${version.value}")
  .settings(scriptedBufferLog := false)
  .settings(addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.6"))
