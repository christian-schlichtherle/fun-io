package global.namespace.fun.io

import java.io.{InputStream, OutputStream}

package object api {

  type Sink = Socket[OutputStream]
  type Source = Socket[InputStream]
}
