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
package global.namespace.fun.io.it.commons.compress

import java.io.File

import global.namespace.fun.io.bios.BIOS
import global.namespace.fun.io.commons.compress.CommonsCompress
import global.namespace.fun.io.it.ArchiveSpecSuite
import org.apache.commons.compress.archivers.tar.TarArchiveEntry

class TarGzSpec extends ArchiveSpecSuite[TarArchiveEntry] {

  override def archiveFileFactory: ArchiveFileFactory[TarArchiveEntry] = {
    f: File => CommonsCompress.tar(BIOS.file(f).map(CommonsCompress.gzip))
  }
}
