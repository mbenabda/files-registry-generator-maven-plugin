package com.mbenabda.maven.plugins.generators.filesRegistry.generators;

import com.mbenabda.maven.plugins.generators.filesRegistry.RegistryGenerationContext;
import com.mbenabda.maven.plugins.generators.filesRegistry.fieldValue.RemoveExtensionToMakeValue;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.io.FilenameUtils;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;

class RegistryClassGenerator {
    private static final Modifier[] ROOT_CLASS_MODIFIERS = new Modifier[]{Modifier.PUBLIC, Modifier.FINAL};
    private static final Modifier[] CLASS_MODIFIERS = new Modifier[]{Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL};
    private final RegistryGenerationContext context;

    RegistryClassGenerator(RegistryGenerationContext context) {
        this.context = context;
    }

    TypeSpec createClass() {
        return createClass(
            ROOT_CLASS_MODIFIERS,
            context.getRegistrySimpleClassName(),
            acceptedFilesIn(context.getFilesLocation())
        );
    }

    private TypeSpec createClass(Modifier[] modifiers, String className, File[] files) {
        TypeSpec.Builder currentClassBuilder = TypeSpec
            .classBuilder(className)
            .addModifiers(modifiers);

        for (File child : files) {
            if (child.isDirectory()) {
                currentClassBuilder.addType(
                    createClass(
                        CLASS_MODIFIERS,
                        classNameFor(child),
                        acceptedFilesIn(child)
                    )
                );
            } else {
                currentClassBuilder.addField(
                    createField(child)
                );
            }
        }

        return currentClassBuilder.build();
    }

    private FieldSpec createField(File file) {
        return new StringFieldGenerator(
            context.getJavaNamingConvention(),
            new RemoveExtensionToMakeValue(context)
        ).createField(file.toPath());
    }

    private String classNameFor(File dir) {
        return context
            .getJavaNamingConvention()
            .asSimpleClassName(
                FilenameUtils.getBaseName(dir.toString())
            );
    }

    private File[] acceptedFilesIn(Path filesLocation) {
        return acceptedFilesIn(filesLocation.toFile());
    }

    private File[] acceptedFilesIn(File child) {
        return child.listFiles(
            new FileFilter() {
                public boolean accept(File file) {
                    return context.accepts(file);
                }
            }
        );
    }
}
