name := "my-project"

enablePlugins(DockerPlugin, JavaAppPackaging)

yourKitOptions += "port" -> "10002"
yourKitOptions += "listen" -> "localhost"

TaskKey[Unit]("checkBashScripts") := {
    val expected = """addJava "-agentpath:/opt/docker/yourkit/yourkit.so=port=10002,listen=localhost,sessionname=my-project""""

    makeBashScripts.value.foreach { case (file, _) =>
        val bashScript = IO.read(file)

        assert(bashScript.contains(expected), s"Bash script doesn't contain agent `addJava` call. Actual script: $bashScript")
    }
}