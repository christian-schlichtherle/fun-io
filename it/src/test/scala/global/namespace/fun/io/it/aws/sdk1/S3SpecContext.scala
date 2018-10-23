/*
 * Copyright © 2017 Schlichtherle IT Services
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package global.namespace.fun.io.it.aws.sdk1

import java.util.UUID.randomUUID

import com.amazonaws.services.s3.model.{ListObjectsV2Request, ListObjectsV2Result}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import global.namespace.fun.io.api.ArchiveStore
import global.namespace.fun.io.aws.sdk1.AWS.s3
import global.namespace.fun.io.it.ArchiveSpecContext
import org.scalatest._

import scala.util.control.NonFatal

trait S3SpecContext extends TestSuiteMixin {
  this: ArchiveSpecContext with TestSuite =>

  lazy val client: AmazonS3 = AmazonS3ClientBuilder.defaultClient

  abstract override protected def withFixture(test: NoArgTest): Outcome = {
    try {
      client
    } catch {
      case NonFatal(e) => return Canceled("Cannot create an AWS S3 client", e)
    }
    super.withFixture(test)
  }

  override def withTempArchiveStore(test: ArchiveStore => Any): Unit = {
    var t: Throwable = null
    val bucket = "test-" + randomUUID
    client createBucket bucket
    try {
      test(s3(client, bucket, "test/"))
    } catch {
      case NonFatal(t1) => t = t1; throw t1
    } finally {
      try {
        val request = new ListObjectsV2Request withBucketName bucket
        var result: ListObjectsV2Result = null
        do {
          result = client listObjectsV2 request
          result.getObjectSummaries.forEach(summary => client.deleteObject(summary.getBucketName, summary.getKey))
          request setContinuationToken result.getContinuationToken
        } while (result.isTruncated)
      } catch {
        case NonFatal(t1) => if (null != t) t.addSuppressed(t1) else throw t1
      } finally {
        try {
          client deleteBucket bucket
        } catch {
          case NonFatal(t1) => if (null != t) t.addSuppressed(t1) else throw t1
        }
      }
    }
  }

  override def archiveStoreFactory: ArchiveStoreFactory = ???
}
