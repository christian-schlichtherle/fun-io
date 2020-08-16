/*
 * Copyright © 2017 - 2019 Schlichtherle IT Services
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
package global.namespace.fun.io.aws.sdk1

import com.amazonaws.services.s3.AmazonS3
import global.namespace.fun.io.aws.sdk1.AWS.s3
import org.scalatest.matchers.should.Matchers._
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar._

class AWSSpec extends AnyWordSpec {

  private val client = mock[AmazonS3]
  private val bucket = "???"

  "AWS.s3" should {
    "reject invalid prefixes" in {
      val tests = Table(
        "prefix",
        "/",
        "/foo",
        "/foo/",
        "/foo/bar",
        "/foo/bar/",
        ".",
        "./",
        "./foo",
        "./foo/",
        "./foo/bar",
        "./foo/bar/",
        "..",
        "../",
        "../foo",
        "../foo/",
        "../foo/bar",
        "../foo/bar/",
        "foo",
        "foo/.",
        "foo/./",
        "foo/./bar",
        "foo/./bar/",
        "foo/..",
        "foo/../",
        "foo/../bar",
        "foo/../bar/"
      )
      forAll(tests)(prefix => intercept[IllegalArgumentException](s3(client, bucket, prefix)))
    }

    "accept valid prefixes" in {
      val tests = Table(
        "prefix",
        "",
        "foo/",
        "foo/bar/"
      )
      forAll(tests)(prefix => s3(client, bucket, prefix) should not be null)
    }
  }
}
