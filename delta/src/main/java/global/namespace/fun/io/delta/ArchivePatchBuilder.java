package global.namespace.fun.io.delta;

import global.namespace.fun.io.api.ArchiveSink;
import global.namespace.fun.io.api.ArchiveSource;

import java.util.Optional;

import static java.util.Optional.empty;

/**
 * A builder for an archive file patch.
 *
 * @author Christian Schlichtherle
 */
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "ConstantConditions"})
public class ArchivePatchBuilder {

    private Optional<ArchiveSource<?>> base = empty(), delta = empty();

    ArchivePatchBuilder() { }

    /**
     * Returns this archive patch builder with the given source for reading the base archive file.
     * This is an alias for {@link #base(ArchiveSource)}.
     */
    public ArchivePatchBuilder first(ArchiveSource<?> base) { return base(base); }

    /** Returns this archive patch builder with the given source for reading the base archive file. */
    public ArchivePatchBuilder base(final ArchiveSource<?> base) {
        this.base = Optional.of(base);
        return this;
    }

    /** Returns this archive patch builder with the given source for reading the delta archive file. */
    public ArchivePatchBuilder delta(final ArchiveSource<?> delta) {
        this.delta = Optional.of(delta);
        return this;
    }

    /** Writes the update archive file computed from the base and delta archive files to the given sink. */
    @SuppressWarnings("unchecked")
    public void to(ArchiveSink<?> update) throws Exception { build().to(update); }

    private ArchivePatch build() { return create(base.get(), delta.get()); }

    private static ArchivePatch create(ArchiveSource<?> baseSource, ArchiveSource<?> deltaSource) {
        return new ArchivePatch() {

            ArchiveSource<?> baseSource() { return baseSource; }

            ArchiveSource<?> deltaSource() { return deltaSource; }
        };
    }
}
