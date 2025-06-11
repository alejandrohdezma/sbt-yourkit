/*
 * Copyright 2021-2024 Alejandro Hernández <https://github.com/alejandrohdezma>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alejandrohdezma.sbt.yourkit

import scala.language.postfixOps

import sbt.Keys._
import sbt._
import sbt.io.IO

import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.archetypes.scripts.BashStartScriptPlugin
import com.typesafe.sbt.packager.archetypes.scripts.BashStartScriptPlugin.autoImport.bashScriptExtraDefines
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._
import com.typesafe.sbt.packager.docker._
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._

/** This plugins automatically adds the Docker YourKit agent to the container created by `DockerPlugin` and attaches it
  * to the running Java app.
  *
  * ==Usage==
  *
  * By default, the plugin enables automatically if the `yourKitEnabled` setting is set to `true`:
  *
  * ```scala
  * ThisBuild / yourKitEnabled := true
  * ```
  *
  * However, if you want to enable this on a single SBT shell session, you can use the aliases `yourKit` or `yourKitOn`,
  * for setting it to `true` and `yourKitOff`, for setting it to `false`.
  *
  * ==Changing the YourKit version==
  *
  * The [YourKit](https://www.yourkit.com) installed version can be customized with the `yourKitVersion` setting:
  *
  * ```scala
  * yourKitVersion := "2025.3"
  * ```
  *
  * Also remember to re-build your Docker image using `sbt "Docker / publishLocal"`.
  *
  * ==Profiler options==
  *
  * The profiler startup options can be customized using the `yourKitOptions` setting:
  *
  * ```scala
  * yourKitOptions += "port" -> "10002"
  * ```
  *
  * You can get more information about available options
  * [here](https://www.yourkit.com/docs/java/help/startup_options.jsp).
  *
  * By default these options are set to:
  *
  * ```
  * port=10001,listen=all,sessionname={normalizedName.value}
  * ```
  *
  * @see
  *   https://www.yourkit.com/docs/java/help/docker.jsp
  */
object YourKitPlugin extends AutoPlugin {

  object autoImport {

    val yourKitEnabled = settingKey[Boolean] {
      "Set this setting to `true` to enable this plugin's features."
    }

    val yourKitVersion = settingKey[String] {
      "The version of the YourKit Docker agent to download and copy to container."
    }.withRank(KeyRanks.Invisible)

    val yourKitOptions = settingKey[Map[String, String]] {
      """The startup options allow to customize some aspects of profiling. These options are added when running the app.
        |
        |You can get more information about available options here: https://www.yourkit.com/docs/java/help/startup_options.jsp
        |
        |By default these options are set to:
        |
        |`port=10001,listen=all,sessionname={normalizedName.value}`
        |""".stripMargin
    }.withRank(KeyRanks.Invisible)

  }

  import autoImport._

  override def requires = BashStartScriptPlugin && JavaAppPackaging && DockerPlugin

  override def trigger: PluginTrigger = allRequirements

  override def buildSettings: Seq[Setting[_]] = aliases ++ List(yourKitEnabled := false)

  override lazy val projectSettings = Seq(
    Universal / mappings   ++= onYourKit(Def.setting(agent.value -> "/yourkit/yourkit.so")).value,
    bashScriptExtraDefines ++= onYourKit(Def.setting(s"""addJava "${agentVMOption.value}"""")).value,
    yourKitVersion          := "2024.9",
    yourKitOptions := Map(
      "port"        -> "10001",
      "listen"      -> "all",
      "sessionname" -> normalizedName.value
    ),
    dockerExposedPorts ++= onYourKit(Def.setting(yourKitOptions.value.get("port").map(_.toInt).getOrElse(10001))).value
  )

  private def onYourKit[A](f: Def.Initialize[A]) = Def.settingDyn {
    if (yourKitEnabled.value) Def.setting(List(f.value)) else Def.setting(Nil)
  }

  private val agentVMOption = Def.setting {
    "-agentpath:/opt/docker/yourkit/yourkit.so" +
      yourKitOptions.value.toList.map {
        case (key, "")    => key
        case (key, value) => s"$key=$value"
      }.mkString("=", ",", "")
  }

  private lazy val aliases: Seq[Setting[State => State]] = Seq(
    "yourKit"    -> ";set ThisBuild / yourKitEnabled := true",
    "yourKitOn"  -> ";set ThisBuild / yourKitEnabled := true",
    "yourKitOff" -> ";set ThisBuild / yourKitEnabled := false"
  ).flatMap(addCommandAlias _ tupled)

  private def agent = Def.setting {
    sLog.value.info(s"Downloading YourKit ${yourKitVersion.value}...")

    val temp = IO.createTemporaryDirectory

    val yourKitUrl =
      url(s"https://www.yourkit.com/download/docker/YourKit-JavaProfiler-${yourKitVersion.value}-docker.zip")
    IO.unzipURL(yourKitUrl, temp)

    temp / s"YourKit-JavaProfiler-${yourKitVersion.value}" / "bin" / "linux-x86-64" / "libyjpagent.so"
  }

}
