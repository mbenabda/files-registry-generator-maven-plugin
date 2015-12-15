package com.mbenabda.maven.plugins.generators.viewsRegistry.threaded;

import com.google.common.base.Predicate;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import org.codehaus.plexus.util.FileUtils;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.Callable;

class RegistryGeneratorDirectoryWorker implements Callable<TypeSpec> {
    private final Path pathToInputFiles;
    private final Predicate<Path> includedFileSpecification;
    private final String registryPackageName;
    private final String generatedClassName;

    RegistryGeneratorDirectoryWorker(Path pathToInputFiles, Predicate<Path> includedFileSpecification, String registryPackageName, String generatedClassName) {
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
                        pathToInputFiles,
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
                && !child.toAbsolutePath().toString().contains(parent.toAbsolutePath().toString()); // the .. folder in unix systems
    }

    private String asClassName(Path dir) {
        String name = FileUtils.basename(dir.toFile().toString());
        String firstLetter = "" + name.charAt(0);
        return firstLetter.toUpperCase() + name.substring(1, name.length());
    }

    private String asFieldName(Path file) {
        return FileUtils.basename(FileUtils.removeExtension(file.toFile().toString())).toLowerCase();
    }

}
