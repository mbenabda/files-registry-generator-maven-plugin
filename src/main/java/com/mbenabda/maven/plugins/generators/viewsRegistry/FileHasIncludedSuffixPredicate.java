package com.mbenabda.maven.plugins.generators.viewsRegistry;

import com.google.common.base.Predicate;
import org.codehaus.plexus.util.FileUtils;

import java.nio.file.Path;

public class FileHasIncludedSuffixPredicate implements Predicate<Path> {
    private final String includedFileSuffix;

    public FileHasIncludedSuffixPredicate(String includedFileSuffix) {
        this.includedFileSuffix = includedFileSuffix;
    }

    @Override
    public boolean apply(Path file) {
        return FileUtils.filename(file.toString()).endsWith(includedFileSuffix);
    }
}
