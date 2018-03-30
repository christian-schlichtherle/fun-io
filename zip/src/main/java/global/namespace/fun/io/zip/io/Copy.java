/*
 * Copyright (C) 2005-2013 Schlichtherle IT Services.
 * Copyright (C) 2013 Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.io;

import global.namespace.fun.io.api.Sink;
import global.namespace.fun.io.api.Source;
import global.namespace.fun.io.api.Store;

import javax.annotation.WillNotClose;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Objects.requireNonNull;

/**
 * Provides a high-performance copy function.
 *
 * @author Christian Schlichtherle (copied and edited from the {@code Streams} class in TrueCommons I/O 2.3.2)
 */
@Immutable
public final class Copy {

    /**
     * The size of the FIFO used for exchanging I/O buffers between a reader
     * thread and a writer thread.
     * A minimum of two elements is required.
     * The actual number is optimized to compensate for oscillating I/O
     * bandwidths like e.g. with network shares.
     */
    private static final int FIFO_SIZE = 4;

    private static final ExecutorService executor = Executors.newCachedThreadPool(new ReaderThreadFactory());

    /**
     * Copies the data from the given source to the given sink.
     * <p>
     * This is a high performance implementation which uses a pooled background
     * thread to fill a FIFO of pooled buffers which is concurrently flushed by
     * the current thread.
     * It performs best when used with <em>unbuffered</em> streams.
     *
     * @param source the source for reading the data from.
     * @param sink the sink for writing the data to.
     */
    public static void copy(final Source source, final Sink sink) throws Exception {

        class SourceTask implements InputTask<Void> {
            @Override
            public Void execute(final InputStream in) throws Exception {

                class SinkTask implements OutputTask<Void> {
                    @Override
                    public Void execute(final OutputStream out) throws IOException {
                        cat(in, out);
                        return null;
                    }
                }

                return Sinks.execute(new SinkTask()).on(sink);
            }
        }

        Sources.execute(new SourceTask()).on(source);
    }

    /**
     * Copies the data from the given input stream to the given output stream
     * <em>without</em> closing them.
     * This method calls {@link OutputStream#flush()} unless an
     * {@link IOException} occurs when writing to the output stream.
     * This hold true even if an {@link IOException} occurs when reading from
     * the input stream.
     * <p>
     * This is a high performance implementation which uses a pooled background
     * thread to fill a FIFO of pooled buffers which is concurrently flushed by
     * the current thread.
     * It performs best when used with <em>unbuffered</em> streams.
     * <p>
     * The name of this method is inspired by the Unix command line utility
     * {@code cat} because you could use it to con<i>cat</i>enate the contents
     * of multiple streams.
     *
     * @param in the input stream.
     * @param out the output stream.
     */
    private static void cat(final @WillNotClose InputStream in,
                            final @WillNotClose OutputStream out)
    throws IOException {
        requireNonNull(in);
        requireNonNull(out);

        // We will use a FIFO to exchange byte buffers between a pooled reader
        // thread and the current writer thread.
        // The pooled reader thread will fill the buffers with data from the
        // input and the current thread will write the filled buffers to the
        // output.
        // The FIFO is simply implemented as a cached array or byte buffers
        // with an offset and a size which is used like a ring buffer.

        final Lock lock = new ReentrantLock();
        final Condition signal = lock.newCondition();
        final Buffer[] buffers = Buffer.allocate();

        /*
         * The task that cycles through the buffers in order to fill them
         * with input.
         */
        final class ReaderTask implements Runnable {

            /** The index of the next buffer to be written. */
            int off;

            /** The number of buffers filled with data to be written. */
            int size;

            /** The Throwable that happened in this task, if any. */
            volatile Throwable exception;

            @Override
            public void run() {
                final int buffersLength = buffers.length;

                // The writer executor interrupts this executor to signal
                // that it cannot handle more input because there has been
                // an IOException during writing.
                // We stop processing in this case.
                int read;
                do {
                    // Wait until a buffer is available.
                    final Buffer buffer;
                    lock.lock();
                    try {
                        while (size >= buffersLength) {
                            try {
                                signal.await();
                            } catch (InterruptedException cancel) {
                                return;
                            }
                        }
                        buffer = buffers[(off + size) % buffersLength];
                    } finally {
                        lock.unlock();
                    }

                    // Fill buffer until end of file or buffer.
                    // This should normally complete in one loop cycle, but
                    // we do not depend on this as it would be a violation
                    // of InputStream's contract.
                    try {
                        final byte[] buf = buffer.buf;
                        read = in.read(buf, 0, buf.length);
                    } catch (final Throwable ex) {
                        exception = ex;
                        read = -1;
                    }
                    buffer.read = read;

                    // Advance head and signal writer.
                    lock.lock();
                    try {
                        size++;
                        signal.signal(); // only the writer could be waiting now!
                    } finally {
                        lock.unlock();
                    }
                } while (0 <= read);
            }
        }

        boolean interrupted = false;
        try {
            final ReaderTask reader = new ReaderTask();
            final Future<?> result = executor.submit(reader);

            // Cache some data for better performance.
            final int buffersLength = buffers.length;

            int write;
            while (true) {
                // Wait until a buffer is available.
                final int off;
                final Buffer buffer;
                lock.lock();
                try {
                    while (0 >= reader.size) {
                        try {
                            signal.await();
                        } catch (InterruptedException interrupt) {
                            interrupted = true;
                        }
                    }
                    off = reader.off;
                    buffer = buffers[off];
                } finally {
                    lock.unlock();
                }

                // Stop on last buffer.
                write = buffer.read;
                if (0 > write)
                    break; // reader has terminated because of EOF or exception

                // Process buffer.
                try {
                    out.write(buffer.buf, 0, write);
                } catch (final IOException ex) {
                    cancel(result);
                    throw ex;
                }

                // Advance tail and signal reader.
                lock.lock();
                try {
                    reader.off = (off + 1) % buffersLength;
                    reader.size--;
                    signal.signal(); // only the reader could be waiting now!
                } finally {
                    lock.unlock();
                }
            }
            out.flush();

            final Throwable t = reader.exception;
            if (null != t) {
                if (t instanceof IOException)
                    throw (IOException) t;
                else if (t instanceof RuntimeException)
                    throw (RuntimeException) t;
                throw (Error) t;
            }
        } finally {
            if (interrupted)
                Thread.currentThread().interrupt(); // restore
            Buffer.release(buffers);
        }
    }

    /**
     * Cancels the reader thread synchronously.
     * Synchronous cancellation of the reader thread is required so that a
     * re-entry to the cat(...) method by the same thread cannot concurrently
     * access the same shared buffers that an unfinished reader thread of a
     * previous call may still be using.
     */
    private static void cancel(final Future<?> result) {
        result.cancel(true);
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    result.get();
                    break;
                } catch (CancellationException cancelled) {
                    break;
                } catch (ExecutionException cannotHappen) {
                    throw new AssertionError(cannotHappen);
                } catch (InterruptedException interrupt) {
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted)
                Thread.currentThread().interrupt(); // restore
        }
    }

    /** A buffer for I/O. */
    private static final class Buffer {

        /**
         * Each entry in this queue holds a soft reference to an array
         * initialized with instances of this class.
         */
        static final Queue<Reference<Buffer[]>> queue
                = new ConcurrentLinkedQueue<>();

        static Buffer[] allocate() {
            {
                Reference<Buffer[]> reference;
                while (null != (reference = queue.poll())) {
                    final Buffer[] buffers = reference.get();
                    if (null != buffers)
                        return buffers;
                }
            }

            final Buffer[] buffers = new Buffer[FIFO_SIZE];
            for (int i = buffers.length; 0 <= --i; )
                buffers[i] = new Buffer();
            return buffers;
        }

        static void release(Buffer[] buffers) {
            //queue.push(new SoftReference<>(buffers));
            queue.add(new SoftReference<>(buffers));
        }

        /** The byte buffer used for reading and writing. */
        final byte[] buf = new byte[Store.BUFSIZE];

        /**
         * The actual number of bytes read into the buffer.
         * -1 represents end-of-file or {@link IOException}.
         */
        int read;
    }

    /** A factory for reader threads. */
    private static final class ReaderThreadFactory implements ThreadFactory {
        @Override public Thread newThread(Runnable r) {
            return new ReaderThread(r);
        }
    }

    /**
     * A pooled and cached daemon thread which runs tasks to read input streams.
     * You cannot instantiate this class.
     */
    @SuppressWarnings("PublicInnerClass")
    public static final class ReaderThread extends Thread {
        private ReaderThread(Runnable r) {
            super(ThreadGroups.getServerThreadGroup(), r, ReaderThread.class.getName());
            setDaemon(true);
        }
    }

    private Copy() { }
}
