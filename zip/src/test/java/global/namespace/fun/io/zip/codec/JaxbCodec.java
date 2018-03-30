/*
 * Copyright (C) 2005-2013 Schlichtherle IT Services.
 * Copyright (C) 2013 Stimulus Software.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.fun.io.zip.codec;

import global.namespace.fun.io.zip.io.*;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

import static java.util.Objects.requireNonNull;

/**
 * A codec which encodes/decodes objects to/from XML with a
 * {@link Marshaller}/{@link Unmarshaller} derived from a {@link JAXBContext}.
 * <p>
 * This type of codec does <em>not</em> support encoding or decoding
 * {@code null}.
 *
 * @author Christian Schlichtherle (copied and edited from TrueLicense Core 2.3.1)
 */
@Immutable
public class JaxbCodec implements Codec {

    /** The JAXB context provided to the constructor. */
    protected final JAXBContext context;

    public JaxbCodec(final JAXBContext context) {
        this.context = requireNonNull(context);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link JaxbCodec}
     * returns {@code "application/xml; charset=utf-8"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc3023">RFC 3023</a>
     */
    @Override public String contentType() {
        return "application/xml; charset=utf-8";
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link JaxbCodec}
     * returns {@code "8bit"}.
     *
     * @see <a href="http://tools.ietf.org/html/rfc3023">RFC 3023</a>
     */
    @Override public String contentTransferEncoding() { return "8bit"; }

    @Override public void encode(final Sink sink, final @Nullable Object obj)
    throws Exception {

        class EncodeTask implements OutputTask<Void, JAXBException> {
            @Override public Void execute(OutputStream out) throws JAXBException {
                marshaller().marshal(obj, out);
                return null;
            }
        } // EncodeTask

        Sinks.execute(new EncodeTask()).on(sink);
    }

    /** Returns a new marshaller. */
    protected Marshaller marshaller() throws JAXBException {
        return context.createMarshaller();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decode(final Source source, final Type expected)
    throws Exception {

        class DecodeTask implements InputTask<T, JAXBException> {
            @Override public T execute(InputStream in) throws JAXBException {
                return (T) unmarshaller().unmarshal(in);
            }
        } // DecodeTask

        return Sources.execute(new DecodeTask()).on(source);
    }

    /** Returns a new unmarshaller. */
    protected Unmarshaller unmarshaller() throws JAXBException {
        return context.createUnmarshaller();
    }
}
