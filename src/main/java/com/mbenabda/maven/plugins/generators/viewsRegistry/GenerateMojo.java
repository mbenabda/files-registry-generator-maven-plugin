package com.mbenabda.maven.plugins.generators.viewsRegistry;


import com.google.common.base.Predicate;
import com.mbenabda.maven.plugins.generators.viewsRegistry.threaded.RegistryGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.nio.file.Path;
import java.nio.file.Paths;

@Mojo(name = "generate")
public class GenerateMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.outputDirectory}")
    private String outputDirectory;

    @Parameter(defaultValue = "${project.build.directory}")
    private String directory;

    @Parameter(defaultValue = "${project.build.sourceDirectory}")
    private String sourceDirectory;

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
        final Predicate<Path> includes = new FileHasIncludedSuffixPredicate(filesSuffix);

        if(pathToFiles.toFile().exists()) {
            getLog().info(String.format(
                    "The plugin will access the %s files in %s to generate the registry class %s at %s",
                    filesSuffix,
                    pathToFiles,
                    registryPackageName + "." + registryClassName,
                    registryDirectory()
            ));

            String javaCode = null;
            try {
                javaCode = new RegistryGenerator()
                .generateRegistryJavaCode(
                        pathToFiles,
                        includes,
                        registryPackageName,
                        registryClassName
                );
            } catch(Exception e) {
                throw new MojoExecutionException("", e);
            }

            getLog().info(javaCode);
        } else {
            throw new MojoFailureException("Path " + pathToFiles + " does not exist.");
        }
    }

    private String registryDirectory() {
        return "one of " + "\n" +
                "outputDirectory = " + outputDirectory + "\n" +
                "directory = " + directory + "\n" +
                "sourceDirectory = " + sourceDirectory;
    }

}
