# Adds YourKit agent to Docker SBT apps

[![][github-action-badge]][github-action] [![][maven-badge]][maven] [![][steward-badge]][steward]

This plugin copies the [YourKit](https://www.yourkit.com) Docker agent into the image created by [sbt-native-packager](https://sbt-native-packager.readthedocs.io/en/latest/formats/docker.html) and attaches it to the running app.

## Installation

Add the following line to your `plugins.sbt` file:

```sbt
addSbtPlugin("com.alejandrohdezma" % "sbt-yourkit" % "0.0.0")
```

## Usage

By default, the plugin enables automatically if the `yourKitEnabled` setting is set to `true`:

```scala
ThisBuild / yourKitEnabled := true
```

However, if you want to enable this on a single SBT shell session, you can use the aliases `yourKit` or `yourKitOn`, for setting it to `true` and `yourKitOff`, for setting it to `false`.

### Changing the YourKit version

The [YourKit](https://www.yourkit.com) installed version can be customized with the `yourKitVersion` setting:

```scala
yourKitVersion := "2021.3"
```

Also remember to re-build your Docker image using `sbt "Docker / publishLocal"`.

### Profiler options

The profiler startup options can be customized using the `yourKitOptions` setting:

```scala
yourKitOptions += "port" -> "10002"
```

You can get more information about available options [here](https://www.yourkit.com/docs/java/help/startup_options.jsp).

By default these options are set to:

```
port=10001,listen=all,sessionname={normalizedName.value}
```

[github-action]: https://github.com/alejandrohdezma/sbt-yourkit/actions
[github-action-badge]: https://img.shields.io/endpoint.svg?url=https%3A%2F%2Factions-badge.atrox.dev%2Falejandrohdezma%2Fsbt-yourkit%2Fbadge%3Fref%3Dmaster&style=flat
[maven]: https://search.maven.org/search?q=g:%20com.alejandrohdezma%20AND%20a:sbt-yourkit
[maven-badge]: https://maven-badges.herokuapp.com/maven-central/com.alejandrohdezma/sbt-yourkit/badge.svg?kill_cache=1
[steward]: https://scala-steward.org
[steward-badge]: https://img.shields.io/badge/Scala_Steward-helping-brightgreen.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=
