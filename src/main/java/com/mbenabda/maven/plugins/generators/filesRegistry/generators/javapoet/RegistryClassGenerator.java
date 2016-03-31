package com.mbenabda.maven.plugins.generators.filesRegistry.generators.javapoet;

import com.mbenabda.maven.plugins.generators.filesRegistry.BuildFieldValueByRemovingExtension;
import com.mbenabda.maven.plugins.generators.filesRegistry.FieldValueMaker;
import com.mbenabda.maven.plugins.generators.filesRegistry.RegistryGenerationContext;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.io.FilenameUtils;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.nio.file.Path;

class RegistryClassGenerator {
    private static final Modifier[] ROOT_CLASS_MODIFIERS = new Modifier[] {Modifier.PUBLIC, Modifier.FINAL};
    private static final Modifier[] CLASS_MODIFIERS = new Modifier[] {Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL};

    private final RegistryGenerationContext context;
    private final FieldValueMaker fieldValueMaker;

    RegistryClassGenerator(RegistryGenerationContext context) {
        this.context = context;
        this.fieldValueMaker = new BuildFieldValueByRemovingExtension(context);
    }

    TypeSpec generateClass(String classSimpleName, Path classFeedDirectory) throws Exception {
        Modifier[] modifiers = classFeedDirectory.equals(context.getFilesRootDirectory())
                ? ROOT_CLASS_MODIFIERS
                : CLASS_MODIFIERS;

        TypeSpec.Builder currentClassBuilder = TypeSpec
                .classBuilder(classSimpleName)
                .addModifiers(modifiers);

        for(File child : classFeedDirectory.toFile().listFiles()) {
            Path childPath = child.toPath();

            if(child.isDirectory() && !isSpecialDirectory(classFeedDirectory, childPath)) {
                currentClassBuilder.addType(
                        new RegistryClassGenerator(context).generateClass(
                                asClassName(childPath),
                                childPath
                        )
                );
            } else if(context.getIncludedFilesSpecification().apply(childPath)) {
                currentClassBuilder.addField(
                        new FieldGenerator(context, fieldValueMaker)
                                .generateField(childPath)
                );
            }
        }

        return currentClassBuilder.build();
    }

    private boolean isSpecialDirectory(Path parent, Path evaluatedDirectory) {
        return evaluatedDirectory == parent
                || evaluatedDirectory.toAbsolutePath().equals(parent.toAbsolutePath()) // the . folder in unix systems
                || parent.getParent().toAbsolutePath().equals(evaluatedDirectory.toAbsolutePath()); // the .. folder in unix systems
    }


    private String asClassName(Path dir) {
        String directoryBaseName = FilenameUtils.getBaseName(dir.toFile().toString());
        return context
                .getJavaIdentifierNormalizer()
                .normalizeClassIdentifier(directoryBaseName);
    }

}
