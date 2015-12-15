package com.mbenabda.maven.plugins.generators.viewsRegistry;

import com.google.common.base.Predicate;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.io.FilenameUtils;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.Callable;

class RegistryGeneratorDirectoryWorker implements Callable<TypeSpec> {
    private final Path pathToInputFiles;
    private final Predicate<Path> includedFileSpecification;
    private final String registryPackageName;
    private final String generatedClassName;
    private final StringToIdentifierConverter stringToIdentifierConverter;

    RegistryGeneratorDirectoryWorker(StringToIdentifierConverter stringToIdentifierConverter, Path pathToInputFiles, Predicate<Path> includedFileSpecification, String registryPackageName, String generatedClassName) {
        this.stringToIdentifierConverter = stringToIdentifierConverter;
        this.pathToInputFiles = pathToInputFiles;
        this.includedFileSpecification = includedFileSpecification;
        this.registryPackageName = registryPackageName;
        this.generatedClassName = generatedClassName;
    }

    @Override
    public TypeSpec call() throws Exception {
        Path dir = pathToInputFiles;
        TypeSpec.Builder currentClassBuilder = TypeSpec
                .classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

        for(File child : dir.toFile().listFiles()) {
            Path childPath = child.toPath();

            if(child.isDirectory() && isOk(dir, childPath)) {
                RegistryGeneratorDirectoryWorker childWorker = new RegistryGeneratorDirectoryWorker(
                        new StringToIdentifierConverter(), childPath,
                        includedFileSpecification,
                        registryPackageName,
                        asClassName(childPath)
                );
                currentClassBuilder.addType(childWorker.call());
            } else if(includedFileSpecification.apply(childPath)) {
                FieldSpec field = FieldSpec
                        .builder(String.class, asFieldName(childPath))
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .build();
                currentClassBuilder.addField(field);
            }
        }

        return currentClassBuilder.build();
    }

    private boolean isOk(Path parent, Path child) {
        return !parent.toAbsolutePath().equals(child.toAbsolutePath()) // the . folder in unix systems
                && !parent.toAbsolutePath().toString().startsWith(child.toAbsolutePath().toString()); // the .. folder in unix systems
    }

    private String asFieldName(Path file) {
        return stringToIdentifierConverter.asFieldIdentifier(
                FilenameUtils.getBaseName(file.toFile().toString())
        );
    }

    private String asClassName(Path dir) {
        return stringToIdentifierConverter.asClassIdentifier(
                FilenameUtils.getBaseName(dir.toFile().toString())
        );
    }

}
