package global.namespace.fun.io.delta;

import global.namespace.fun.io.api.archive.ArchiveFileSink;
import global.namespace.fun.io.api.archive.ArchiveFileSource;

import java.util.Optional;

import static java.util.Optional.empty;

/**
 * A builder for an archive file patch.
 *
 * @author Christian Schlichtherle
 */
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "ConstantConditions"})
public class ArchiveFilePatchBuilder {

    private Optional<ArchiveFileSource<?>> base = empty(), delta = empty();

    ArchiveFilePatchBuilder() { }

    /**
     * Returns this archive file patch builder with the given source for reading the base archive file.
     * This is an alias for {@link #base(ArchiveFileSource)}.
     */
    public ArchiveFilePatchBuilder first(ArchiveFileSource<?> base) { return base(base); }

    /** Returns this archive file patch builder with the given source for reading the base archive file. */
    public ArchiveFilePatchBuilder base(final ArchiveFileSource<?> base) {
        this.base = Optional.of(base);
        return this;
    }

    /** Returns this archive file patch builder with the given source for reading the delta archive file. */
    public ArchiveFilePatchBuilder delta(final ArchiveFileSource<?> delta) {
        this.delta = Optional.of(delta);
        return this;
    }

    /** Writes the update archive file computed from the base and delta archive files to the given sink. */
    @SuppressWarnings("unchecked")
    public void to(ArchiveFileSink<?> update) throws Exception { build().to(update); }

    private ArchiveFilePatch build() { return create(base.get(), delta.get()); }

    private static ArchiveFilePatch create(ArchiveFileSource<?> baseSource, ArchiveFileSource<?> deltaSource) {
        return new ArchiveFilePatch() {

            ArchiveFileSource<?> baseSource() { return baseSource; }

            ArchiveFileSource<?> deltaSource() { return deltaSource; }
        };
    }
}
