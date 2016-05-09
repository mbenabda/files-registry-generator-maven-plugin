package com.mbenabda.maven.plugins.generators.filesRegistry.filters;

import com.google.common.base.Predicate;

import java.nio.file.Path;

public class FilenameHasSuffixPredicate implements Predicate<Path> {
    private final String suffix;

    public FilenameHasSuffixPredicate(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public boolean apply(Path file) {
        return file.toString().endsWith(suffix);
    }
}
