package com.mbenabda.maven.plugins.generators.filesRegistry.filters;

import org.junit.Test;

import java.nio.file.Paths;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class FilenameHasSuffixPredicateTest {
    @Test
    public void should_reject_files_that_do_not_have_the_expected_suffix() {
        assertFalse(
            acceptFilesWithSuffix(".txt")
                .apply(Paths.get("aFile.jpg"))
        );
    }

    @Test
    public void should_accept_files_that_have_the_expected_suffix() {
        assertTrue(
            acceptFilesWithSuffix(".txt")
                .apply(Paths.get("aFile.txt"))
        );
    }

    private FilenameHasSuffixPredicate acceptFilesWithSuffix(String suffix) {
        return new FilenameHasSuffixPredicate(suffix);
    }
}