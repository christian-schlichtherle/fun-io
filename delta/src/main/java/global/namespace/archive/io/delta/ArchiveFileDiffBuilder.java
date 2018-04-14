package global.namespace.archive.io.delta;

import global.namespace.archive.io.api.ArchiveFileSink;
import global.namespace.archive.io.api.ArchiveFileSource;
import global.namespace.archive.io.delta.model.DeltaModel;

import java.security.MessageDigest;
import java.util.Optional;

import static java.util.Optional.empty;

/**
 * A builder for an archive file diff.
 * The default message digest is SHA-1.
 *
 * @author Christian Schlichtherle
 */
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "ConstantConditions"})
public class ArchiveFileDiffBuilder {

    private Optional<MessageDigest> digest = empty();

    private Optional<ArchiveFileSource<?>> base = empty(), update = empty();

    ArchiveFileDiffBuilder() { }

    /** Returns this archive file diff builder with the given message digest. */
    public ArchiveFileDiffBuilder digest(final MessageDigest digest) {
        this.digest = Optional.of(digest);
        return this;
    }

    /**
     * Returns this archive file diff builder with the given source for reading the base archive file.
     * This is an alias for {@link #base(ArchiveFileSource)}.
     */
    public ArchiveFileDiffBuilder first(ArchiveFileSource<?> base) { return base(base); }

    /** Returns this archive file diff builder with the given source for reading the base archive file. */
    public ArchiveFileDiffBuilder base(final ArchiveFileSource<?> base) {
        this.base = Optional.of(base);
        return this;
    }

    /**
     * Returns this archive file diff builder with the given source for reading the update archive file.
     * This is an alias for {@link #update(ArchiveFileSource)}.
     */
    public ArchiveFileDiffBuilder second(ArchiveFileSource<?> update) { return update(update); }

    /** Returns this archive file diff builder with the given source for reading the update archive file. */
    public ArchiveFileDiffBuilder update(final ArchiveFileSource<?> update) {
        this.update = Optional.of(update);
        return this;
    }

    /**
     * Returns the delta model computed from the base and update archive file.
     * This is an alias for {@link #toModel()}.
     */
    public DeltaModel deltaModel() throws Exception { return toModel(); }

    /** Returns the delta model computed from the base and update archive file. */
    public DeltaModel toModel() throws Exception { return build().toModel(); }

    /** Writes the delta archive file computed from the base and update archive file to the given sink. */
    @SuppressWarnings("unchecked")
    public void to(ArchiveFileSink<?> delta) throws Exception { build().to(delta); }

    private ArchiveFileDiff build() {
        return create(digest.orElseGet(MessageDigests::sha1), base.get(), update.get());
    }

    private static ArchiveFileDiff create(MessageDigest digest,
                                          ArchiveFileSource<?> baseSource,
                                          ArchiveFileSource<?> updateSource) {
        return new ArchiveFileDiff() {

            MessageDigest digest() { return digest; }

            ArchiveFileSource<?> baseSource() { return baseSource; }

            ArchiveFileSource<?> updateSource() { return updateSource; }
        };
    }
}
