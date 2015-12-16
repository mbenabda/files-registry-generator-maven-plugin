package com.mbenabda.maven.plugins.generators.filesRegistry;

import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;

public class BuildFieldValueByRemovingExtension implements FieldValueMaker {
    private final RegistryGenerationContext context;

    public BuildFieldValueByRemovingExtension(RegistryGenerationContext context) {
        this.context = context;
    }

    @Override
    public String asFieldValue(Path filePath) {
            Path relativePathFromRootToChild = context.getFilesRootDirectory().relativize(filePath);
            return FilenameUtils.removeExtension(relativePathFromRootToChild.toString());
    }
}
