package com.mbenabda.maven.plugins.generators.viewsRegistry;

import com.google.common.base.Predicate;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.nio.file.Path;

public class RegistryClassCodeGenerator {
    public String generateClassCode(Path pathToInputFiles, Predicate<Path> includedFileSpecification, String registryPackageName, String generatedClassName) throws Exception {
        return asJavaCode(
                registryPackageName,
                new RegistryGeneratorDirectoryWorker(
                        new StringToIdentifierConverter(),
                        pathToInputFiles,
                        pathToInputFiles,
                        includedFileSpecification,
                        registryPackageName,
                        generatedClassName
                ).call()
        );
    }

    private String asJavaCode(String registryPackageName, TypeSpec spec) {
        return JavaFile.builder(registryPackageName, spec)
                .build()
                .toString();
    }


}
