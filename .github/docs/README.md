# @DESCRIPTION@

This plugin copies the [YourKit](https://www.yourkit.com) Docker agent into the image created by [sbt-native-packager](https://sbt-native-packager.readthedocs.io/en/latest/formats/docker.html) and attaches it to the running app.

## Installation

Add the following line to your `plugins.sbt` file:

```sbt
addSbtPlugin("com.alejandrohdezma" % "sbt-yourkit" % "@VERSION@")
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
