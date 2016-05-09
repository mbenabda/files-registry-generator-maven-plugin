package com.mbenabda.maven.plugins.generators.filesRegistry.generators;

import com.mbenabda.maven.plugins.generators.filesRegistry.RegistryGenerationContext;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.FileNotFoundException;

public class RegistryClassCodeGenerator {

    public String generateCode(RegistryGenerationContext context) throws FileNotFoundException {
        if(context.getFilesRootDirectory().toFile().exists()) {
            return asJavaCode(
                context.getRegistryPackageName(),
                new RegistryClassGenerator()
                    .createClass(
                        context,
                        context.getRegistrySimpleClassName(),
                        context.getFilesRootDirectory()
                    )
            );
        } else {
            throw new FileNotFoundException(context.getFilesRootDirectory().toString());
        }
    }

    private String asJavaCode(String registryPackageName, TypeSpec spec) {
        return JavaFile.builder(registryPackageName, spec)
                .build()
                .toString();
    }


}
