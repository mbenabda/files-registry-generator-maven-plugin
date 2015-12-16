package com.mbenabda.maven.plugins.generators.filesRegistry.generators.javapoet;

import com.mbenabda.maven.plugins.generators.filesRegistry.RegistryGenerationContext;
import com.squareup.javapoet.FieldSpec;
import org.apache.commons.io.FilenameUtils;

import javax.lang.model.element.Modifier;
import java.nio.file.Path;

class FieldGenerator {
    private final RegistryGenerationContext context;

    FieldGenerator(RegistryGenerationContext context) {
        this.context = context;
    }

    public FieldSpec generateField(Path fieldFile) {
        return FieldSpec
                .builder(String.class, asFieldName(fieldFile))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", asFieldValue(fieldFile))
                .build();
    }

    private String asFieldName(Path file) {
        return context
                .getJavaIdentifierNormalizer()
                .normalizeMemberIdentifier(
                        FilenameUtils.getBaseName(file.toFile().toString())
                );
    }

    private String asFieldValue(Path childPath) {
        String pathFromRoot = childPath.toAbsolutePath().toString()
                .replaceFirst(context.getFilesRootDirectory().toAbsolutePath().toString(), "");

        pathFromRoot = pathFromRoot.startsWith("/")
                ? pathFromRoot.replaceFirst("/", "")
                : pathFromRoot;

        return FilenameUtils.removeExtension(pathFromRoot.toString());
    }
}
