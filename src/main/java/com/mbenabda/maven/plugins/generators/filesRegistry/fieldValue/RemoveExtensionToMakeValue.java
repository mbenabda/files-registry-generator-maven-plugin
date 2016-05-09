package com.mbenabda.maven.plugins.generators.filesRegistry.fieldValue;

import com.mbenabda.maven.plugins.generators.filesRegistry.RegistryGenerationContext;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;

public class RemoveExtensionToMakeValue implements FieldValueMaker<String> {
    private final RegistryGenerationContext context;

    public RemoveExtensionToMakeValue(RegistryGenerationContext context) {
        this.context = context;
    }

    @Override
    public String asFieldValue(Path filePath) {
        Path relativePathFromRootToChild = context.getFilesLocation().relativize(filePath);
        return FilenameUtils.removeExtension(relativePathFromRootToChild.toString());
    }
}
