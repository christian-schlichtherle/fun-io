package global.namespace.fun.io.delta;

import global.namespace.fun.io.api.ArchiveSink;
import global.namespace.fun.io.api.ArchiveSource;
import global.namespace.fun.io.delta.model.DeltaModel;

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
public class ArchiveDiffBuilder {

    private Optional<MessageDigest> digest = empty();

    private Optional<ArchiveSource<?>> base = empty(), update = empty();

    ArchiveDiffBuilder() { }

    /** Returns this archive diff builder with the given message digest. */
    public ArchiveDiffBuilder digest(final MessageDigest digest) {
        this.digest = Optional.of(digest);
        return this;
    }

    /**
     * Returns this archive diff builder with the given source for reading the base archive file.
     * This is an alias for {@link #base(ArchiveSource)}.
     */
    public ArchiveDiffBuilder first(ArchiveSource<?> base) { return base(base); }

    /** Returns this archive diff builder with the given source for reading the base archive file. */
    public ArchiveDiffBuilder base(final ArchiveSource<?> base) {
        this.base = Optional.of(base);
        return this;
    }

    /**
     * Returns this archive diff builder with the given source for reading the update archive file.
     * This is an alias for {@link #update(ArchiveSource)}.
     */
    public ArchiveDiffBuilder second(ArchiveSource<?> update) { return update(update); }

    /** Returns this archive diff builder with the given source for reading the update archive file. */
    public ArchiveDiffBuilder update(final ArchiveSource<?> update) {
        this.update = Optional.of(update);
        return this;
    }

    /**
     * Returns the delta model computed from the base and update archive files.
     * This is an alias for {@link #toModel()}.
     */
    public DeltaModel deltaModel() throws Exception { return toModel(); }

    /** Returns the delta model computed from the base and update archive files. */
    public DeltaModel toModel() throws Exception { return build().toModel(); }

    /** Writes the delta archive file computed from the base and update archive files to the given sink. */
    @SuppressWarnings("unchecked")
    public void to(ArchiveSink<?> delta) throws Exception { build().to(delta); }

    private ArchiveDiff build() { return create(digest.orElseGet(MessageDigests::sha1), base.get(), update.get()); }

    private static ArchiveDiff create(MessageDigest digest,
                                      ArchiveSource<?> baseSource,
                                      ArchiveSource<?> updateSource) {
        return new ArchiveDiff() {

            MessageDigest digest() { return digest; }

            ArchiveSource<?> baseSource() { return baseSource; }

            ArchiveSource<?> updateSource() { return updateSource; }
        };
    }
}
