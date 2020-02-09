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

import global.namespace.fun.io.api.Filter
import global.namespace.fun.io.bios.BIOS.cipher
import javax.crypto.Cipher.{DECRYPT_MODE, ENCRYPT_MODE}
import javax.crypto.spec.{PBEKeySpec, PBEParameterSpec}
import javax.crypto.{Cipher, SecretKeyFactory}

object PBE {

  private[this] val Algorithm = "PBEWithMD5AndDES"

  private[this] val Skf = SecretKeyFactory getInstance Algorithm

  private[this] val PbeKeySpec = new PBEKeySpec("secret".toCharArray)

  private[this] val PbeParameterSpec = {
    val salt = new Array[Byte](8)
    new SecureRandom nextBytes salt
    new PBEParameterSpec(salt, 2017)
  }

  // MUST be a `def` or the expression `pbe - pbe` may get optimized to the `identity` filter!
  def pbe: Filter = {
    cipher { outputMode: java.lang.Boolean =>
      val secretKey = Skf generateSecret PbeKeySpec
      val cipher = Cipher getInstance Algorithm
      cipher.init(if (outputMode) ENCRYPT_MODE else DECRYPT_MODE, secretKey, PbeParameterSpec)
      cipher
    }
  }
}
