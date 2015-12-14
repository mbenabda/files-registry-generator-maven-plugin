package com.mbenabda.maven.plugins.generators.viewsRegistry;


import com.google.common.base.Predicate;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
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

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Path pathToFiles = Paths.get(this.pathToFiles);
        final Predicate<Path> includes = new Predicate<Path>() {
            @Override
            public boolean apply(Path file) {
                return FileUtils.filename(file.toString()).endsWith(filesSuffix);
            }
        };

        String registryClassName = "FilesRegistry";

        getLog().info("The plugin will access the " + filesSuffix + " files in " + pathToFiles + " to generate the registry class " + registryClassName + " registry in package " + registryPackageName + " at " + registryDirectory());

        if(pathToFiles.toFile().exists()) {
            try {
                RegistryBuilderVisitor registry = new RegistryBuilderVisitor(
                        registryPackageName,
                        registryClassName,
                        includes,
                        getLog()
                );

                Files.walkFileTree(pathToFiles, registry);

                getLog().info("Generated java code : ");
                getLog().info(registry.getGeneratedJavaCode());
            } catch(IOException e) {
                getLog().error(e);
            }
        } else {
            getLog().error("unable to find " + this.pathToFiles);
        }
    }

    private String registryDirectory() {
        return "one of " + "\n" +
                "outputDirectory = " + outputDirectory + "\n" +
                "directory = " + directory + "\n" +
                "sourceDirectory = " + sourceDirectory;
    }

}
