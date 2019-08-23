package global.namespace.fun.io.it

import com.typesafe.config.ConfigFactory

trait S3Config {

  private lazy val s3Config = ConfigFactory.load getConfig "s3"

  lazy val accessKeyId: String = s3Config getString "access-key-id"
  lazy val endpoint: String = s3Config getString "endpoint"
  lazy val region: String = s3Config getString "region"
  lazy val secretAccessKey: String = s3Config getString "secret-access-key"
}
