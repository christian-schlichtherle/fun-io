package global.namespace.fun.io.it

import org.scalatest.{BeforeAndAfter, Suite}

trait MinIoContainer extends BeforeAndAfter with S3Config {
  this: Suite =>

  before {
    println(s"Starting image ${imageName} as container ${containerName} on port ${port}:")

    val status = new ProcessBuilder(
      "docker", "run",
      "--detach",
      "--env", "MINIO_ACCESS_KEY=" + accessKeyId,
      "--env", "MINIO_SECRET_KEY=" + secretAccessKey,
      "--name", containerName,
      "--publish", port + ":9000",
      "--rm",
      imageName,
      "--compat", "server", "/data"
    ).inheritIO.start().waitFor()
    if (0 != status) {
      throw new RuntimeException("Cannot start Docker container.")
    }
    Thread sleep 1000
  }

  after {
    println(s"Stopping container ${containerName}:")
    new ProcessBuilder(
      "docker",
      "stop",
      containerName
    ).inheritIO().start().waitFor()
  }
}
