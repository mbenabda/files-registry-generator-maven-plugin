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
    private final Path root;

    RegistryGeneratorDirectoryWorker(StringToIdentifierConverter stringToIdentifierConverter, Path root, Path pathToInputFiles, Predicate<Path> includedFileSpecification, String registryPackageName, String generatedClassName) {
        this.root = root;
        this.stringToIdentifierConverter = stringToIdentifierConverter;
        this.pathToInputFiles = pathToInputFiles;
        this.includedFileSpecification = includedFileSpecification;
        this.registryPackageName = registryPackageName;
        this.generatedClassName = generatedClassName;
    }

    @Override
    public TypeSpec call() throws Exception {
        Path dir = pathToInputFiles;
        Modifier[] modifiers = pathToInputFiles.equals(root)
                ? new Modifier[] {Modifier.PUBLIC, Modifier.FINAL}
                : new Modifier[] {Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL};
        TypeSpec.Builder currentClassBuilder = TypeSpec
                .classBuilder(generatedClassName)
                .addModifiers(modifiers);

        for(File child : dir.toFile().listFiles()) {
            Path childPath = child.toPath();

            if(child.isDirectory() && isOk(dir, childPath)) {
                RegistryGeneratorDirectoryWorker childWorker = new RegistryGeneratorDirectoryWorker(
                        new StringToIdentifierConverter(),
                        root,
                        childPath,
                        includedFileSpecification,
                        registryPackageName,
                        asClassName(childPath)
                );
                currentClassBuilder.addType(childWorker.call());
            } else if(includedFileSpecification.apply(childPath)) {
                FieldSpec field = FieldSpec
                        .builder(String.class, asFieldName(childPath))
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", asFieldValue(childPath))
                        .build();
                currentClassBuilder.addField(field);
            }
        }

        return currentClassBuilder.build();
    }

    private String asFieldValue(Path childPath) {
        String pathFromRoot = childPath.toAbsolutePath().toString()
                .replaceFirst(root.toAbsolutePath().toString(), "");

        pathFromRoot = pathFromRoot.startsWith("/")
                ? pathFromRoot.replaceFirst("/", "")
                : pathFromRoot;

        return FilenameUtils.removeExtension(pathFromRoot.toString());
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
