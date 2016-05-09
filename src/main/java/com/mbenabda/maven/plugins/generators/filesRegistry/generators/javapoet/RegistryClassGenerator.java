package com.mbenabda.maven.plugins.generators.filesRegistry.generators.javapoet;

import com.mbenabda.maven.plugins.generators.filesRegistry.JavaNamingConvention;
import com.mbenabda.maven.plugins.generators.filesRegistry.RegistryGenerationContext;
import com.mbenabda.maven.plugins.generators.filesRegistry.RemoveExtensionToMakeValue;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.io.FilenameUtils;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.nio.file.Path;

class RegistryClassGenerator {
    private static final Modifier[] ROOT_CLASS_MODIFIERS = new Modifier[]{Modifier.PUBLIC, Modifier.FINAL};
    private static final Modifier[] CLASS_MODIFIERS = new Modifier[]{Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL};

    RegistryClassGenerator() {}

    TypeSpec createClass(RegistryGenerationContext context, String classSimpleName, Path classFeedDirectory) {
        Modifier[] modifiers = classFeedDirectory.equals(context.getFilesRootDirectory())
            ? ROOT_CLASS_MODIFIERS
            : CLASS_MODIFIERS;

        TypeSpec.Builder currentClassBuilder = TypeSpec
            .classBuilder(classSimpleName)
            .addModifiers(modifiers);

        for (File child : classFeedDirectory.toFile().listFiles()) {
            Path childPath = child.toPath();

            if (child.isDirectory() && !isSpecialDirectory(classFeedDirectory, childPath)) {
                currentClassBuilder.addType(
                    new RegistryClassGenerator().createClass(
                        context,
                        asClassName(context.getJavaNamingConvention(), childPath),
                        childPath
                    )
                );
            } else if (context.getIncludedFilesSpecification().apply(childPath)) {
                StringFieldGenerator fieldGenerator = new StringFieldGenerator(
                    context.getJavaNamingConvention(),
                    new RemoveExtensionToMakeValue(context)
                );

                currentClassBuilder.addField(
                    fieldGenerator.createField(childPath)
                );
            }
        }

        return currentClassBuilder.build();
    }

    private boolean isSpecialDirectory(Path parent, Path evaluatedDirectory) {
        return evaluatedDirectory == parent
            || evaluatedDirectory.toAbsolutePath().equals(parent.toAbsolutePath()) // the . folder in unix systems
            || evaluatedDirectory.toAbsolutePath().equals(parent.getParent().toAbsolutePath()); // the .. folder in unix systems
    }

    private String asClassName(JavaNamingConvention namingConvention, Path dir) {
        String directoryBaseName = FilenameUtils.getBaseName(dir.toFile().toString());
        return namingConvention
            .asSimpleClassName(directoryBaseName);
    }

}
