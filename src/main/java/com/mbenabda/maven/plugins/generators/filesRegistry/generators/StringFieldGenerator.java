package com.mbenabda.maven.plugins.generators.filesRegistry.generators;

import com.mbenabda.maven.plugins.generators.filesRegistry.fieldValue.FieldValueMaker;
import com.mbenabda.maven.plugins.generators.filesRegistry.naming.JavaNamingConvention;
import com.squareup.javapoet.FieldSpec;
import org.apache.commons.io.FilenameUtils;

import javax.lang.model.element.Modifier;
import java.nio.file.Path;

class StringFieldGenerator {
    private final FieldValueMaker<String> fieldValueMaker;
    private final JavaNamingConvention javaNamingConvention;

    StringFieldGenerator(JavaNamingConvention javaNamingConvention, FieldValueMaker<String> fieldValueMaker) {
        this.javaNamingConvention = javaNamingConvention;
        this.fieldValueMaker = fieldValueMaker;
    }

    public FieldSpec createField(Path fieldFile) {
        return FieldSpec
                .builder(String.class, asFieldName(fieldFile))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", fieldValueMaker.asFieldValue(fieldFile))
                .build();
    }

    private String asFieldName(Path file) {
        return javaNamingConvention
                .asMemberIdentifier(
                        FilenameUtils.getBaseName(file.toFile().toString())
                );
    }
}
