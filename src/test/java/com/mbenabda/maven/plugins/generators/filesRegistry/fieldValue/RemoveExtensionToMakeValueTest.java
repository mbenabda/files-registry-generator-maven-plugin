package com.mbenabda.maven.plugins.generators.filesRegistry.fieldValue;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.mbenabda.maven.plugins.generators.filesRegistry.RegistryGenerationContext.iWantToGenerateARegistryClass;
import static org.junit.Assert.assertEquals;

public class RemoveExtensionToMakeValueTest {

    @Test
    public void should_make_value_from_filename_without_etension() {
        Path root = Paths.get("/");
        Path path = Paths.get("/aFile.txt");

        assertEquals(
            "aFile",
            testableValueMaker(root).asFieldValue(path)
        );
    }

    @Test
    public void should_prepend_filename_with_relative_path_from_root() {
        Path root = Paths.get("/path/to/files/");
        Path path = Paths.get("/path/to/files/nestedFolder/aFile.txt");

        assertEquals(
            "nestedFolder/aFile",
            testableValueMaker(root).asFieldValue(path)
        );
    }

    private RemoveExtensionToMakeValue testableValueMaker(Path root) {
        return new RemoveExtensionToMakeValue(
            iWantToGenerateARegistryClass()
                .fromTheFilesUnder(root)
                .please()
        );
    }


}