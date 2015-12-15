package com.mbenabda.maven.plugins.generators.viewsRegistry;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mojo(name = "generate")
public class GenerateMojo extends AbstractMojo {

    public static final String JAVA_FILE_EXTENSION = ".java";
    @Parameter(defaultValue = "${project.build.directory}/generated-source")
    private String targetRegistryPackageDirectory;

    @Parameter(required = true)
    private String pathToFiles;

    @Parameter(required = true)
    private String filesSuffix;

    @Parameter(defaultValue = "com.mbenabda.filesRegistry")
    private String registryPackageName;

    @Parameter(defaultValue = "FilesRegistry")
    private String registryClassName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Path pathToFiles = Paths.get(this.pathToFiles);

        if(pathToFiles.toFile().exists()) {
            getLog().info(String.format(
                    "The plugin will access the %s files in %s to generate the registry class %s at %s",
                    filesSuffix,
                    pathToFiles,
                    registryPackageName + "." + registryClassName,
                    targetRegistryClassDirectory()
            ));

            try {
                writeJavaCode(
                        new RegistryClassCodeGenerator()
                                .generateClassCode(
                                        pathToFiles,
                                        new FileHasIncludedSuffixPredicate(filesSuffix),
                                        registryPackageName,
                                        registryClassName
                                )
                );
            } catch(Exception e) {
                throw new MojoExecutionException("", e);
            }
        } else {
            throw new MojoFailureException("Path " + pathToFiles + " does not exist.");
        }
    }

    private void writeJavaCode(String javaCode) throws IOException {
        getLog().info(javaCode);
//        File javaFile = targetRegistryClassDirectory()
//                .resolve(registryClassName + JavaFileObject.Kind.SOURCE.extension)
//                .toFile();
//
//        FileUtils.writeStringToFile(javaFile, javaCode);
    }

    private Path targetRegistryClassDirectory() {
        Path targetRegistryClassDirectory = Paths
                .get(this.targetRegistryPackageDirectory)
                .resolve(packageNameAsPath(registryPackageName));

        if(!targetRegistryClassDirectory.toFile().exists()) {
            targetRegistryClassDirectory.toFile().mkdirs();
        }
        return targetRegistryClassDirectory;
    }

    private Path packageNameAsPath(String packageName) {
        String asPath = packageName.replaceAll("[.]", File.separator);
        return Paths.get(asPath);
    }

}
