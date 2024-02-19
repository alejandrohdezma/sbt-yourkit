name := "my-project"

enablePlugins(DockerPlugin, JavaAppPackaging)

TaskKey[Unit]("checkBashScripts") := {
    val expected = """addJava "-agentpath:/opt/docker/yourkit/yourkit.so=port=10001,listen=all,sessionname=my-project"""

    makeBashScripts.value.foreach { case (file, _) =>
        val bashScript = IO.read(file)

        assert(bashScript.contains(expected), "Bash script doesn't contain agent `addJava` call.")
    }
}