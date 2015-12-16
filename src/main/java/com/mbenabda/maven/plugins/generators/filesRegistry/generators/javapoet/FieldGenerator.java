package com.mbenabda.maven.plugins.generators.filesRegistry.generators.javapoet;

import com.mbenabda.maven.plugins.generators.filesRegistry.FieldValueMaker;
import com.mbenabda.maven.plugins.generators.filesRegistry.RegistryGenerationContext;
import com.squareup.javapoet.FieldSpec;
import org.apache.commons.io.FilenameUtils;

import javax.lang.model.element.Modifier;
import java.nio.file.Path;

class FieldGenerator {
    private final RegistryGenerationContext context;
    private final FieldValueMaker fieldValueMaker;

    FieldGenerator(RegistryGenerationContext context, FieldValueMaker fieldValueMaker) {
        this.context = context;
        this.fieldValueMaker = fieldValueMaker;
    }

    public FieldSpec generateField(Path fieldFile) {
        return FieldSpec
                .builder(String.class, asFieldName(fieldFile))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", fieldValueMaker.asFieldValue(fieldFile))
                .build();
    }

    private String asFieldName(Path file) {
        return context
                .getJavaIdentifierNormalizer()
                .normalizeMemberIdentifier(
                        FilenameUtils.getBaseName(file.toFile().toString())
                );
    }
}
