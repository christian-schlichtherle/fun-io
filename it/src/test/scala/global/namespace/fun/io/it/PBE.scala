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
package global.namespace.fun.io.it

import java.security.SecureRandom

import global.namespace.fun.io.api.Transformation
import global.namespace.fun.io.bios.BIOS
import global.namespace.fun.io.scala.api._
import javax.crypto.Cipher.{DECRYPT_MODE, ENCRYPT_MODE, getInstance}
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.{PBEKeySpec, PBEParameterSpec}

object PBE {

  private val algorithm = "PBEWithMD5AndDES"

  private val secretKeyFactory = SecretKeyFactory getInstance algorithm

  private val pbeKeySpec = new PBEKeySpec("secret".toCharArray)

  private val pbeParameterSpec = {
    val salt = new Array[Byte](8)
    new SecureRandom() nextBytes salt
    new PBEParameterSpec(salt, 2017)
  }

  // MUST be `def` or `pbe - pbe` may get optimized to `identity`!
  def pbe: Transformation = {
    BIOS cipher { forOutput: java.lang.Boolean =>
      val secretKey = secretKeyFactory generateSecret pbeKeySpec
      val cipher = getInstance(algorithm)
      cipher.init(if (forOutput) ENCRYPT_MODE else DECRYPT_MODE, secretKey, pbeParameterSpec)
      cipher
    }
  }
}
