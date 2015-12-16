package com.mbenabda.maven.plugins.generators.filesRegistry.generators.javapoet;

import com.mbenabda.maven.plugins.generators.filesRegistry.RegistryGenerationContext;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

public class RegistryClassCodeGenerator {

    public String generateCode(RegistryGenerationContext context) throws Exception {
        return asJavaCode(
                context.getRegistryPackageName(),
                new RegistryClassGenerator(context)
                        .generateClass(
                                context.getRegistrySimpleClassName(),
                                context.getFilesRootDirectory()
                        )
        );
    }

    private String asJavaCode(String registryPackageName, TypeSpec spec) {
        return JavaFile.builder(registryPackageName, spec)
                .build()
                .toString();
    }


}
