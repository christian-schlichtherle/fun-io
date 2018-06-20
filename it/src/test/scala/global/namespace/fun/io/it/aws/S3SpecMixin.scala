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
package global.namespace.fun.io.it.aws

import java.util.UUID.randomUUID

import global.namespace.fun.io.api.ArchiveStore
import global.namespace.fun.io.aws.AWS.s3
import global.namespace.fun.io.it.ArchiveSpecContext
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.{ObjectIdentifier, S3Object}

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

trait S3SpecMixin { this: ArchiveSpecContext[S3Object] =>

  override def disabled: Boolean = sys.env contains "TRAVIS"

  override def withTempArchiveStore: (ArchiveStore[S3Object] => Any) => Unit = {
    test: (ArchiveStore[S3Object] => Any) => {
      var t: Throwable = null
      val client = S3Client.create
      val bucket = "test-" + randomUUID
      client createBucket (b => b bucket bucket)
      try {
        test(s3(client, bucket, "test/"))
      } catch {
        case NonFatal(t1) => t = t1; throw t1
      } finally {
        try {
          val objects = client
            .listObjectsV2Paginator(b => b.bucket(bucket))
            .contents.asScala.map(o => ObjectIdentifier.builder.key(o.key).build).toSeq
          client deleteObjects (b => b bucket bucket delete (b => b objects (objects: _*)))
        } catch {
          case NonFatal(t1) => if (null != t) t.addSuppressed(t1) else throw t1
        } finally {
          try {
            client deleteBucket (b => b bucket bucket)
          } catch {
            case NonFatal(t1) => if (null != t) t.addSuppressed(t1) else throw t1
          }
        }
      }
    }
  }

  override def archiveFileFactory: ArchiveFileFactory[S3Object] = ???
}
